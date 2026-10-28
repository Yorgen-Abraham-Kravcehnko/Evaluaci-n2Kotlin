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
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.example.ev2rinconsur.ui.theme.DangerRed
import com.example.ev2rinconsur.ui.theme.PrimaryBlue
import com.example.ev2rinconsur.ui.theme.TextMuted
import com.example.ev2rinconsur.ui.viewmodel.AuthViewModel
import com.example.ev2rinconsur.ui.viewmodel.ConteoViewModel

@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel,
    conteoViewModel: ConteoViewModel,
    onBack: () -> Unit,
    onLogout: () -> Unit
) {
    val user by authViewModel.currentUser.collectAsStateWithLifecycle()
    val conteoState by conteoViewModel.uiState.collectAsStateWithLifecycle()
    val registros by conteoViewModel.registros.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            AppTopBar(title = "Perfil de Colaborador", onBack = onBack)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundLight)
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(12.dp))
            Icon(
                imageVector = Icons.Filled.AccountCircle,
                contentDescription = "Avatar Colaborador",
                tint = PrimaryBlue,
                modifier = Modifier.size(84.dp)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = user.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = user.rol,
                style = MaterialTheme.typography.bodyMedium,
                color = TextMuted
            )

            Spacer(Modifier.height(20.dp))

            ElevatedCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ProfileRow(label = "RUT:", value = user.rut)
                    HorizontalDivider()
                    ProfileRow(label = "Correo:", value = user.email)
                    HorizontalDivider()
                    ProfileRow(label = "Sucursal:", value = "Rincón Sur - Plaza Norte")
                    HorizontalDivider()
                    ProfileRow(label = "Turno Asignado:", value = conteoState.turnoActivo)
                    HorizontalDivider()
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Registros Acumulados:", color = TextMuted)
                        Text(
                            text = "${registros.size} registros",
                            fontWeight = FontWeight.Bold,
                            color = AccentGreen
                        )
                    }
                }
            }

            Spacer(Modifier.weight(1f))

            Button(
                onClick = onLogout,
                colors = ButtonDefaults.buttonColors(containerColor = DangerRed),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cerrar Sesión")
            }
        }
    }
}

@Composable
private fun ProfileRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = TextMuted)
        Text(text = value, fontWeight = FontWeight.SemiBold)
    }
}
