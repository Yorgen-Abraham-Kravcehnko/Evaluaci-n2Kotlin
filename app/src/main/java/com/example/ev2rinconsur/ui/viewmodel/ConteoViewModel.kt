package com.example.ev2rinconsur.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    val registroEnEdicion: RegistroAccesoEntity? = null,
    val registroAEliminar: RegistroAccesoEntity? = null,
    val mostrarResumenFinal: Boolean = false,
    val mensajeConfirmacion: String? = null
)

class ConteoViewModel(
    private val repo: RegistroRepository
) : ViewModel() {

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
        "Acceso 2 (Conexion Hogar)",
        "Acceso 3 (Plaza Central)",
        "Acceso 4 (Estacionamiento -1)"
    )

    val turnosDisponibles = listOf(
        "Turno Manana (08:30 - 15:30)",
        "Turno Tarde (15:30 - 21:30)"
    )

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

    fun registrarAcceso() {
        val s = _uiState.value
        val ahora = Date()
        val fecha = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(ahora)
        val hora = SimpleDateFormat("HH:mm", Locale.getDefault()).format(ahora)

        viewModelScope.launch {
            repo.agregar(
                acceso = s.accesoActivo,
                turno = s.turnoActivo,
                compro = s.comproSeleccionado,
                sexo = s.sexoSeleccionado,
                rangoEtario = s.rangoEtarioSeleccionado,
                fecha = fecha,
                hora = hora
            )
            _uiState.update {
                it.copy(mensajeConfirmacion = "Registro guardado correctamente")
            }
        }
    }

    fun limpiarMensaje() {
        _uiState.update { it.copy(mensajeConfirmacion = null) }
    }

    fun abrirModalEditar(registro: RegistroAccesoEntity) {
        _uiState.update { it.copy(registroEnEdicion = registro) }
    }

    fun cerrarModalEditar() {
        _uiState.update { it.copy(registroEnEdicion = null) }
    }

    fun guardarEdicion(compro: Boolean, sexo: String, rangoEtario: String) {
        val actual = _uiState.value.registroEnEdicion ?: return
        viewModelScope.launch {
            repo.actualizar(
                actual.copy(
                    compro = compro,
                    sexo = sexo,
                    rangoEtario = rangoEtario
                )
            )
            _uiState.update {
                it.copy(
                    registroEnEdicion = null,
                    mensajeConfirmacion = "Registro actualizado"
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
        viewModelScope.launch {
            repo.eliminar(aBorrar)
            _uiState.update {
                it.copy(
                    registroAEliminar = null,
                    mensajeConfirmacion = "Registro eliminado"
                )
            }
        }
    }

    fun mostrarResumenTurno(mostrar: Boolean) {
        _uiState.update { it.copy(mostrarResumenFinal = mostrar) }
    }
}
