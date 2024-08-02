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
import me.zhanghai.compose.preference.checkboxPreference

class BubbleConfigActivity : ConfigActivity() {
    override val name = "Bubble"

    @Composable
    override fun ConfigContent() {
        ProvidePreferenceLocals {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                sendingSliderPreference(
                    pref = Preferences.bubbleSpeed,
                    defaultValue = 1F,
                    valueRange = 1F..5F
                )
                sendingSliderPreference(
                    pref = Preferences.bubbleWidth,
                    defaultValue = 3F,
                    valueRange = 1F..20F,
                )
                colorPreference(
                    pref = Preferences.bubbleBackgroundColor,
                    defaultValue = Color.Black.toHexString(),
                )
                colorPreference(
                    pref = Preferences.bubbleBubbleColor,
                    defaultValue = Color.Magenta.toHexString(),
                )
                sendingCheckboxPreference(
                    pref = Preferences.bubbleTrail,
                    defaultValue = false,
                    summary = { Text(text = if (it) "On" else "Off") }
                )
            }
        }
    }
}
