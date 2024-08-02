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

class ZebraConfigActivity : ConfigActivity() {
    override val name = "Zebra"

    @Composable
    override fun ConfigContent() {
        ProvidePreferenceLocals {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                colorPreference(
                    pref = Preferences.zebraColorOne,
                    defaultValue = Color.Black.toHexString(),
                )
                colorPreference(
                    pref = Preferences.zebraColorTwo,
                    defaultValue = Color.White.toHexString(),
                )
            }
        }
    }
}
