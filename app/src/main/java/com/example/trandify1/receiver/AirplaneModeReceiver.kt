package com.example.trandify1.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.trandify1.utils.AirplaneModeUtils

class AirplaneModeReceiver(
    private val onStateChanged: (Boolean) -> Unit
) : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_AIRPLANE_MODE_CHANGED) {
            val isOn = AirplaneModeUtils.isAirplaneModeOn(context)
            onStateChanged(isOn)
        }
    }
}