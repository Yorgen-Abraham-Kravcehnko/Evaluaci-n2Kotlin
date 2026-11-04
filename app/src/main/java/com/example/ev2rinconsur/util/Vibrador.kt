package com.example.ev2rinconsur.util

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

class Vibrador(context: Context) {

    private val vibrator: Vibrator? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            (context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager)
                ?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }

    private fun patron(tiempos: LongArray) {
        val v = vibrator ?: return
        if (!v.hasVibrator()) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createWaveform(tiempos, -1))
        } else {
            @Suppress("DEPRECATION")
            v.vibrate(tiempos, -1)
        }
    }

    /** 1 pulso corto */
    fun inicioTurno() = patron(longArrayOf(0, 150))

    /** 2 pulsos */
    fun finTurno() = patron(longArrayOf(0, 200, 120, 200))
}