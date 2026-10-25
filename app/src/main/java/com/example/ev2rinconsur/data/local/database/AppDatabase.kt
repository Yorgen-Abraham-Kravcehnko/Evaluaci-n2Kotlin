package com.example.ev2rinconsur.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.ev2rinconsur.data.local.registro.RegistroAccesoDao
import com.example.ev2rinconsur.data.local.registro.RegistroAccesoEntity
import com.example.ev2rinconsur.data.local.user.UserDao
import com.example.ev2rinconsur.data.local.user.UserEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [UserEntity::class, RegistroAccesoEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun registroAccesoDao(): RegistroAccesoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        private const val DB_NAME = "rincon_sur.db"

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DB_NAME
                )
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                val database = getInstance(context)
                                val userDao = database.userDao()
                                val registroDao = database.registroAccesoDao()

                                if (userDao.count() == 0) {
                                    val usuariosIniciales = listOf(
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
                                    usuariosIniciales.forEach { userDao.insert(it) }
                                }

                                if (registroDao.count() == 0) {
                                    val registrosIniciales = listOf(
                                        RegistroAccesoEntity(
                                            acceso = "Acceso 1 (Puerta Norte Mall)",
                                            turno = "Turno Manana (08:30 - 15:30)",
                                            compro = false,
                                            sexo = "Mujer",
                                            rangoEtario = "Joven",
                                            fecha = "26-10-2026",
                                            hora = "11:20"
                                        ),
                                        RegistroAccesoEntity(
                                            acceso = "Acceso 2 (Conexion Hogar)",
                                            turno = "Turno Manana (08:30 - 15:30)",
                                            compro = false,
                                            sexo = "Hombre",
                                            rangoEtario = "Adulto",
                                            fecha = "26-10-2026",
                                            hora = "12:15"
                                        ),
                                        RegistroAccesoEntity(
                                            acceso = "Acceso 1 (Puerta Norte Mall)",
                                            turno = "Turno Tarde (15:30 - 21:30)",
                                            compro = true,
                                            sexo = "Mujer",
                                            rangoEtario = "Adulto",
                                            fecha = "27-10-2026",
                                            hora = "16:10"
                                        ),
                                        RegistroAccesoEntity(
                                            acceso = "Acceso 3 (Plaza Central)",
                                            turno = "Turno Tarde (15:30 - 21:30)",
                                            compro = true,
                                            sexo = "Hombre",
                                            rangoEtario = "Joven",
                                            fecha = "27-10-2026",
                                            hora = "17:05"
                                        ),
                                        RegistroAccesoEntity(
                                            acceso = "Acceso 4 (Estacionamiento -1)",
                                            turno = "Turno Tarde (15:30 - 21:30)",
                                            compro = false,
                                            sexo = "Otro",
                                            rangoEtario = "Adolescente",
                                            fecha = "27-10-2026",
                                            hora = "17:40"
                                        )
                                    )
                                    registrosIniciales.forEach { registroDao.insert(it) }
                                }
                            }
                        }
                    })
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}
