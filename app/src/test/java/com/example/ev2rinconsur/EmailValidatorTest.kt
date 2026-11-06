package com.example.ev2rinconsur

import com.example.ev2rinconsur.domain.validation.validateEmail
import org.junit.Assert.*
import org.junit.Test

class EmailValidatorTest {
    @Test fun corporativoValido()      = assertNull(validateEmail("colaborador@rinconsur.cl"))
    @Test fun mayusculasValido()       = assertNull(validateEmail("Colaborador@RinconSur.CL"))
    @Test fun espaciosAlrededor()      = assertNull(validateEmail("  colaborador@rinconsur.cl  "))
    @Test fun otroDominioRechazado()   = assertEquals("Usa tu correo @rinconsur.cl", validateEmail("x@gmail.com"))
    @Test fun dominioParecido()        = assertNotNull(validateEmail("x@rinconsur.cl.fake.com"))
    @Test fun sinArroba()              = assertEquals("Formato de correo inválido", validateEmail("colaborador.rinconsur.cl"))
    @Test fun vacioRechazado()         = assertEquals("El correo es obligatorio", validateEmail(""))
    @Test fun puntosSeguidosRechazado() = assertNotNull(validateEmail("a..b@rinconsur.cl"))
}