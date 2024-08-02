package com.inappropirates.lightwalker.remote.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.inappropirates.lightwalker.remote.config.Preferences
import me.zhanghai.compose.preference.Preference
import me.zhanghai.compose.preference.ProvidePreferenceLocals

class SparkleConfigActivity : ConfigActivity() {
    override val name = "Sparkle"

    @Composable
    override fun ConfigContent() {
        ProvidePreferenceLocals {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                sendingSliderPreference(
                    pref = Preferences.sparkleFadeRate,
                    defaultValue = 5F,
                    valueRange = 1F..60F
                )
                sendingSliderPreference(
                    pref = Preferences.sparkleFlashLength,
                    defaultValue = 525F,
                    valueRange = 0F..1000F,
                )
                sendingSliderPreference(
                    pref = Preferences.sparkleSparkleLength,
                    defaultValue = 575F,
                    valueRange = 100F..2000F,
                )
                sendingSliderPreference(
                    pref = Preferences.sparkleMinValue,
                    defaultValue = 50F,
                    valueRange = 0F..255F,
                )
                colorPreference(
                    pref = Preferences.sparkleFootFlashColor,
                    defaultValue = Color.Magenta.toHexString(),
                )
                colorPreference(
                    pref = Preferences.sparkleSparkleColor,
                    defaultValue = Color.Magenta.toHexString(),
                )
            }
        }
    }
}
