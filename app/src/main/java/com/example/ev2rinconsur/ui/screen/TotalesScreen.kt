package com.example.ev2rinconsur.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ev2rinconsur.ui.components.AppTopBar
import com.example.ev2rinconsur.ui.theme.AccentGreen
import com.example.ev2rinconsur.ui.theme.BackgroundLight
import com.example.ev2rinconsur.ui.theme.PrimaryBlue
import com.example.ev2rinconsur.ui.theme.TextMuted
import com.example.ev2rinconsur.ui.viewmodel.ConteoViewModel

@Composable
fun TotalesScreen(
    vm: ConteoViewModel,
    onBack: () -> Unit,
    onGoHistory: () -> Unit,
    onGoCrud: () -> Unit
) {
    val state by vm.uiState.collectAsStateWithLifecycle()
    val registros by vm.registros.collectAsStateWithLifecycle()
    val fechaHoy = vm.obtenerFechaHoy()
    val registrosHoy = registros.filter {
        it.fecha == fechaHoy && it.id !in state.idsCerradosEnTurno
    }

    val total = registrosHoy.size
    val compraron = registrosHoy.count { it.compro }
    val transito = total - compraron

    val pctCompra = if (total > 0) (compraron * 100) / total else 0
    val pctTransito = if (total > 0) (transito * 100) / total else 0

    Scaffold(
        topBar = {
            AppTopBar(title = "Registros Totales del Día", onBack = onBack)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundLight)
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "CONSOLIDADO DIARIO POR ACCESOS · $fechaHoy",
                style = MaterialTheme.typography.labelMedium,
                color = TextMuted,
                fontWeight = FontWeight.Bold
            )

            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "$total",
                        style = MaterialTheme.typography.headlineLarge,
                        color = PrimaryBlue,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Total Clientes Ingresados Hoy",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMuted
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ElevatedCard(modifier = Modifier.weight(1f)) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "$compraron",
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
                            .padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "$transito",
                            style = MaterialTheme.typography.headlineSmall,
                            color = TextMuted,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Solo Tránsito ($pctTransito%)",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted
                        )
                    }
                }
            }

            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Ingresos de Hoy por Acceso",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )

                    vm.accesosDisponibles.forEach { acceso ->
                        val cantidadAcceso = registrosHoy.count { it.acceso == acceso }
                        val progreso = if (total > 0) cantidadAcceso.toFloat() / total.toFloat() else 0f

                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = acceso,
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Text(
                                    text = "$cantidadAcceso personas",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = { progreso },
                                color = PrimaryBlue,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                            )
                        }
                    }
                }
            }

            OutlinedButton(
                onClick = onGoHistory,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ver Historial de Registros del Día")
            }

            Button(
                onClick = onGoCrud,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("+ Ir a Conteo en Vivo")
            }
        }
    }
}
