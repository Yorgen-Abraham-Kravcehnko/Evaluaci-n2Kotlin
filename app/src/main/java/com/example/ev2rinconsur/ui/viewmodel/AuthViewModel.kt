package com.example.ev2rinconsur.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ev2rinconsur.data.local.user.UserEntity
import com.example.ev2rinconsur.data.repository.UserRepository
import com.example.ev2rinconsur.domain.validation.validateConfirm
import com.example.ev2rinconsur.domain.validation.validateEmail
import com.example.ev2rinconsur.domain.validation.validateNameLettersOnly
import com.example.ev2rinconsur.domain.validation.validateRut
import com.example.ev2rinconsur.domain.validation.validateStrongPassword
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    val email: String = "colaborador@rinconsur.cl",
    val pass: String = "Password123!",
    val emailError: String? = null,
    val passError: String? = null,
    val isSubmitting: Boolean = false,
    val canSubmit: Boolean = true,
    val success: Boolean = false,
    val errorMsg: String? = null
)

data class RegisterUiState(
    val name: String = "",
    val rut: String = "",
    val email: String = "",
    val rol: String = "Operador de Acceso",
    val pass: String = "",
    val confirm: String = "",
    val nameError: String? = null,
    val rutError: String? = null,
    val emailError: String? = null,
    val passError: String? = null,
    val confirmError: String? = null,
    val isSubmitting: Boolean = false,
    val canSubmit: Boolean = false,
    val success: Boolean = false,
    val errorMsg: String? = null
)

class AuthViewModel(
    private val repository: UserRepository
) : ViewModel() {

    private val _login = MutableStateFlow(LoginUiState())
    val login: StateFlow<LoginUiState> = _login

    private val _register = MutableStateFlow(RegisterUiState())
    val register: StateFlow<RegisterUiState> = _register

    private val _currentUser = MutableStateFlow(
        UserEntity(
            id = 1L,
            name = "Sebastián Curi",
            rut = "19876543-2",
            email = "colaborador@rinconsur.cl",
            rol = "Operador de Acceso",
            password = ""
        )
    )
    val currentUser: StateFlow<UserEntity> = _currentUser

    fun onLoginEmailChange(value: String) {
        _login.update { it.copy(email = value, emailError = validateEmail(value), errorMsg = null) }
        recomputeLoginCanSubmit()
    }

    fun onLoginPassChange(value: String) {
        val err = if (value.isBlank()) "Ingresa tu contraseña" else null
        _login.update { it.copy(pass = value, passError = err, errorMsg = null) }
        recomputeLoginCanSubmit()
    }

    private fun recomputeLoginCanSubmit() {
        val s = _login.value
        val can = s.emailError == null && s.email.isNotBlank() && s.pass.isNotBlank()
        _login.update { it.copy(canSubmit = can) }
    }

    fun submitLogin() {
        val s = _login.value
        if (!s.canSubmit || s.isSubmitting) return
        viewModelScope.launch {
            _login.update { it.copy(isSubmitting = true, errorMsg = null, success = false) }
            delay(300)

            val result = repository.login(s.email.trim(), s.pass)
            _login.update {
                if (result.isSuccess) {
                    result.getOrNull()?.let { user -> _currentUser.value = user }
                    it.copy(isSubmitting = false, success = true, errorMsg = null)
                } else {
                    it.copy(
                        isSubmitting = false,
                        success = false,
                        errorMsg = result.exceptionOrNull()?.message ?: "Credenciales inválidas"
                    )
                }
            }
        }
    }

    fun clearLoginResult() {
        _login.update { it.copy(success = false, errorMsg = null) }
    }

    fun onNameChange(value: String) {
        _register.update {
            it.copy(name = value, nameError = validateNameLettersOnly(value), errorMsg = null)
        }
        recomputeRegisterCanSubmit()
    }

    fun onRutChange(value: String) {
        _register.update {
            it.copy(rut = value, rutError = validateRut(value), errorMsg = null)
        }
        recomputeRegisterCanSubmit()
    }

    fun onRegisterEmailChange(value: String) {
        _register.update {
            it.copy(email = value, emailError = validateEmail(value), errorMsg = null)
        }
        recomputeRegisterCanSubmit()
    }

    fun onRolChange(value: String) {
        _register.update { it.copy(rol = value) }
    }

    fun onRegisterPassChange(value: String) {
        _register.update { it.copy(pass = value, passError = validateStrongPassword(value)) }
        _register.update { it.copy(confirmError = validateConfirm(it.pass, it.confirm)) }
        recomputeRegisterCanSubmit()
    }

    fun onConfirmChange(value: String) {
        _register.update { it.copy(confirm = value, confirmError = validateConfirm(it.pass, value)) }
        recomputeRegisterCanSubmit()
    }

    private fun recomputeRegisterCanSubmit() {
        val s = _register.value
        val noErrors = listOf(
            s.nameError,
            s.rutError,
            s.emailError,
            s.passError,
            s.confirmError
        ).all { it == null }
        val filled = s.name.isNotBlank() &&
            s.rut.isNotBlank() &&
            s.email.isNotBlank() &&
            s.pass.isNotBlank() &&
            s.confirm.isNotBlank()
        _register.update { it.copy(canSubmit = noErrors && filled) }
    }

    fun submitRegister() {
        val s = _register.value
        if (!s.canSubmit || s.isSubmitting) return
        viewModelScope.launch {
            _register.update { it.copy(isSubmitting = true, errorMsg = null, success = false) }
            delay(400)

            val result = repository.register(
                name = s.name.trim(),
                rut = s.rut.trim(),
                email = s.email.trim(),
                rol = s.rol,
                password = s.pass
            )

            _register.update {
                if (result.isSuccess) {
                    it.copy(isSubmitting = false, success = true, errorMsg = null)
                } else {
                    it.copy(
                        isSubmitting = false,
                        success = false,
                        errorMsg = result.exceptionOrNull()?.message ?: "No se pudo registrar"
                    )
                }
            }
        }
    }

    fun clearRegisterResult() {
        _register.update { RegisterUiState() }
    }
}
