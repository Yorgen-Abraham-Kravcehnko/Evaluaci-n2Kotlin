package com.example.ev2rinconsur.ui.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ev2rinconsur.data.local.registro.RegistroAccesoEntity
import com.example.ev2rinconsur.ui.components.AppTopBar
import com.example.ev2rinconsur.ui.theme.AccentGreen
import com.example.ev2rinconsur.ui.theme.BackgroundLight
import com.example.ev2rinconsur.ui.theme.DangerRed
import com.example.ev2rinconsur.ui.theme.PrimaryBlue
import com.example.ev2rinconsur.ui.theme.TextMuted
import com.example.ev2rinconsur.ui.viewmodel.ConteoViewModel

@Composable
fun ConteoCrudScreen(
    vm: ConteoViewModel,
    onBack: () -> Unit,
    onFinishTurno: () -> Unit
) {
    val state by vm.uiState.collectAsStateWithLifecycle()
    val registros by vm.registros.collectAsStateWithLifecycle()
    val fechaHoy = vm.obtenerFechaHoy()

    val guardadosTurnoActual = registros
        .filter {
            it.fecha == fechaHoy &&
                it.acceso == state.accesoActivo &&
                it.turno == state.turnoActivo &&
                it.id !in state.eliminadosPendientes
        }
        .map { state.editadosPendientes[it.id] ?: it }

    val nuevosTurnoActual = state.nuevosPendientes.filter {
        it.acceso == state.accesoActivo && it.turno == state.turnoActivo
    }

    val registrosAccesoActual = nuevosTurnoActual + guardadosTurnoActual

    val totalTurno = registrosAccesoActual.size
    val compraronTurno = registrosAccesoActual.count { it.compro }
    val transitoTurno = totalTurno - compraronTurno

    val intentarSalir = {
        if (state.hayCambiosSinGuardar) {
            vm.mostrarModalSalirSinGuardar(true)
        } else {
            onBack()
        }
    }

    BackHandler {
        when {
            state.registroEnEdicion != null -> vm.cerrarModalEditar()
            state.registroAEliminar != null -> vm.cerrarModalEliminar()
            state.mostrarResumenFinal -> vm.mostrarResumenTurno(false)
            state.mostrarAlertaSalirSinGuardar -> vm.mostrarModalSalirSinGuardar(false)
            else -> intentarSalir()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                AppTopBar(title = state.accesoActivo, onBack = intentarSalir)
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BackgroundLight)
                    .padding(innerPadding)
                    .padding(12.dp)
            ) {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Contados: $totalTurno", fontWeight = FontWeight.Bold)
                        Text(
                            "Compraron: $compraronTurno",
                            color = AccentGreen,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Tránsito: $transitoTurno",
                            color = TextMuted,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        PanelChipsPerfil(
                            compro = state.comproSeleccionado,
                            sexo = state.sexoSeleccionado,
                            rangoEtario = state.rangoEtarioSeleccionado,
                            onComproChange = vm::onComproChange,
                            onSexoChange = vm::onSexoChange,
                            onRangoEtarioChange = vm::onRangoEtarioChange
                        )

                        Spacer(Modifier.height(6.dp))

                        val btnColor = if (state.comproSeleccionado) AccentGreen else PrimaryBlue

                        Button(
                            onClick = vm::registrarAcceso,
                            colors = ButtonDefaults.buttonColors(containerColor = btnColor),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "+ REGISTRAR ACCESO",
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "REGISTROS DEL TURNO ACTUAL (${registrosAccesoActual.size})",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextMuted,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(4.dp))

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(registrosAccesoActual, key = { it.id }) { reg ->
                        val esPendiente = reg.id < 0 || reg.id in state.editadosPendientes
                        RegistroItemCard(
                            registro = reg,
                            esPendiente = esPendiente,
                            onEdit = { vm.abrirModalEditar(reg) },
                            onDelete = { vm.abrirModalEliminar(reg) }
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { vm.guardarCambiosSesion() },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Guardar",
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Button(
                        onClick = { vm.mostrarResumenTurno(true) },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Finalizar Turno")
                    }
                }
            }
        }

        if (state.registroEnEdicion != null) {
            val editando = state.registroEnEdicion!!
            var editCompro by remember(editando.id) { mutableStateOf(editando.compro) }
            var editSexo by remember(editando.id) { mutableStateOf(editando.sexo) }
            var editEdad by remember(editando.id) { mutableStateOf(editando.rangoEtario) }

            ModalOverlayContainer {
                Text(
                    text = "Editar Registro",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryBlue
                )
                Spacer(Modifier.height(10.dp))

                PanelChipsPerfil(
                    compro = editCompro,
                    sexo = editSexo,
                    rangoEtario = editEdad,
                    onComproChange = { editCompro = it },
                    onSexoChange = { editSexo = it },
                    onRangoEtarioChange = { editEdad = it }
                )

                Spacer(Modifier.height(14.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = vm::cerrarModalEditar,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancelar")
                    }
                    Button(
                        onClick = { vm.guardarEdicion(editCompro, editSexo, editEdad) },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Guardar Cambios")
                    }
                }
            }
        }

        if (state.registroAEliminar != null) {
            val aBorrar = state.registroAEliminar!!
            ModalOverlayContainer {
                Text(
                    text = "Confirmar eliminación",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = DangerRed
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "¿Estás seguro de que deseas eliminar este registro de las ${aBorrar.hora} (${aBorrar.sexo}, ${aBorrar.rangoEtario})?",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = vm::cerrarModalEliminar,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancelar")
                    }
                    Button(
                        onClick = vm::confirmarEliminacion,
                        colors = ButtonDefaults.buttonColors(containerColor = DangerRed),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Eliminar", color = Color.White)
                    }
                }
            }
        }

        if (state.mostrarResumenFinal) {
            val todosActivosHoy = state.nuevosPendientes + registros
                .filter {
                    it.fecha == fechaHoy &&
                        it.id !in state.eliminadosPendientes
                }
                .map { state.editadosPendientes[it.id] ?: it }
            val totalCierre = todosActivosHoy.size
            val compraronCierre = todosActivosHoy.count { it.compro }
            val transitoCierre = totalCierre - compraronCierre

            ModalOverlayContainer {
                Text(
                    text = "¿Seguro que deseas finalizar el turno?",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryBlue
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Al finalizar el turno se archivarán los registros en Turnos Anteriores y el contador de hoy se reiniciará a 0:",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
                Spacer(Modifier.height(10.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = BackgroundLight)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text("Acceso activo: ${state.accesoActivo}", style = MaterialTheme.typography.bodySmall)
                        Text("Turno: ${state.turnoActivo}", style = MaterialTheme.typography.bodySmall)
                        Text(
                            "Total flujo del turno a cerrar: $totalCierre",
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Clientes que compraron: $compraronCierre",
                            color = AccentGreen,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text("Clientes en tránsito: $transitoCierre")
                    }
                }
                Spacer(Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { vm.mostrarResumenTurno(false) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Seguir Contando")
                    }
                    Button(
                        onClick = {
                            vm.mostrarResumenTurno(false)
                            onFinishTurno()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentGreen),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Finalizar Turno")
                    }
                }
            }
        }

        if (state.mostrarAlertaSalirSinGuardar) {
            ModalOverlayContainer {
                Text(
                    text = "¿Seguro de salir sin guardar?",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = DangerRed
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Tienes cambios sin guardar en esta sesión. Si sales ahora sin presionar 'Guardar', estos cambios se descartarán.",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { vm.mostrarModalSalirSinGuardar(false) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancelar")
                    }
                    Button(
                        onClick = {
                            vm.descartarCambiosSesion()
                            onBack()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = DangerRed),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Salir sin Guardar", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
private fun PanelChipsPerfil(
    compro: Boolean,
    sexo: String,
    rangoEtario: String,
    onComproChange: (Boolean) -> Unit,
    onSexoChange: (String) -> Unit,
    onRangoEtarioChange: (String) -> Unit
) {
    val sexos = listOf("Hombre", "Mujer", "Otro")
    val edades = listOf("Adolescente", "Joven", "Adulto", "Tercera Edad")

    Text(
        text = "1. ¿Realizó Compra?",
        style = MaterialTheme.typography.labelMedium,
        color = TextMuted,
        fontWeight = FontWeight.Bold
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = !compro,
            onClick = { onComproChange(false) },
            label = { Text("Solo Tránsito") },
            modifier = Modifier.weight(1f)
        )
        FilterChip(
            selected = compro,
            onClick = { onComproChange(true) },
            label = { Text("Compró") },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = AccentGreen.copy(alpha = 0.2f)
            ),
            modifier = Modifier.weight(1f)
        )
    }

    Text(
        text = "2. Sexo Estimado",
        style = MaterialTheme.typography.labelMedium,
        color = TextMuted,
        fontWeight = FontWeight.Bold
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        sexos.forEach { opcionSexo ->
            FilterChip(
                selected = sexo == opcionSexo,
                onClick = { onSexoChange(opcionSexo) },
                label = { Text(opcionSexo) },
                modifier = Modifier.weight(1f)
            )
        }
    }

    Text(
        text = "3. Rango Etario",
        style = MaterialTheme.typography.labelMedium,
        color = TextMuted,
        fontWeight = FontWeight.Bold
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        edades.take(2).forEach { opcionEdad ->
            FilterChip(
                selected = rangoEtario == opcionEdad,
                onClick = { onRangoEtarioChange(opcionEdad) },
                label = { Text(opcionEdad) },
                modifier = Modifier.weight(1f)
            )
        }
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        edades.drop(2).forEach { opcionEdad ->
            FilterChip(
                selected = rangoEtario == opcionEdad,
                onClick = { onRangoEtarioChange(opcionEdad) },
                label = { Text(opcionEdad) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun ModalOverlayContainer(
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.55f))
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                content()
            }
        }
    }
}

@Composable
private fun RegistroItemCard(
    registro: RegistroAccesoEntity,
    esPendiente: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = if (registro.compro) "Compró" else "Solo Tránsito",
                        fontWeight = FontWeight.Bold,
                        color = if (registro.compro) AccentGreen else PrimaryBlue
                    )
                    if (esPendiente) {
                        Text(
                            text = "(Sin guardar)",
                            style = MaterialTheme.typography.labelSmall,
                            color = DangerRed,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                Text(
                    text = "${registro.sexo} · ${registro.rangoEtario} · ${registro.hora}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
            }
            Row {
                TextButton(onClick = onEdit) {
                    Text("Editar")
                }
                TextButton(onClick = onDelete) {
                    Text("Borrar", color = DangerRed)
                }
            }
        }
    }
}
