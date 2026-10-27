package com.example.ev2rinconsur.ui.screen

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
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
import com.example.ev2rinconsur.ui.components.AppTopBar
import com.example.ev2rinconsur.ui.theme.AccentGreen
import com.example.ev2rinconsur.ui.theme.BackgroundLight
import com.example.ev2rinconsur.ui.theme.PrimaryBlue
import com.example.ev2rinconsur.ui.theme.TextMuted
import com.example.ev2rinconsur.ui.viewmodel.ConteoViewModel

@Composable
fun HistorialScreen(
    vm: ConteoViewModel,
    onBack: () -> Unit
) {
    val state by vm.uiState.collectAsStateWithLifecycle()
    val registros by vm.registros.collectAsStateWithLifecycle()

    val filtrados = registros.filter { reg ->
        val query = state.filtroHistorial.trim().lowercase()
        query.isEmpty() ||
            reg.acceso.lowercase().contains(query) ||
            reg.fecha.lowercase().contains(query) ||
            reg.rangoEtario.lowercase().contains(query) ||
            reg.turno.lowercase().contains(query)
    }

    Scaffold(
        topBar = {
            AppTopBar(title = "Registros Anteriores", onBack = onBack)
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
                label = { Text("Filtrar por fecha, acceso o rango etario...") },
                leadingIcon = {
                    Icon(Icons.Filled.Search, contentDescription = "Buscar")
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            if (filtrados.isEmpty()) {
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
                    items(filtrados, key = { it.id }) { reg ->
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
                                        text = if (reg.compro) "Compro" else "Solo Transito",
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
                }
            }
        }
    }
}
