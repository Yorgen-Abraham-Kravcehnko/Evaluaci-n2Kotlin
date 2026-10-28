package com.example.ev2rinconsur.ui.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ev2rinconsur.data.local.registro.RegistroAccesoEntity
import com.example.ev2rinconsur.ui.components.AppTopBar
import com.example.ev2rinconsur.ui.theme.AccentGreen
import com.example.ev2rinconsur.ui.theme.BackgroundLight
import com.example.ev2rinconsur.ui.theme.PrimaryBlue
import com.example.ev2rinconsur.ui.theme.TextMuted
import com.example.ev2rinconsur.ui.viewmodel.ConteoViewModel

private data class TarjetaTurno(
    val fecha: String,
    val turno: String,
    val total: Int,
    val compraron: Int,
    val transito: Int
)

private fun coincideFiltro(reg: RegistroAccesoEntity, rawQuery: String): Boolean {
    val query = rawQuery.trim().lowercase()
    if (query.isEmpty()) return true
    val textoCompra = if (reg.compro) "compró compro compra" else "solo tránsito transito"
    return reg.acceso.lowercase().contains(query) ||
        reg.fecha.lowercase().contains(query) ||
        reg.hora.lowercase().contains(query) ||
        reg.sexo.lowercase().contains(query) ||
        reg.rangoEtario.lowercase().contains(query) ||
        reg.turno.lowercase().contains(query) ||
        textoCompra.contains(query)
}

@Composable
fun HistorialScreen(
    vm: ConteoViewModel,
    onBack: () -> Unit
) {
    val state by vm.uiState.collectAsStateWithLifecycle()
    val registros by vm.registros.collectAsStateWithLifecycle()
    val fechaHoy = vm.obtenerFechaHoy()

    if (state.modoHistorialDiaActual) {
        val registrosHoyFiltrados = registros.filter { reg ->
            reg.fecha == fechaHoy &&
                reg.id !in state.idsCerradosEnTurno &&
                coincideFiltro(reg, state.filtroHistorial)
        }

        Scaffold(
            topBar = {
                AppTopBar(title = "Registros del Día", onBack = onBack)
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BackgroundLight)
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                OutlinedTextField(
                    value = state.filtroHistorial,
                    onValueChange = vm::onFiltroHistorialChange,
                    label = { Text("Filtrar por acceso, sexo, edad, turno u hora...") },
                    leadingIcon = {
                        Icon(Icons.Filled.Search, contentDescription = "Buscar")
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(12.dp))

                if (registrosHoyFiltrados.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No se encontraron registros con ese filtro",
                            color = TextMuted
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(registrosHoyFiltrados, key = { it.id }) { reg ->
                            TarjetaRegistroIndividual(reg = reg)
                        }
                    }
                }
            }
        }
        return
    }

    val hayTurnoSeleccionado = state.fechaTurnoSeleccionado.isNotEmpty()
    val registrosTurnosAnteriores = registros.filter {
        it.fecha != fechaHoy || it.id in state.idsCerradosEnTurno
    }

    BackHandler(enabled = hayTurnoSeleccionado) {
        vm.cerrarDetalleTurnoHistorial()
    }

    if (!hayTurnoSeleccionado) {
        val tarjetasTurnos = registrosTurnosAnteriores
            .groupBy { "${it.fecha}|${it.turno}" }
            .map { (_, lista) ->
                val primero = lista.first()
                val total = lista.size
                val compraron = lista.count { it.compro }
                TarjetaTurno(
                    fecha = primero.fecha,
                    turno = primero.turno,
                    total = total,
                    compraron = compraron,
                    transito = total - compraron
                )
            }

        Scaffold(
            topBar = {
                AppTopBar(title = "Historial de Turnos Anteriores", onBack = onBack)
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BackgroundLight)
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                Text(
                    text = "SELECCIONA UN TURNO PARA VER SU FLUJO Y REGISTROS",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextMuted,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(12.dp))

                if (tarjetasTurnos.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No hay turnos anteriores registrados",
                            color = TextMuted
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = tarjetasTurnos,
                            key = { "${it.fecha}-${it.turno}" }
                        ) { itemTurno ->
                            ElevatedCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        vm.seleccionarTurnoHistorial(
                                            fecha = itemTurno.fecha,
                                            turno = itemTurno.turno
                                        )
                                    }
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = itemTurno.fecha,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = PrimaryBlue
                                        )
                                        Card(
                                            colors = CardDefaults.cardColors(
                                                containerColor = PrimaryBlue.copy(alpha = 0.12f)
                                            )
                                        ) {
                                            Text(
                                                text = itemTurno.turno.substringBefore(" ("),
                                                style = MaterialTheme.typography.labelMedium,
                                                color = PrimaryBlue,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(
                                                    horizontal = 10.dp,
                                                    vertical = 4.dp
                                                )
                                            )
                                        }
                                    }

                                    Spacer(Modifier.height(6.dp))

                                    Text(
                                        text = "Horario: ${itemTurno.turno}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextMuted
                                    )

                                    Spacer(Modifier.height(10.dp))

                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(containerColor = BackgroundLight)
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 12.dp, vertical = 8.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = "Flujo Total: ${itemTurno.total}",
                                                style = MaterialTheme.typography.bodySmall,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = "Compraron: ${itemTurno.compraron}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = AccentGreen,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = "Tránsito: ${itemTurno.transito}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = TextMuted,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                    }

                                    Spacer(Modifier.height(8.dp))

                                    Text(
                                        text = "Toca para ver estadísticas y registros de este turno →",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = PrimaryBlue,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    } else {
        val fechaSel = state.fechaTurnoSeleccionado
        val turnoSel = state.nombreTurnoSeleccionado
        val registrosDeEsteTurno = registrosTurnosAnteriores.filter {
            it.fecha == fechaSel && it.turno == turnoSel
        }

        val totalTurno = registrosDeEsteTurno.size
        val compraronTurno = registrosDeEsteTurno.count { it.compro }
        val transitoTurno = totalTurno - compraronTurno
        val pctCompra = if (totalTurno > 0) (compraronTurno * 100) / totalTurno else 0
        val pctTransito = if (totalTurno > 0) (transitoTurno * 100) / totalTurno else 0

        val registrosTurnoFiltrados = registrosDeEsteTurno.filter { reg ->
            coincideFiltro(reg, state.filtroHistorial)
        }

        val tituloTurno = "${turnoSel.substringBefore(" (")} · $fechaSel"

        Scaffold(
            topBar = {
                AppTopBar(
                    title = tituloTurno,
                    onBack = vm::cerrarDetalleTurnoHistorial
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BackgroundLight)
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ElevatedCard(modifier = Modifier.weight(1f)) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "$totalTurno",
                                style = MaterialTheme.typography.headlineSmall,
                                color = PrimaryBlue,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Flujo Total",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMuted
                            )
                        }
                    }

                    ElevatedCard(modifier = Modifier.weight(1f)) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "$compraronTurno",
                                style = MaterialTheme.typography.headlineSmall,
                                color = AccentGreen,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Compraron ($pctCompra%)",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMuted
                            )
                        }
                    }

                    ElevatedCard(modifier = Modifier.weight(1f)) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "$transitoTurno",
                                style = MaterialTheme.typography.headlineSmall,
                                color = TextMuted,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Tránsito ($pctTransito%)",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMuted
                            )
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = state.filtroHistorial,
                    onValueChange = vm::onFiltroHistorialChange,
                    label = { Text("Filtrar por acceso, sexo, edad u hora...") },
                    leadingIcon = {
                        Icon(Icons.Filled.Search, contentDescription = "Buscar")
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(12.dp))

                if (registrosTurnoFiltrados.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No se encontraron registros en este turno con ese filtro",
                            color = TextMuted
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(registrosTurnoFiltrados, key = { it.id }) { reg ->
                            TarjetaRegistroIndividual(reg = reg)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TarjetaRegistroIndividual(reg: RegistroAccesoEntity) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${reg.fecha} · ${reg.hora}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (reg.compro) "Compró" else "Solo Tránsito",
                    color = if (reg.compro) AccentGreen else PrimaryBlue,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.height(4.dp))
            Text(
                text = reg.acceso,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "${reg.turno} · Perfil: ${reg.sexo}, ${reg.rangoEtario}",
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted
            )
        }
    }
}
