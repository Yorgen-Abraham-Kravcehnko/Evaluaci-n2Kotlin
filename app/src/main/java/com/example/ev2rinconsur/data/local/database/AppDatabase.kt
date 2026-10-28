package com.example.ev2rinconsur.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.ev2rinconsur.data.local.registro.RegistroAccesoDao
import com.example.ev2rinconsur.data.local.registro.RegistroAccesoEntity
import com.example.ev2rinconsur.data.local.user.UserDao
import com.example.ev2rinconsur.data.local.user.UserEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Database(
    entities = [UserEntity::class, RegistroAccesoEntity::class],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun registroAccesoDao(): RegistroAccesoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        private const val DB_NAME = "rincon_sur.db"

        fun obtenerFechaHoy(): String =
            SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())

        fun seedUsersList(): List<UserEntity> = listOf(
            UserEntity(
                name = "Sebastián Curi",
                rut = "19876543-2",
                email = "colaborador@rinconsur.cl",
                rol = "Operador de Acceso",
                password = "Password123!"
            ),
            UserEntity(
                name = "Luis Carvallo",
                rut = "20123456-7",
                email = "admin@rinconsur.cl",
                rol = "Supervisor de Tienda",
                password = "Admin123!"
            )
        )

        fun seedRegistrosList(): List<RegistroAccesoEntity> {
            val hoy = obtenerFechaHoy()
            return listOf(
                RegistroAccesoEntity(
                    acceso = "Acceso 1 (Puerta Norte Mall)",
                    turno = "Turno Mañana (08:30 - 15:30)",
                    compro = true,
                    sexo = "Mujer",
                    rangoEtario = "Adulto",
                    fecha = "25-10-2026",
                    hora = "09:40"
                ),
                RegistroAccesoEntity(
                    acceso = "Acceso 2 (Conexión Hogar)",
                    turno = "Turno Mañana (08:30 - 15:30)",
                    compro = false,
                    sexo = "Hombre",
                    rangoEtario = "Joven",
                    fecha = "25-10-2026",
                    hora = "11:15"
                ),
                RegistroAccesoEntity(
                    acceso = "Acceso 4 (Estacionamiento -1)",
                    turno = "Turno Mañana (08:30 - 15:30)",
                    compro = true,
                    sexo = "Otro",
                    rangoEtario = "Adolescente",
                    fecha = "25-10-2026",
                    hora = "13:20"
                ),
                RegistroAccesoEntity(
                    acceso = "Acceso 3 (Plaza Central)",
                    turno = "Turno Tarde (15:30 - 21:30)",
                    compro = true,
                    sexo = "Mujer",
                    rangoEtario = "Tercera Edad",
                    fecha = "25-10-2026",
                    hora = "16:45"
                ),
                RegistroAccesoEntity(
                    acceso = "Acceso 1 (Puerta Norte Mall)",
                    turno = "Turno Tarde (15:30 - 21:30)",
                    compro = false,
                    sexo = "Hombre",
                    rangoEtario = "Adulto",
                    fecha = "25-10-2026",
                    hora = "18:30"
                ),
                RegistroAccesoEntity(
                    acceso = "Acceso 2 (Conexión Hogar)",
                    turno = "Turno Mañana (08:30 - 15:30)",
                    compro = false,
                    sexo = "Mujer",
                    rangoEtario = "Joven",
                    fecha = "26-10-2026",
                    hora = "10:10"
                ),
                RegistroAccesoEntity(
                    acceso = "Acceso 1 (Puerta Norte Mall)",
                    turno = "Turno Mañana (08:30 - 15:30)",
                    compro = true,
                    sexo = "Hombre",
                    rangoEtario = "Adulto",
                    fecha = "26-10-2026",
                    hora = "12:15"
                ),
                RegistroAccesoEntity(
                    acceso = "Acceso 3 (Plaza Central)",
                    turno = "Turno Mañana (08:30 - 15:30)",
                    compro = true,
                    sexo = "Mujer",
                    rangoEtario = "Tercera Edad",
                    fecha = "26-10-2026",
                    hora = "14:05"
                ),
                RegistroAccesoEntity(
                    acceso = "Acceso 1 (Puerta Norte Mall)",
                    turno = "Turno Tarde (15:30 - 21:30)",
                    compro = true,
                    sexo = "Mujer",
                    rangoEtario = "Adulto",
                    fecha = "26-10-2026",
                    hora = "16:20"
                ),
                RegistroAccesoEntity(
                    acceso = "Acceso 4 (Estacionamiento -1)",
                    turno = "Turno Tarde (15:30 - 21:30)",
                    compro = false,
                    sexo = "Hombre",
                    rangoEtario = "Joven",
                    fecha = "26-10-2026",
                    hora = "17:50"
                ),
                RegistroAccesoEntity(
                    acceso = "Acceso 2 (Conexión Hogar)",
                    turno = "Turno Tarde (15:30 - 21:30)",
                    compro = false,
                    sexo = "Otro",
                    rangoEtario = "Adolescente",
                    fecha = "26-10-2026",
                    hora = "19:15"
                ),
                RegistroAccesoEntity(
                    acceso = "Acceso 1 (Puerta Norte Mall)",
                    turno = "Turno Tarde (15:30 - 21:30)",
                    compro = true,
                    sexo = "Mujer",
                    rangoEtario = "Adulto",
                    fecha = hoy,
                    hora = "16:10"
                ),
                RegistroAccesoEntity(
                    acceso = "Acceso 1 (Puerta Norte Mall)",
                    turno = "Turno Tarde (15:30 - 21:30)",
                    compro = false,
                    sexo = "Hombre",
                    rangoEtario = "Joven",
                    fecha = hoy,
                    hora = "16:35"
                ),
                RegistroAccesoEntity(
                    acceso = "Acceso 2 (Conexión Hogar)",
                    turno = "Turno Tarde (15:30 - 21:30)",
                    compro = true,
                    sexo = "Mujer",
                    rangoEtario = "Joven",
                    fecha = hoy,
                    hora = "16:50"
                ),
                RegistroAccesoEntity(
                    acceso = "Acceso 3 (Plaza Central)",
                    turno = "Turno Tarde (15:30 - 21:30)",
                    compro = true,
                    sexo = "Hombre",
                    rangoEtario = "Adulto",
                    fecha = hoy,
                    hora = "17:05"
                ),
                RegistroAccesoEntity(
                    acceso = "Acceso 4 (Estacionamiento -1)",
                    turno = "Turno Tarde (15:30 - 21:30)",
                    compro = false,
                    sexo = "Otro",
                    rangoEtario = "Adolescente",
                    fecha = hoy,
                    hora = "17:40"
                )
            )
        }

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DB_NAME
                )
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}
