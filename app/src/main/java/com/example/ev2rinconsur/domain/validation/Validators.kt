package com.example.ev2rinconsur.domain.validation

private const val DOMINIO_CORPORATIVO = "@rinconsur.cl"
private val EMAIL_REGEX = Regex("^[A-Za-z0-9_%+-]+(\\.[A-Za-z0-9_%+-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*\\.[A-Za-z]{2,}$")
fun validateEmail(email: String): String? {
    if (email.isBlank()) return "El correo es obligatorio"
    val limpio = email.trim().lowercase()
    if (!EMAIL_REGEX.matches(limpio)) return "Formato de correo inválido"
    return if (!limpio.endsWith(DOMINIO_CORPORATIVO))
        "Usa tu correo $DOMINIO_CORPORATIVO" else null
}

fun validateNameLettersOnly(name: String): String? {
    if (name.isBlank()) return "El nombre es obligatorio"
    val regex = Regex("^[A-Za-zÁÉÍÓÚÑáéíóúñ ]+$")
    return if (!regex.matches(name)) "Solo letras y espacios" else null
}

fun normalizeRut(rut: String): String = rut.trim().replace(".", "").uppercase()

fun calcularDvRut(cuerpo: String): Char {
    var suma = 0
    var multiplicador = 2
    for (digito in cuerpo.reversed()) {
        suma += digito.digitToInt() * multiplicador
        multiplicador = if (multiplicador == 7) 2 else multiplicador + 1
    }
    return when (val resto = 11 - suma % 11) {
        11 -> '0'
        10 -> 'K'
        else -> '0' + resto
    }
}

fun validateRut(rut: String): String? {
    if (rut.isBlank()) return "El RUT es obligatorio"
    val limpio = normalizeRut(rut)
    if (!Regex("^[0-9]{7,8}-[0-9K]$").matches(limpio)) return "Formato RUT: 12345678-9"
    val (cuerpo, dv) = limpio.split("-")
    return if (calcularDvRut(cuerpo) != dv[0]) "RUT inválido (dígito verificador)" else null
}

fun validateStrongPassword(pass: String): String? {
    if (pass.isBlank()) return "La contraseña es obligatoria"
    if (pass.length < 8) return "Mínimo 8 caracteres"
    if (!pass.any { it.isUpperCase() }) return "Debe incluir una mayúscula"
    if (!pass.any { it.isLowerCase() }) return "Debe incluir una minúscula"
    if (!pass.any { it.isDigit() }) return "Debe incluir un número"
    if (!pass.any { !it.isLetterOrDigit() }) return "Debe incluir un símbolo"
    if (pass.contains(' ')) return "No debe contener espacios"
    return null
}

fun validateConfirm(pass: String, confirm: String): String? {
    if (confirm.isBlank()) return "Confirma tu contraseña"
    return if (pass != confirm) "Las contraseñas no coinciden" else null
}
