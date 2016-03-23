package com.inappropirates.lightwalker.util;

public class Util
{
    public static final String TAG = "LightWalkerRemote";
    public static final String INTENT_EXTRA_MODE_NAME = "android.intent.extra.MODE_NAME";

    public static float map(float value, float fromLow, float fromHigh, float toLow, float toHigh) {
        return toLow + (value - fromLow) * (toHigh - toLow) / (fromHigh - fromLow);
    }

    public float constrain(float amount, float low, float high)
    {
        return amount < low ? low : (amount > high ? high : amount);
    }
}
