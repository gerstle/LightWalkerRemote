package com.inappropirates.lightwalker.remote.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.inappropirates.lightwalker.remote.ui.theme.RemoteTheme


class ColorPickerActivity : ComponentActivity() {

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val preferences =
            getSharedPreferences("${application.packageName}_preferences", MODE_PRIVATE)
        val key = intent.extras?.getString("key")!!
        val color = mutableStateOf(getColor(preferences, key, Color.Magenta))
        val activity = this

        enableEdgeToEdge()
        setContent {
            RemoteTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(
                                    text = "Color Picker",
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center,
                                    color = Color.Blue
                                )
                            }
                        )
                    }
                ) {
                    Column {
                        Spacer(modifier = Modifier.padding(top = 104.dp))
                        Text(color.value.toHexString())
                        ColorPicker2(color)
                        Button(onClick = {
                            //setColor(preferences, key, color.value)
                            Intent()
                                .also {
                                    it.putExtra("color", color.value.toHexString())
                                    setResult(RESULT_OK, it)
                                }
                            activity.finish()
                        }) {
                            Text("done!")
                        }
                    }
                }
            }
        }
    }

    fun getColor(preferences: SharedPreferences, key: String, default: Color): Color =
        preferences
            .getString(key, default.toHexString())
            ?.let { Color.fromHex(it) }
            ?: throw RuntimeException("failed to get color")

    fun setColor(preferences: SharedPreferences, key: String, color: Color) {
        preferences
            .edit()
            .also {
                it.putString(key, color.toHexString())
                it.commit()
            }
    }
}

fun Int.hexToString() = String.format("#%06X", 0xFFFFFF and this)
fun Color.toHexString(): String = this.toArgb().hexToString()
fun Color.Companion.fromHex(colorString: String) =
    Color(android.graphics.Color.parseColor(colorString))
