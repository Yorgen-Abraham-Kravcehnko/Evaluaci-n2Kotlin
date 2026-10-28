package com.example.ev2rinconsur.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ev2rinconsur.ui.components.AppTopBar
import com.example.ev2rinconsur.ui.components.SelectorDesplegable
import com.example.ev2rinconsur.ui.theme.BackgroundLight
import com.example.ev2rinconsur.ui.theme.PrimaryBlue
import com.example.ev2rinconsur.ui.viewmodel.AuthViewModel

@Composable
fun RegisterScreenVm(
    vm: AuthViewModel,
    onRegisteredNavigateLogin: () -> Unit,
    onGoLogin: () -> Unit
) {
    val state by vm.register.collectAsStateWithLifecycle()

    LaunchedEffect(state.success) {
        if (state.success) {
            vm.clearRegisterResult()
            onRegisteredNavigateLogin()
        }
    }

    RegisterScreen(
        name = state.name,
        rut = state.rut,
        email = state.email,
        rol = state.rol,
        pass = state.pass,
        confirm = state.confirm,
        nameError = state.nameError,
        rutError = state.rutError,
        emailError = state.emailError,
        passError = state.passError,
        confirmError = state.confirmError,
        canSubmit = state.canSubmit,
        isSubmitting = state.isSubmitting,
        errorMsg = state.errorMsg,
        onNameChange = vm::onNameChange,
        onRutChange = vm::onRutChange,
        onEmailChange = vm::onRegisterEmailChange,
        onRolChange = vm::onRolChange,
        onPassChange = vm::onRegisterPassChange,
        onConfirmChange = vm::onConfirmChange,
        onSubmit = vm::submitRegister,
        onGoLogin = onGoLogin
    )
}

@Composable
private fun RegisterScreen(
    name: String,
    rut: String,
    email: String,
    rol: String,
    pass: String,
    confirm: String,
    nameError: String?,
    rutError: String?,
    emailError: String?,
    passError: String?,
    confirmError: String?,
    canSubmit: Boolean,
    isSubmitting: Boolean,
    errorMsg: String?,
    onNameChange: (String) -> Unit,
    onRutChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onRolChange: (String) -> Unit,
    onPassChange: (String) -> Unit,
    onConfirmChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onGoLogin: () -> Unit
) {
    var showPass by remember { mutableStateOf(false) }
    var showConfirm by remember { mutableStateOf(false) }
    var expandedRol by remember { mutableStateOf(false) }
    val roles = listOf("Operador de Acceso", "Supervisor de Tienda", "Jefe de Seguridad")

    Scaffold(
        topBar = {
            AppTopBar(title = "Crear Usuario", onBack = onGoLogin)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundLight)
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = onNameChange,
                        label = { Text("Nombre Completo") },
                        leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null) },
                        trailingIcon = {
                            if (nameError != null) {
                                Icon(
                                    imageVector = Icons.Filled.Error,
                                    contentDescription = "Error en nombre",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        singleLine = true,
                        isError = nameError != null,
                        supportingText = {
                            if (nameError != null) {
                                Text(nameError, color = MaterialTheme.colorScheme.error)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = rut,
                        onValueChange = onRutChange,
                        label = { Text("RUT (Ej: 18765432-1)") },
                        leadingIcon = { Icon(Icons.Filled.Badge, contentDescription = null) },
                        trailingIcon = {
                            if (rutError != null) {
                                Icon(
                                    imageVector = Icons.Filled.Error,
                                    contentDescription = "Error en RUT",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        singleLine = true,
                        isError = rutError != null,
                        supportingText = {
                            if (rutError != null) {
                                Text(rutError, color = MaterialTheme.colorScheme.error)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = email,
                        onValueChange = onEmailChange,
                        label = { Text("Correo Corporativo") },
                        leadingIcon = { Icon(Icons.Filled.Email, contentDescription = null) },
                        trailingIcon = {
                            if (emailError != null) {
                                Icon(
                                    imageVector = Icons.Filled.Error,
                                    contentDescription = "Error en correo",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        singleLine = true,
                        isError = emailError != null,
                        supportingText = {
                            if (emailError != null) {
                                Text(emailError, color = MaterialTheme.colorScheme.error)
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier.fillMaxWidth()
                    )

                    SelectorDesplegable(
                        label = "Rol / Cargo Asignado",
                        valorActual = rol,
                        opciones = roles,
                        expandido = expandedRol,
                        onToggleExpandido = { expandedRol = !expandedRol },
                        onSeleccionar = { opcion ->
                            onRolChange(opcion)
                            expandedRol = false
                        },
                        leadingIcon = Icons.Filled.Work
                    )

                    Spacer(Modifier.height(10.dp))

                    OutlinedTextField(
                        value = pass,
                        onValueChange = onPassChange,
                        label = { Text("Contraseña") },
                        leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null) },
                        singleLine = true,
                        isError = passError != null,
                        visualTransformation = if (showPass) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { showPass = !showPass }) {
                                Icon(
                                    imageVector = if (showPass) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                    contentDescription = null
                                )
                            }
                        },
                        supportingText = {
                            if (passError != null) {
                                Text(passError, color = MaterialTheme.colorScheme.error)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = confirm,
                        onValueChange = onConfirmChange,
                        label = { Text("Confirmar Contraseña") },
                        leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null) },
                        singleLine = true,
                        isError = confirmError != null,
                        visualTransformation = if (showConfirm) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { showConfirm = !showConfirm }) {
                                Icon(
                                    imageVector = if (showConfirm) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                    contentDescription = null
                                )
                            }
                        },
                        supportingText = {
                            if (confirmError != null) {
                                Text(confirmError, color = MaterialTheme.colorScheme.error)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(12.dp))

                    Button(
                        onClick = onSubmit,
                        enabled = canSubmit && !isSubmitting,
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (isSubmitting) {
                            CircularProgressIndicator(
                                strokeWidth = 2.dp,
                                color = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("Registrando...")
                        } else {
                            Text("Registrar Usuario")
                        }
                    }

                    AnimatedVisibility(visible = errorMsg != null) {
                        if (errorMsg != null) {
                            Text(
                                text = errorMsg,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            OutlinedButton(
                onClick = onGoLogin,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cancelar y Volver")
            }
        }
    }
}
