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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ev2rinconsur.ui.components.AppTopBar
import com.example.ev2rinconsur.ui.components.SelectorDesplegable
import com.example.ev2rinconsur.ui.theme.AccentGreen
import com.example.ev2rinconsur.ui.theme.BackgroundLight
import com.example.ev2rinconsur.ui.theme.PrimaryBlue
import com.example.ev2rinconsur.ui.theme.TextMuted
import com.example.ev2rinconsur.ui.viewmodel.AuthViewModel
import com.example.ev2rinconsur.ui.viewmodel.ConteoViewModel

@Composable
fun HomeScreen(
    authViewModel: AuthViewModel,
    conteoViewModel: ConteoViewModel,
    onGoProfile: () -> Unit,
    onGoCrud: () -> Unit,
    onGoTotals: () -> Unit,
    onGoHistory: () -> Unit
) {
    val currentUser by authViewModel.currentUser.collectAsStateWithLifecycle()
    val uiState by conteoViewModel.uiState.collectAsStateWithLifecycle()
    val registros by conteoViewModel.registros.collectAsStateWithLifecycle()

    val totalIngresosHoy = registros.size
    val totalCompraronHoy = registros.count { it.compro }
    val porcentajeCompraHoy = if (totalIngresosHoy > 0) (totalCompraronHoy * 100) / totalIngresosHoy else 0

    val registrosTurnoSeleccionado = registros.filter {
        it.acceso == uiState.accesoActivo && it.turno == uiState.turnoActivo
    }
    val contadosTurno = registrosTurnoSeleccionado.size
    val compraronTurno = registrosTurnoSeleccionado.count { it.compro }
    val transitoTurno = contadosTurno - compraronTurno

    var expandedAcceso by remember { mutableStateOf(false) }
    var expandedTurno by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Hola, ${currentUser.name}",
                onProfile = onGoProfile
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundLight)
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "CONFIGURACIÓN DEL TURNO ACTIVO",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextMuted,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(10.dp))

                    SelectorDesplegable(
                        label = "Punto de Acceso",
                        valorActual = uiState.accesoActivo,
                        opciones = conteoViewModel.accesosDisponibles,
                        expandido = expandedAcceso,
                        onToggleExpandido = {
                            expandedAcceso = !expandedAcceso
                            if (expandedAcceso) expandedTurno = false
                        },
                        onSeleccionar = { opcion ->
                            conteoViewModel.onAccesoChange(opcion)
                            expandedAcceso = false
                        }
                    )

                    Spacer(Modifier.height(10.dp))

                    SelectorDesplegable(
                        label = "Jornada / Turno",
                        valorActual = uiState.turnoActivo,
                        opciones = conteoViewModel.turnosDisponibles,
                        expandido = expandedTurno,
                        onToggleExpandido = {
                            expandedTurno = !expandedTurno
                            if (expandedTurno) expandedAcceso = false
                        },
                        onSeleccionar = { opcion ->
                            conteoViewModel.onTurnoChange(opcion)
                            expandedTurno = false
                        }
                    )

                    Spacer(Modifier.height(10.dp))

                    Text(
                        text = "En este acceso y turno hoy: $contadosTurno contados ($compraronTurno compraron · $transitoTurno tránsito)",
                        style = MaterialTheme.typography.bodySmall,
                        color = PrimaryBlue,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Button(
                onClick = onGoCrud,
                colors = ButtonDefaults.buttonColors(containerColor = AccentGreen),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
            ) {
                Text(
                    text = "+ INICIAR CONTEO DE ACCESO",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            ElevatedCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Flujo Acumulado Hoy",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "En Vivo",
                            style = MaterialTheme.typography.labelSmall,
                            color = PrimaryBlue,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ElevatedCard(modifier = Modifier.weight(1f)) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "$totalIngresosHoy",
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = PrimaryBlue,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Ingresos Hoy (4 Accesos)",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextMuted
                                )
                            }
                        }

                        ElevatedCard(modifier = Modifier.weight(1f)) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "$totalCompraronHoy",
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = AccentGreen,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Compraron ($porcentajeCompraHoy%)",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextMuted
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = onGoTotals,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Ver Registros Totales del Día")
                    }
                }
            }

            OutlinedButton(
                onClick = onGoHistory,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ver Historial de Turnos Anteriores")
            }
        }
    }
}
