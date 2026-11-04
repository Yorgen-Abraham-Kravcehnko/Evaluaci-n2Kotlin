package com.example.ev2rinconsur.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.sessionDataStore: DataStore<Preferences> by preferencesDataStore(name = "sesion")

class SessionManager(private val context: Context) {

    private val keyUserId = longPreferencesKey("user_id")

    suspend fun guardarSesion(userId: Long) {
        context.sessionDataStore.edit { it[keyUserId] = userId }
    }

    suspend fun obtenerUserId(): Long? =
        context.sessionDataStore.data.map { it[keyUserId] }.first()

    suspend fun cerrarSesion() {
        context.sessionDataStore.edit { it.remove(keyUserId) }
    }
}