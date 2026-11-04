package com.example.ev2rinconsur.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File
import java.io.FileOutputStream

object FotoPerfilStorage {

    fun guardar(directorio: File, userId: Long, bitmap: Bitmap): String {
        directorio.listFiles { f -> f.name.startsWith("perfil_${userId}_") }
            ?.forEach { it.delete() }
        val archivo = File(directorio, "perfil_${userId}_${System.currentTimeMillis()}.jpg")
        FileOutputStream(archivo).use { bitmap.compress(Bitmap.CompressFormat.JPEG, 90, it) }
        return archivo.absolutePath
    }

    fun cargar(path: String?): Bitmap? =
        path?.let { if (File(it).exists()) BitmapFactory.decodeFile(it) else null }
}