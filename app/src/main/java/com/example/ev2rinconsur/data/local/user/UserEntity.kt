package com.example.ev2rinconsur.data.local.user

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    indices = [
        Index(value = ["email"], unique = true),
        Index(value = ["rut"], unique = true)
    ]
)

data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val name: String,
    val rut: String,
    val email: String,
    val rol: String,
    val password: String,
    val salt: String = "",
    val fotoPath: String? = null
)
