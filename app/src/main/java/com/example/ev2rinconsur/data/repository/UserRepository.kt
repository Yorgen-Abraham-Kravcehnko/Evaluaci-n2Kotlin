package com.example.ev2rinconsur.data.repository

import com.example.ev2rinconsur.domain.validation.PasswordHasher
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
                    val salt = PasswordHasher.generateSalt()
                    userDao.insert(
                        user.copy(
                            password = PasswordHasher.hash(user.password, salt),
                            salt = salt
                        )
                    )
                }
            }
        }
    }

    suspend fun login(email: String, password: String): Result<UserEntity> {
        ensureSeedUsers()
        val user = userDao.getByEmail(email)
        return if (user != null && PasswordHasher.verify(password, user.salt, user.password)) {
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
        if (userDao.getByEmail(email) != null) {
            return Result.failure(IllegalStateException("El correo ya está registrado"))
        }
        val salt = PasswordHasher.generateSalt()
        val id = userDao.insert(
            UserEntity(
                name = name,
                rut = rut,
                email = email,
                rol = rol,
                password = PasswordHasher.hash(password, salt),
                salt = salt
            )
        )
        return Result.success(id)
    }
}