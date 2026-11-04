package com.example.ev2rinconsur.data.repository

import com.example.ev2rinconsur.domain.validation.PasswordHasher
import com.example.ev2rinconsur.data.local.database.AppDatabase
import com.example.ev2rinconsur.data.local.user.UserDao
import com.example.ev2rinconsur.data.local.user.UserEntity
import com.example.ev2rinconsur.domain.validation.normalizeRut
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
        val user = userDao.getByEmail(email.trim().lowercase())
        return if (user != null && PasswordHasher.verify(password, user.salt, user.password)) {
            Result.success(user)
        } else {
            Result.failure(IllegalArgumentException("Credenciales inválidas"))
        }
    }

    suspend fun getById(id: Long): UserEntity? {
        ensureSeedUsers()
        return userDao.getById(id)
    }

    suspend fun register(
        name: String,
        rut: String,
        email: String,
        rol: String,
        password: String
    ): Result<Long> {
        ensureSeedUsers()
        val emailNorm = email.trim().lowercase()
        val rutNorm = normalizeRut(rut)
        if (userDao.getByEmail(emailNorm) != null) {
            return Result.failure(IllegalStateException("El correo ya está registrado"))
        }
        if (userDao.getByRut(rutNorm) != null) {
            return Result.failure(IllegalStateException("El RUT ya está registrado"))
        }
        val salt = PasswordHasher.generateSalt()
        val id = userDao.insert(
            UserEntity(
                name = name,
                rut = rutNorm,
                email = emailNorm,
                rol = rol,
                password = PasswordHasher.hash(password, salt),
                salt = salt
            )
        )
        return Result.success(id)
    }
}