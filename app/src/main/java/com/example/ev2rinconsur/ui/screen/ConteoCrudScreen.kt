package com.example.ev2rinconsur.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import kotlinx.coroutines.delay

@Composable
fun ConteoCrudScreen(
    vm: ConteoViewModel,
    onBack: () -> Unit,
    onFinishTurno: () -> Unit
) {
    val state by vm.uiState.collectAsStateWithLifecycle()
    val registros by vm.registros.collectAsStateWithLifecycle()

    val registrosAccesoActual = registros.filter { it.acceso == state.accesoActivo }
    val totalTurno = registrosAccesoActual.size
    val compraronTurno = registrosAccesoActual.count { it.compro }
    val transitoTurno = totalTurno - compraronTurno

    val sexos = listOf("Hombre", "Mujer", "Otro")
    val edades = listOf("Adolescente", "Joven", "Adulto", "Tercera Edad")

    LaunchedEffect(state.mensajeConfirmacion) {
        if (state.mensajeConfirmacion != null) {
            delay(2000)
            vm.limpiarMensaje()
        }
    }

    if (state.registroEnEdicion != null) {
        val editando = state.registroEnEdicion!!
        var editCompro by remember(editando.id) { mutableStateOf(editando.compro) }
        var editSexo by remember(editando.id) { mutableStateOf(editando.sexo) }
        var editEdad by remember(editando.id) { mutableStateOf(editando.rangoEtario) }

        AlertDialog(
            onDismissRequest = vm::cerrarModalEditar,
            title = { Text("Editar Registro #${editando.id}") },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("1. ¿Realizo Compra?", fontWeight = FontWeight.SemiBold)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = !editCompro,
                            onClick = { editCompro = false },
                            label = { Text("Solo Transito") }
                        )
                        FilterChip(
                            selected = editCompro,
                            onClick = { editCompro = true },
                            label = { Text("Compro") }
                        )
                    }

                    Text("2. Sexo Estimado", fontWeight = FontWeight.SemiBold)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        sexos.forEach { s ->
                            FilterChip(
                                selected = editSexo == s,
                                onClick = { editSexo = s },
                                label = { Text(s) }
                            )
                        }
                    }

                    Text("3. Rango Etario", fontWeight = FontWeight.SemiBold)
                    Column {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            edades.take(2).forEach { e ->
                                FilterChip(
                                    selected = editEdad == e,
                                    onClick = { editEdad = e },
                                    label = { Text(e) }
                                )
                            }
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            edades.drop(2).forEach { e ->
                                FilterChip(
                                    selected = editEdad == e,
                                    onClick = { editEdad = e },
                                    label = { Text(e) }
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { vm.guardarEdicion(editCompro, editSexo, editEdad) }
                ) {
                    Text("Guardar Cambios")
                }
            },
            dismissButton = {
                TextButton(onClick = vm::cerrarModalEditar) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (state.registroAEliminar != null) {
        val aBorrar = state.registroAEliminar!!
        AlertDialog(
            onDismissRequest = vm::cerrarModalEliminar,
            title = { Text("Confirmar eliminacion") },
            text = {
                Text("¿Seguro que deseas eliminar el registro de las ${aBorrar.hora} (${aBorrar.rangoEtario})?")
            },
            confirmButton = {
                TextButton(onClick = vm::confirmarEliminacion) {
                    Text("Eliminar", color = DangerRed)
                }
            },
            dismissButton = {
                TextButton(onClick = vm::cerrarModalEliminar) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (state.mostrarResumenFinal) {
        AlertDialog(
            onDismissRequest = { vm.mostrarResumenTurno(false) },
            title = { Text("Resumen del Turno") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Acceso: ${state.accesoActivo}")
                    Text("Turno: ${state.turnoActivo}")
                    Text("Total registrados en puerta: $totalTurno", fontWeight = FontWeight.Bold)
                    Text("Clientes que compraron: $compraronTurno")
                    Text("Clientes en transito: $transitoTurno")
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        vm.mostrarResumenTurno(false)
                        onFinishTurno()
                    }
                ) {
                    Text("Confirmar y Cerrar Turno")
                }
            },
            dismissButton = {
                TextButton(onClick = { vm.mostrarResumenTurno(false) }) {
                    Text("Seguir Contando")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            AppTopBar(title = state.accesoActivo, onBack = onBack)
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
                    Text("Compraron: $compraronTurno", color = AccentGreen, fontWeight = FontWeight.Bold)
                    Text("Transito: $transitoTurno", color = TextMuted, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(8.dp))

            AnimatedVisibility(visible = state.mensajeConfirmacion != null) {
                state.mensajeConfirmacion?.let { msg ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    ) {
                        Text(
                            text = msg,
                            color = AccentGreen,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }
            }

            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "1. ¿Realizo Compra?",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextMuted,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = !state.comproSeleccionado,
                            onClick = { vm.onComproChange(false) },
                            label = { Text("Solo Transito") },
                            modifier = Modifier.weight(1f)
                        )
                        FilterChip(
                            selected = state.comproSeleccionado,
                            onClick = { vm.onComproChange(true) },
                            label = { Text("Compro") },
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
                        sexos.forEach { s ->
                            FilterChip(
                                selected = state.sexoSeleccionado == s,
                                onClick = { vm.onSexoChange(s) },
                                label = { Text(s) },
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
                        edades.take(2).forEach { e ->
                            FilterChip(
                                selected = state.rangoEtarioSeleccionado == e,
                                onClick = { vm.onRangoEtarioChange(e) },
                                label = { Text(e) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        edades.drop(2).forEach { e ->
                            FilterChip(
                                selected = state.rangoEtarioSeleccionado == e,
                                onClick = { vm.onRangoEtarioChange(e) },
                                label = { Text(e) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(Modifier.height(6.dp))

                    val btnColor by animateColorAsState(
                        targetValue = if (state.comproSeleccionado) AccentGreen else PrimaryBlue,
                        label = "colorBotonRegistrar"
                    )

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
                text = "REGISTROS DE LA SESION (${registrosAccesoActual.size})",
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
                    RegistroItemCard(
                        registro = reg,
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
                    onClick = onBack,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Guardar y Salir")
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
}

@Composable
private fun RegistroItemCard(
    registro: RegistroAccesoEntity,
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
                Text(
                    text = if (registro.compro) "Compro" else "Solo Transito",
                    fontWeight = FontWeight.Bold,
                    color = if (registro.compro) AccentGreen else PrimaryBlue
                )
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
