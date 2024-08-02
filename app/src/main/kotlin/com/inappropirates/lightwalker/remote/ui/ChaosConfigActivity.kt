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

class ChaosConfigActivity : ConfigActivity() {
    override val name = "Sparkle"

    @Composable
    override fun ConfigContent() {
        ProvidePreferenceLocals {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                sendingSliderPreference(
                    pref = Preferences.chaosMinValue,
                    defaultValue = 100F,
                    valueRange = 20F..200F
                )
                sendingSliderPreference(
                    pref = Preferences.chaosStepLength,
                    defaultValue = 1250F,
                    valueRange = 500F..4000F
                )
                sendingSliderPreference(
                    pref = Preferences.chaosSpeed,
                    defaultValue = 4F,
                    valueRange = 0F..30F
                )
                sendingSliderPreference(
                    pref = Preferences.chaosSwing,
                    defaultValue = 30F,
                    valueRange = 0F..100F
                )
                sendingCheckboxPreference(
                    pref = Preferences.chaosSparse,
                    defaultValue = false
                )
                colorPreference(
                    pref = Preferences.chaosColor,
                    defaultValue = Color.Blue.toHexString(),
                )
            }
        }
    }
}
