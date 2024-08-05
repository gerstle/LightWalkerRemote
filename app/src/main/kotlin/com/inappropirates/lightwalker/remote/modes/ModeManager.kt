package com.inappropirates.lightwalker.remote.modes

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.preference.PreferenceManager
import com.inappropirates.lightwalker.remote.ui.MandyActivity
import com.inappropirates.lightwalker.remote.bluetooth.Bt
import com.inappropirates.lightwalker.remote.ui.BubbleConfigActivity
import com.inappropirates.lightwalker.remote.ui.ChaosConfigActivity
import com.inappropirates.lightwalker.remote.ui.EqualizerConfigActivity
import com.inappropirates.lightwalker.remote.ui.FlamesConfigActivity
import com.inappropirates.lightwalker.remote.ui.RainbowConfigActivity
import com.inappropirates.lightwalker.remote.ui.SparkleConfigActivity
import com.inappropirates.lightwalker.remote.ui.ZebraConfigActivity
import com.inappropirates.lightwalker.remote.util.PropertyFormatter

object ModeManager {
    private val TAG = this::class.java.simpleName
    val modes = listOf(
        Mode("main", enabled = false),
        Mode("mandy"),
        Mode("sparkle", configActivity = SparkleConfigActivity::class.java),
        Mode("rainbow", configActivity = RainbowConfigActivity::class.java),
        Mode("zebra", configActivity = ZebraConfigActivity::class.java),
        Mode("chaos", configActivity = ChaosConfigActivity::class.java),
        Mode("flames", configActivity = FlamesConfigActivity::class.java),
        Mode("equalizer", configActivity = EqualizerConfigActivity::class.java),
        Mode("bubble", configActivity = BubbleConfigActivity::class.java),
        Mode("gravity", enabled = false)
    )
    val modeMap: Map<String, Mode> = modes.associateBy { it.name }

    val modeState = mutableStateOf(modeMap["main"]!!)

    suspend fun setMode(context: Context, mode: String) {
        modeMap[mode]?.let {
            setMode(context, it)
        } ?: throw RuntimeException("Mode ${mode} not found!")
    }

    suspend fun setMode(context: Context, mode: Mode) {
        when (mode) {
            modeMap["mandy"] -> {
                Intent(context, MandyActivity::class.java)
                    .also { context.startActivity(it) }
            }

            else -> {
                modeState.value = mode
                sendSettings(context, mode)
            }
        }
    }

    suspend fun sendSettings(context: Context, mode: Mode) {
        if (Bt.connection == null) {
            Log.d(TAG, "sendSettings: no connection available")
            return
        }

        val preferences: SharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(context)

        preferences
            .all
            .entries
            .filter { it.key.startsWith("main") || it.key.startsWith(mode.name) }
            .forEach { pref ->
                pref.value?.let { v ->
                    PropertyFormatter
                        .getStringVal(pref.key, v)
                        .let { Bt.send(pref.key, it) }
                }
            }

        Bt.send("mode", Modes.valueOf(mode.name).ordinal.toString())
    }
}