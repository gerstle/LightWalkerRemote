package com.inappropirates.lightwalker;

public class Util
{
    public static float Map(float value, float fromLow, float fromHigh, float toLow, float toHigh) {
        return toLow + (value - fromLow) * (toHigh - toLow) / (fromHigh - fromLow);
    }

    public float constrain(float amount, float low, float high)
    {
        return amount < low ? low : (amount > high ? high : amount);
    }
}
