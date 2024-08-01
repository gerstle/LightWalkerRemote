package com.inappropirates.lightwalker.remote.util

import android.graphics.Color
import com.inappropirates.lightwalker.remote.ui.HSVColor
import com.inappropirates.lightwalker.remote.util.Util.map
import java.util.Locale

object PropertyFormatter {
    private const val COLOR_ROUND_VALUE = 17

    fun getStringVal(key: String, value: Any): String? {
        var stringVal: String? = null

        when (value) {
            is HSVColor -> {
                val builder = StringBuilder()
                builder.append(value.hue)
                    .append(",")
                    .append(value.sat)
                    .append(",")
                    .append(value.value)
                stringVal = builder.toString()
            }
            else -> {
                if (value is Int) stringVal = value.toString()
                else if (value is String) stringVal = value
                else if (value is Boolean) stringVal = if (value) "1" else "0"
            }
        }

        return stringVal
    }

    private fun roundColor(color: Int): Int {
        val modValue = color % COLOR_ROUND_VALUE
        if (modValue == 0) return color

        val cutoff = (COLOR_ROUND_VALUE * 0.8).toInt()
        return if (modValue <= cutoff) color - modValue
        else color + (COLOR_ROUND_VALUE - modValue)
    }
}
