package com.example.ev2rinconsur.data.repository

import com.example.ev2rinconsur.data.local.user.UserDao
import com.example.ev2rinconsur.data.local.user.UserEntity

class UserRepository(
    private val userDao: UserDao
) {

    suspend fun login(email: String, password: String): Result<UserEntity> {
        val user = userDao.getByEmail(email)
        return if (user != null && user.password == password) {
            Result.success(user)
        } else {
            Result.failure(IllegalArgumentException("Credenciales invalidas"))
        }
    }

    suspend fun register(
        name: String,
        rut: String,
        email: String,
        rol: String,
        password: String
    ): Result<Long> {
        val exists = userDao.getByEmail(email) != null
        if (exists) {
            return Result.failure(IllegalStateException("El correo ya esta registrado"))
        }
        val id = userDao.insert(
            UserEntity(
                name = name,
                rut = rut,
                email = email,
                rol = rol,
                password = password
            )
        )
        return Result.success(id)
    }
}
