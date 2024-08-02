package com.inappropirates.lightwalker.remote.util

object Util {
    const val TAG: String = "LightWalker"

    fun constrain(amount: Float, low: Float, high: Float): Float {
        return if (amount < low) low else (if (amount > high) high else amount)
    }

    fun map(value: Float, fromLow: Float, fromHigh: Float, toLow: Float, toHigh: Float): Float {
        return toLow + (value - fromLow) * (toHigh - toLow) / (fromHigh - fromLow)
    }
}
