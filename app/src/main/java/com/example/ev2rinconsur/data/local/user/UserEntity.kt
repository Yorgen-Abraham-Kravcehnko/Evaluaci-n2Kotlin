package com.example.ev2rinconsur.data.local.user

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val name: String,
    val rut: String,
    val email: String,
    val rol: String,
    val password: String
)
