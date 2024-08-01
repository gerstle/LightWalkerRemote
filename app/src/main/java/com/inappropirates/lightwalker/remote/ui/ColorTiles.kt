package com.inappropirates.lightwalker.remote.ui

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.inappropirates.lightwalker.remote.bluetooth.BluetoothUartManager
import com.inappropirates.lightwalker.remote.config.Preferences
import com.inappropirates.lightwalker.remote.modes.ModeManager
import com.inappropirates.lightwalker.remote.util.PropertyFormatter
import com.inappropirates.lightwalker.remote.util.Util.TAG

private val colors: List<HSVColor> = listOf(
    HSVColor(192, 255, 255, "purple"),
    HSVColor(208, 255, 255),
    HSVColor(224, 255, 255, "pink"),
    HSVColor(240, 255, 255),
    HSVColor(0, 255, 255, "red", Color.Red),
    HSVColor(16, 255, 255),
    HSVColor(32, 255, 255, "orange"),
    HSVColor(48, 255, 255),
    HSVColor(64, 255, 255, "yellow", Color.Yellow),
    HSVColor(80, 255, 255),
    HSVColor(96, 255, 255, "green", Color.Green),
    HSVColor(112, 255, 255),
    HSVColor(128, 255, 255, "aqua"),
    HSVColor(144, 255, 255),
    HSVColor(160, 255, 255, "blue", Color.Blue),
    HSVColor(176, 255, 255),
    HSVColor(0, 0, 255, "white", Color.White),
    HSVColor(0, 0, 0, "black", Color.Black)
)

@Composable
fun ColorTile(
    modifier: Modifier = Modifier,
    name: String,
    color: HSVColor,
    colorState: MutableState<HSVColor?>
) {
    Surface(
        modifier = modifier,
        color = color.getAndroidColor(),
        border = if (color == colorState.value) { BorderStroke(2.dp, Color.Magenta) } else { null },
        onClick = {
            Log.d(TAG, color.name + " clicked!")
            colorState.value = color

            var preference: Preferences? = null
            if (ModeManager.modeState.value.name.equals("sparkle"))
                preference = Preferences.sparkleSparkleColor
            else if (ModeManager.modeState.value.name.equals("zebra")) {
                preference = Preferences.zebraColorOne
            }

            preference?.let {
                BluetoothUartManager.sendSetting(
                    preference.toString(),
                    PropertyFormatter.getStringVal("Color", color)!!
                )
            }
        }
    ) {
    }
}

@Composable
fun ColorTilesGrid(
    modifier: Modifier = Modifier,
) {
    val colorState = remember { mutableStateOf<HSVColor?>(null)}

    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.Fixed(4),
    ) {
        items(colors) {
            ColorTile(
                Modifier.aspectRatio(1f),
                it.name ?: "",
                it,
                colorState
            )
        }
    }
}
