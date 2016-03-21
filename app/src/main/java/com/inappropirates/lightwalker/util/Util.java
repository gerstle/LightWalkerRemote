package com.inappropirates.lightwalker.util;

public class Util
{
    public static float map(float value, float fromLow, float fromHigh, float toLow, float toHigh) {
        return toLow + (value - fromLow) * (toHigh - toLow) / (fromHigh - fromLow);
    }

    public float constrain(float amount, float low, float high)
    {
        return amount < low ? low : (amount > high ? high : amount);
    }
}
