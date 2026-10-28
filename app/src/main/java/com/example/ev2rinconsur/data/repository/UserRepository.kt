package com.example.ev2rinconsur.data.repository

import com.example.ev2rinconsur.data.local.database.AppDatabase
import com.example.ev2rinconsur.data.local.user.UserDao
import com.example.ev2rinconsur.data.local.user.UserEntity

class UserRepository(
    private val userDao: UserDao
) {

    private suspend fun ensureSeedUsers() {
        if (userDao.count() == 0) {
            AppDatabase.seedUsersList().forEach { user ->
                if (userDao.getByEmail(user.email) == null) {
                    userDao.insert(user)
                }
            }
        }
    }

    suspend fun login(email: String, password: String): Result<UserEntity> {
        ensureSeedUsers()
        val user = userDao.getByEmail(email)
        return if (user != null && user.password == password) {
            Result.success(user)
        } else {
            Result.failure(IllegalArgumentException("Credenciales inválidas"))
        }
    }

    suspend fun register(
        name: String,
        rut: String,
        email: String,
        rol: String,
        password: String
    ): Result<Long> {
        ensureSeedUsers()
        val exists = userDao.getByEmail(email) != null
        if (exists) {
            return Result.failure(IllegalStateException("El correo ya está registrado"))
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
