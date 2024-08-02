package com.inappropirates.lightwalker.remote.modes

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.mutableStateOf
import com.inappropirates.lightwalker.remote.ui.MandyActivity
import com.inappropirates.lightwalker.remote.bluetooth.BluetoothUartManager
import com.inappropirates.lightwalker.remote.ui.SparkleConfigActivity

object ModeManager {
    val modes = listOf(
        Mode("main", enabled = false),
        Mode("mandy"),
        Mode("sparkle", configActivity = SparkleConfigActivity::class.java),
        Mode("rainbow"),
        Mode("zebra"),
        Mode("chaos"),
        Mode("flames"),
        Mode("equalizer"),
        Mode("bubble", enabled = false),
        Mode("gravity", enabled = false)
    )
    val modeMap: Map<String, Mode> = modes.associateBy { it.name }

    val modeState = mutableStateOf(modeMap["main"]!!)

    fun setMode(context: Context, mode: String) {
        modeMap[mode]?.let {
            setMode(context, it)
        } ?: throw RuntimeException("Mode ${mode} not found!")
    }

    fun setMode(context: Context, mode: Mode) {
        when (mode) {
            modeMap["mandy"] -> {
                Intent(context, MandyActivity::class.java)
                    .also { context.startActivity(it) }
            }

            else -> {
                modeState.value = mode
                BluetoothUartManager.sendAllCurrentSettings(context)
            }
        }
    }
}