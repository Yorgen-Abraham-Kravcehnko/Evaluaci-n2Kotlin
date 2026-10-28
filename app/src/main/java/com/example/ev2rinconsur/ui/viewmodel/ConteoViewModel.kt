package com.example.ev2rinconsur.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ev2rinconsur.data.local.database.AppDatabase
import com.example.ev2rinconsur.data.local.registro.RegistroAccesoEntity
import com.example.ev2rinconsur.data.repository.RegistroRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ConteoUiState(
    val accesoActivo: String = "Acceso 1 (Puerta Norte Mall)",
    val turnoActivo: String = "Turno Tarde (15:30 - 21:30)",
    val comproSeleccionado: Boolean = false,
    val sexoSeleccionado: String = "Hombre",
    val rangoEtarioSeleccionado: String = "Joven",
    val filtroHistorial: String = "",
    val modoHistorialDiaActual: Boolean = false,
    val fechaTurnoSeleccionado: String = "",
    val nombreTurnoSeleccionado: String = "",
    val registroEnEdicion: RegistroAccesoEntity? = null,
    val registroAEliminar: RegistroAccesoEntity? = null,
    val mostrarResumenFinal: Boolean = false,
    val mostrarAlertaSalirSinGuardar: Boolean = false,
    val nuevosPendientes: List<RegistroAccesoEntity> = emptyList(),
    val editadosPendientes: Map<Long, RegistroAccesoEntity> = emptyMap(),
    val eliminadosPendientes: Set<Long> = emptySet()
) {
    val hayCambiosSinGuardar: Boolean
        get() = nuevosPendientes.isNotEmpty() ||
            editadosPendientes.isNotEmpty() ||
            eliminadosPendientes.isNotEmpty()
}

class ConteoViewModel(
    private val repo: RegistroRepository
) : ViewModel() {

    private var tempIdCounter = -1L

    init {
        viewModelScope.launch {
            repo.ensureSeedRegistros()
        }
    }

    val registros: StateFlow<List<RegistroAccesoEntity>> =
        repo.registros.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    private val _uiState = MutableStateFlow(ConteoUiState())
    val uiState: StateFlow<ConteoUiState> = _uiState.asStateFlow()

    val accesosDisponibles = listOf(
        "Acceso 1 (Puerta Norte Mall)",
        "Acceso 2 (Conexión Hogar)",
        "Acceso 3 (Plaza Central)",
        "Acceso 4 (Estacionamiento -1)"
    )

    val turnosDisponibles = listOf(
        "Turno Mañana (08:30 - 15:30)",
        "Turno Tarde (15:30 - 21:30)"
    )

    fun obtenerFechaHoy(): String = AppDatabase.obtenerFechaHoy()

    fun onAccesoChange(acceso: String) {
        _uiState.update { it.copy(accesoActivo = acceso) }
    }

    fun onTurnoChange(turno: String) {
        _uiState.update { it.copy(turnoActivo = turno) }
    }

    fun onComproChange(compro: Boolean) {
        _uiState.update { it.copy(comproSeleccionado = compro) }
    }

    fun onSexoChange(sexo: String) {
        _uiState.update { it.copy(sexoSeleccionado = sexo) }
    }

    fun onRangoEtarioChange(rango: String) {
        _uiState.update { it.copy(rangoEtarioSeleccionado = rango) }
    }

    fun onFiltroHistorialChange(filtro: String) {
        _uiState.update { it.copy(filtroHistorial = filtro) }
    }

    fun abrirRegistrosDelDia() {
        _uiState.update {
            it.copy(
                modoHistorialDiaActual = true,
                fechaTurnoSeleccionado = "",
                nombreTurnoSeleccionado = "",
                filtroHistorial = ""
            )
        }
    }

    fun abrirHistorialTurnosAnteriores() {
        _uiState.update {
            it.copy(
                modoHistorialDiaActual = false,
                fechaTurnoSeleccionado = "",
                nombreTurnoSeleccionado = "",
                filtroHistorial = ""
            )
        }
    }

    fun seleccionarTurnoHistorial(fecha: String, turno: String) {
        _uiState.update {
            it.copy(
                fechaTurnoSeleccionado = fecha,
                nombreTurnoSeleccionado = turno,
                filtroHistorial = ""
            )
        }
    }

    fun cerrarDetalleTurnoHistorial() {
        _uiState.update {
            it.copy(
                fechaTurnoSeleccionado = "",
                nombreTurnoSeleccionado = "",
                filtroHistorial = ""
            )
        }
    }

    fun registrarAcceso() {
        val s = _uiState.value
        val ahora = Date()
        val fecha = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(ahora)
        val hora = SimpleDateFormat("HH:mm", Locale.getDefault()).format(ahora)
        val nuevoTemp = RegistroAccesoEntity(
            id = tempIdCounter--,
            acceso = s.accesoActivo,
            turno = s.turnoActivo,
            compro = s.comproSeleccionado,
            sexo = s.sexoSeleccionado,
            rangoEtario = s.rangoEtarioSeleccionado,
            fecha = fecha,
            hora = hora
        )

        _uiState.update {
            it.copy(nuevosPendientes = listOf(nuevoTemp) + it.nuevosPendientes)
        }
    }

    private suspend fun guardarPendientesEnBd(s: ConteoUiState): Set<Long> {
        val actualesEnBd = registros.value
        val nuevosIds = mutableSetOf<Long>()

        s.eliminadosPendientes.forEach { idBorrar ->
            actualesEnBd.find { it.id == idBorrar }?.let { entidad ->
                repo.eliminar(entidad)
            }
        }

        s.editadosPendientes.forEach { (id, entidadEditada) ->
            if (id > 0 && id !in s.eliminadosPendientes) {
                repo.actualizar(entidadEditada)
            }
        }

        s.nuevosPendientes.reversed().forEach { nuevo ->
            val idInsertado = repo.agregar(
                acceso = nuevo.acceso,
                turno = nuevo.turno,
                compro = nuevo.compro,
                sexo = nuevo.sexo,
                rangoEtario = nuevo.rangoEtario,
                fecha = nuevo.fecha,
                hora = nuevo.hora
            )
            nuevosIds.add(idInsertado)
        }

        return nuevosIds
    }

    fun guardarCambiosSesion() {
        val s = _uiState.value
        if (!s.hayCambiosSinGuardar) return

        viewModelScope.launch {
            guardarPendientesEnBd(s)
            _uiState.update {
                it.copy(
                    nuevosPendientes = emptyList(),
                    editadosPendientes = emptyMap(),
                    eliminadosPendientes = emptySet()
                )
            }
        }
    }

    fun descartarCambiosSesion() {
        _uiState.update {
            it.copy(
                nuevosPendientes = emptyList(),
                editadosPendientes = emptyMap(),
                eliminadosPendientes = emptySet(),
                mostrarAlertaSalirSinGuardar = false
            )
        }
    }

    fun mostrarModalSalirSinGuardar(mostrar: Boolean) {
        _uiState.update { it.copy(mostrarAlertaSalirSinGuardar = mostrar) }
    }

    fun abrirModalEditar(registro: RegistroAccesoEntity) {
        _uiState.update { it.copy(registroEnEdicion = registro) }
    }

    fun cerrarModalEditar() {
        _uiState.update { it.copy(registroEnEdicion = null) }
    }

    fun guardarEdicion(compro: Boolean, sexo: String, rangoEtario: String) {
        val actual = _uiState.value.registroEnEdicion ?: return
        val actualizado = actual.copy(
            compro = compro,
            sexo = sexo,
            rangoEtario = rangoEtario
        )

        _uiState.update { state ->
            if (actual.id < 0) {
                val nuevosActualizados = state.nuevosPendientes.map {
                    if (it.id == actual.id) actualizado else it
                }
                state.copy(
                    nuevosPendientes = nuevosActualizados,
                    registroEnEdicion = null
                )
            } else {
                state.copy(
                    editadosPendientes = state.editadosPendientes + (actual.id to actualizado),
                    registroEnEdicion = null
                )
            }
        }
    }

    fun abrirModalEliminar(registro: RegistroAccesoEntity) {
        _uiState.update { it.copy(registroAEliminar = registro) }
    }

    fun cerrarModalEliminar() {
        _uiState.update { it.copy(registroAEliminar = null) }
    }

    fun confirmarEliminacion() {
        val aBorrar = _uiState.value.registroAEliminar ?: return
        _uiState.update { state ->
            if (aBorrar.id < 0) {
                state.copy(
                    nuevosPendientes = state.nuevosPendientes.filterNot { it.id == aBorrar.id },
                    registroAEliminar = null
                )
            } else {
                state.copy(
                    eliminadosPendientes = state.eliminadosPendientes + aBorrar.id,
                    editadosPendientes = state.editadosPendientes - aBorrar.id,
                    registroAEliminar = null
                )
            }
        }
    }

    fun mostrarResumenTurno(mostrar: Boolean) {
        _uiState.update { it.copy(mostrarResumenFinal = mostrar) }
    }

}
