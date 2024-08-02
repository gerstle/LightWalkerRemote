package com.inappropirates.lightwalker.remote.util

import com.inappropirates.lightwalker.remote.ui.HSVColor

object PropertyFormatter {
    private const val COLOR_ROUND_VALUE = 17

    fun getStringVal(key: String, value: Any): String =
        when (value) {
            is Int -> value.toString()
            is String -> value
            is Float -> "%.0f".format(value)
            is HSVColor -> {
                val builder = StringBuilder()
                builder.append(value.hue)
                    .append(",")
                    .append(value.sat)
                    .append(",")
                    .append(value.value)
                builder.toString()
            }

            is Boolean -> {
                if (value) "1" else "0"
            }

            else -> throw RuntimeException("Unhandled property type! key: $key value: $value type: ${value.javaClass}")
        }

    private fun roundColor(color: Int): Int {
        val modValue = color % COLOR_ROUND_VALUE
        if (modValue == 0) return color

        val cutoff = (COLOR_ROUND_VALUE * 0.8).toInt()
        return if (modValue <= cutoff) color - modValue
        else color + (COLOR_ROUND_VALUE - modValue)
    }
}
