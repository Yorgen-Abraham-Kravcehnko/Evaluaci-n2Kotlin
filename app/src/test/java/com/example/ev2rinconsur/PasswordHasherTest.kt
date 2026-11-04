package com.example.ev2rinconsur

import com.example.ev2rinconsur.domain.validation.PasswordHasher
import org.junit.Assert.*
import org.junit.Test

class PasswordHasherTest {

    @Test fun hashCorrectoVerifica() {
        val salt = PasswordHasher.generateSalt()
        val hash = PasswordHasher.hash("Password123!", salt)
        assertTrue(PasswordHasher.verify("Password123!", salt, hash))
    }

    @Test fun contrasenaIncorrectaFalla() {
        val salt = PasswordHasher.generateSalt()
        val hash = PasswordHasher.hash("Password123!", salt)
        assertFalse(PasswordHasher.verify("password123!", salt, hash))
    }

    @Test fun mismoPasswordDistintoSaltDaDistintoHash() {
        assertNotEquals(
            PasswordHasher.hash("Password123!", PasswordHasher.generateSalt()),
            PasswordHasher.hash("Password123!", PasswordHasher.generateSalt())
        )
    }

    @Test fun formatoHex() {
        val salt = PasswordHasher.generateSalt()
        assertEquals(32, salt.length)
        assertEquals(64, PasswordHasher.hash("x", salt).length)
    }
}