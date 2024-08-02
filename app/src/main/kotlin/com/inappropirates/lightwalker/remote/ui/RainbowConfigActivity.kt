package com.inappropirates.lightwalker.remote.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.inappropirates.lightwalker.remote.config.Preferences
import me.zhanghai.compose.preference.ProvidePreferenceLocals

class RainbowConfigActivity : ConfigActivity() {
    override val name = "Rainbow"

    @Composable
    override fun ConfigContent() {
        ProvidePreferenceLocals {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                sendingListPreference(
                    pref = Preferences.rainbowMode,
                    defaultValue = "double",
                    values = listOf("single", "double", "rotate", "rise"),
                    summary = { Text(text = it) }
                )
                sendingSliderPreference(
                    pref = Preferences.rainbowMinValue,
                    defaultValue = 40F,
                    valueRange = 0F..255F
                )
                sendingSliderPreference(
                    pref = Preferences.rainbowDelay,
                    defaultValue = 35F,
                    valueRange = 0F..500F,
                )
            }
        }
    }
}
