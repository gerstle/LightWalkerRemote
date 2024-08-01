package com.inappropirates.lightwalker.remote.ui

import android.app.Activity
import android.app.Instrumentation
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.preference.PreferenceManager
import com.github.skydoves.colorpicker.compose.AlphaTile
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import me.zhanghai.compose.preference.Preference
import me.zhanghai.compose.preference.ProvidePreferenceLocals
import me.zhanghai.compose.preference.preference
import me.zhanghai.compose.preference.sliderPreference
import me.zhanghai.compose.preference.textFieldPreference
import me.zhanghai.compose.preference.twoTargetIconButtonPreference

class SparkleConfigActivity : ConfigActivity() {
    override val name = "Sparkle"
    val colorPickerState = mutableStateOf(false)

    @Composable
    override fun ConfigContent() {
        Preferences()
    }

    @Composable
    fun Preferences() {
        val showColorPicker = remember { colorPickerState }
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
//                textFieldPreference(
//                    key = "sparkleFootFlashColor",
//                    defaultValue = Color.Magenta.toHexString(),
//                    title = { Text("foot flash color") },
//                    textToValue = { it }
//                )
            }
        }
    }

    fun LazyListScope.colorPref(color: Color, showColorPicker: MutableState<Boolean>) {
        preference(

            key = "sparkleFootFlashColor",
            title = { Text("foot flash color") },
            widgetContainer = {
                Surface(
                    modifier = Modifier
                        .width(150.dp)
                        .height(50.dp)
                        .padding(5.dp),
                    color = color
                ) {}
            },
            onClick = { colorPickerState.value = true }
        )
    }
}

@Composable
fun MinimalDialog(onDismissRequest: () -> Unit) {
    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Text(
                text = "This is a minimal dialog",
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center),
                textAlign = TextAlign.Center,
            )
        }
    }
}