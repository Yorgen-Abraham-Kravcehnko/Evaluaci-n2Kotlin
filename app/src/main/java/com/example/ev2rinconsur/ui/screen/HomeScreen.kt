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
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import com.example.ev2rinconsur.ui.theme.AccentGreen
import com.example.ev2rinconsur.ui.theme.BackgroundLight
import com.example.ev2rinconsur.ui.theme.PrimaryBlue
import com.example.ev2rinconsur.ui.theme.TextMuted
import com.example.ev2rinconsur.ui.viewmodel.AuthViewModel
import com.example.ev2rinconsur.ui.viewmodel.ConteoViewModel

@OptIn(ExperimentalMaterial3Api::class)
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

    val totalIngresos = registros.size
    val totalCompraron = registros.count { it.compro }
    val porcentajeCompra = if (totalIngresos > 0) (totalCompraron * 100) / totalIngresos else 0

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
                        text = "CONFIGURACION DEL TURNO ACTIVO",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextMuted,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(10.dp))

                    ExposedDropdownMenuBox(
                        expanded = expandedAcceso,
                        onExpandedChange = { expandedAcceso = !expandedAcceso }
                    ) {
                        OutlinedTextField(
                            value = uiState.accesoActivo,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Punto de Acceso") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedAcceso)
                            },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedAcceso,
                            onDismissRequest = { expandedAcceso = false }
                        ) {
                            conteoViewModel.accesosDisponibles.forEach { opcion ->
                                DropdownMenuItem(
                                    text = { Text(opcion) },
                                    onClick = {
                                        conteoViewModel.onAccesoChange(opcion)
                                        expandedAcceso = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(10.dp))

                    ExposedDropdownMenuBox(
                        expanded = expandedTurno,
                        onExpandedChange = { expandedTurno = !expandedTurno }
                    ) {
                        OutlinedTextField(
                            value = uiState.turnoActivo,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Jornada / Turno") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTurno)
                            },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedTurno,
                            onDismissRequest = { expandedTurno = false }
                        ) {
                            conteoViewModel.turnosDisponibles.forEach { opcion ->
                                DropdownMenuItem(
                                    text = { Text(opcion) },
                                    onClick = {
                                        conteoViewModel.onTurnoChange(opcion)
                                        expandedTurno = false
                                    }
                                )
                            }
                        }
                    }
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
                                    text = "$totalIngresos",
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = PrimaryBlue,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Ingresos Totales",
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
                                    text = "$totalCompraron",
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = AccentGreen,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Compraron ($porcentajeCompra%)",
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
                        Text("Ver Registros Totales del Dia")
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
