package com.example.ev2rinconsur.data.local.registro

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "registros_acceso")
data class RegistroAccesoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val acceso: String,
    val turno: String,
    val compro: Boolean,
    val sexo: String,
    val rangoEtario: String,
    val fecha: String,
    val hora: String
)
