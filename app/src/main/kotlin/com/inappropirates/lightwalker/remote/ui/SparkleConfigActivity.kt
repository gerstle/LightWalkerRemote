package com.inappropirates.lightwalker.remote.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import me.zhanghai.compose.preference.ProvidePreferenceLocals
import me.zhanghai.compose.preference.sliderPreference

class SparkleConfigActivity : ConfigActivity() {
    override val name = "Sparkle"

    @Composable
    override fun ConfigContent() {
        ProvidePreferenceLocals {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                sliderPreference(
                    key = "sparkleFadeRate",
                    defaultValue = 5F,
                    title = { Text("fade rate") },
                    valueRange = 1f..60f,
                    summary = { Text("%.1f".format(it)) },
                )
                sliderPreference(
                    key = "sparkleFlashLength",
                    defaultValue = 525F,
                    title = { Text("flash length") },
                    valueRange = 0f..1000f,
                    summary = { Text("%.1f".format(it)) },
                )
                sliderPreference(
                    key = "sparkleSparkleLength",
                    defaultValue = 575F,
                    title = { Text("sparkle length") },
                    valueRange = 100f..2000f,
                    summary = { Text("%.1f".format(it)) },
                )
                sliderPreference(
                    key = "sparkleMinValue",
                    defaultValue = 50F,
                    title = { Text("sparkle min value") },
                    valueRange = 0f..255f,
                    summary = { Text("%.1f".format(it)) },
                )
                colorPreference(
                    key = "sparkleFootFlashColor",
                    defaultValue = Color.Magenta.toHexString(),
                    title = { Text("foot flash color") },
                )
            }
        }
    }
}