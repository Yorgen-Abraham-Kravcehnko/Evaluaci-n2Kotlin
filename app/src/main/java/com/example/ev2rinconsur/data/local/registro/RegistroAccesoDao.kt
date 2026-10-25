package com.example.ev2rinconsur.data.local.registro

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface RegistroAccesoDao {

    @Query("SELECT * FROM registros_acceso ORDER BY id DESC")
    fun getAll(): Flow<List<RegistroAccesoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(registro: RegistroAccesoEntity): Long

    @Update
    suspend fun update(registro: RegistroAccesoEntity)

    @Delete
    suspend fun delete(registro: RegistroAccesoEntity)

    @Query("SELECT COUNT(*) FROM registros_acceso")
    suspend fun count(): Int
}
