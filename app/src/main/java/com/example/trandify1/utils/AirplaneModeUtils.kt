package com.example.trandify1.utils

import android.content.Context
import android.provider.Settings

object AirplaneModeUtils {
    fun isAirplaneModeOn(context: Context): Boolean {
        return try {
            Settings.Global.getInt(
                context.contentResolver,
                Settings.Global.AIRPLANE_MODE_ON, 0
            ) != 0
        } catch (e: Exception) {
            false
        }
    }
}