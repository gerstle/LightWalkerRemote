package com.inappropirates.lightwalker.remote.ui

import androidx.compose.ui.graphics.Color
import com.inappropirates.lightwalker.remote.util.Util.map


/***
 * android Color's HSV is 0-360, but LED stuff is all 0-255...
 */
class HSVColor(
    var hue: Int,
    var sat: Int,
    var value: Int,
    var name: String? = null,
    private var androidColor: Color? = null
) {
    fun getAndroidColor(): Color {
        androidColor?.let { return it }

        return Color.hsv(
            map(hue.toFloat(), 0f, 255f, 0f, 360f),
            map(sat.toFloat(), 0f, 255f, 0f, 1f),
            map(value.toFloat(), 0f, 255f, 0f, 1f),
        )
    }
}
