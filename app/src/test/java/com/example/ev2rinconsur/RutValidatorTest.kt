package com.example.ev2rinconsur

import com.example.ev2rinconsur.domain.validation.validateRut
import org.junit.Assert.*
import org.junit.Test

class RutValidatorTest {
    @Test fun rutSemillaValido()        = assertNull(validateRut("19876543-0"))
    @Test fun dvIncorrectoRechazado()   = assertNotNull(validateRut("19876543-2"))
    @Test fun conPuntosValido()         = assertNull(validateRut("12.345.678-5"))
    @Test fun dvKMinusculaValido()      = assertNull(validateRut("10000013-k"))
    @Test fun dvKIncorrectoRechazado()  = assertNotNull(validateRut("10000013-0"))
    @Test fun formatoInvalido()         = assertNotNull(validateRut("abc"))
    @Test fun vacioRechazado()          = assertNotNull(validateRut(""))
}