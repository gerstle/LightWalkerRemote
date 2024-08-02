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

class FlamesConfigActivity : ConfigActivity() {
    override val name = "Flames"

    @Composable
    override fun ConfigContent() {
        ProvidePreferenceLocals {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                sendingSliderPreference(
                    pref = Preferences.flamesStepMillis,
                    defaultValue = 800F,
                    valueRange = 100F..2000F
                )
                sendingSliderPreference(
                    pref = Preferences.flamesDelay,
                    defaultValue = 30F,
                    valueRange = 0F..500F
                )
            }
        }
    }
}
