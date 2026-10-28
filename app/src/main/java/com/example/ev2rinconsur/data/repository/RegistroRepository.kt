package com.example.ev2rinconsur.data.repository

import com.example.ev2rinconsur.data.local.database.AppDatabase
import com.example.ev2rinconsur.data.local.registro.RegistroAccesoDao
import com.example.ev2rinconsur.data.local.registro.RegistroAccesoEntity
import kotlinx.coroutines.flow.Flow

class RegistroRepository(
    private val dao: RegistroAccesoDao
) {

    val registros: Flow<List<RegistroAccesoEntity>> = dao.getAll()

    suspend fun ensureSeedRegistros() {
        if (dao.count() == 0) {
            AppDatabase.seedRegistrosList().forEach { dao.insert(it) }
        }
    }

    suspend fun agregar(
        acceso: String,
        turno: String,
        compro: Boolean,
        sexo: String,
        rangoEtario: String,
        fecha: String,
        hora: String
    ): Long {
        return dao.insert(
            RegistroAccesoEntity(
                acceso = acceso,
                turno = turno,
                compro = compro,
                sexo = sexo,
                rangoEtario = rangoEtario,
                fecha = fecha,
                hora = hora
            )
        )
    }

    suspend fun actualizar(registro: RegistroAccesoEntity) {
        dao.update(registro)
    }

    suspend fun eliminar(registro: RegistroAccesoEntity) {
        dao.delete(registro)
    }
}
