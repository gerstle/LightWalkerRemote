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

class EqualizerConfigActivity : ConfigActivity() {
    override val name = "Equalizer"

    @Composable
    override fun ConfigContent() {
        ProvidePreferenceLocals {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                sendingListPreference(
                    pref = Preferences.eqMode,
                    defaultValue = "static color",
                    values = listOf("static color", "single rainbow", "double rainbow"),
                    summary = { Text(text = it) }
                )

                sendingSliderPreference(
                    pref = Preferences.eqMinValue,
                    defaultValue = 175F,
                    valueRange = 0F..255F
                )
                colorPreference(
                    pref = Preferences.eqColor,
                    defaultValue = Color.Magenta.toHexString(),
                )
                sendingCheckboxPreference(
                    pref = Preferences.eqAllBands,
                    defaultValue = false,
                    summary = { Text(text = if (it) "On" else "Off") }
                )
            }
        }
    }
}
