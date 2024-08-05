package com.inappropirates.lightwalker.remote.util

import androidx.compose.ui.graphics.Color
import com.inappropirates.lightwalker.remote.ui.HSVColor
import com.inappropirates.lightwalker.remote.ui.fromHex

object PropertyFormatter {
    private const val COLOR_ROUND_VALUE = 17

    fun getStringVal(key: String, value: Any): String {
        val it = if (value is String && value.startsWith("#")) {
            HSVColor.fromAndroidColor(Color.fromHex(value))
        } else {
            value
        }

        return when (it) {
            is Int -> it.toString()
            is String -> it
            is Float -> "%.0f".format(it)
            is HSVColor -> {
                val builder = StringBuilder()
                builder.append(it.hue)
                    .append(",")
                    .append(it.sat)
                    .append(",")
                    .append(it.value)
                builder.toString()
            }

            is Boolean -> {
                if (it) "1" else "0"
            }

            else -> throw RuntimeException("Unhandled property type! key: $key value: $value type: ${value.javaClass}")
        }
    }

    private fun roundColor(color: Int): Int {
        val modValue = color % COLOR_ROUND_VALUE
        if (modValue == 0) return color

        val cutoff = (COLOR_ROUND_VALUE * 0.8).toInt()
        return if (modValue <= cutoff) color - modValue
        else color + (COLOR_ROUND_VALUE - modValue)
    }
}
