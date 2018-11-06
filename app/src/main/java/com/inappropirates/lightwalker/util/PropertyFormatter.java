package com.inappropirates.lightwalker.util;

import android.graphics.Color;

public class PropertyFormatter
{
    private final static int colorRoundValue = 17;

    public static String getStringVal(String key, Object value)
    {
        String stringVal = null;

        if (key.toLowerCase().contains("color"))
        {
            // in order to round things out a big so you can easily pick a solid green or yellow or whatever,
            // pick the color down to it's argb values, round those, and then put it back into a color
            Integer color = (Integer) value;
            int newColor = Color.argb(Color.alpha(color), RoundColor(Color.red(color)), RoundColor(Color.green(color)), RoundColor(Color.blue(color)));
            float[] hsvColor = new float[3];
            Color.colorToHSV(newColor, hsvColor);

            StringBuilder builder = new StringBuilder();
            builder.append((int) Util.map(hsvColor[0], 0, 360, 0, 255))
                    .append(",")
                    .append((int) Util.map(hsvColor[1], 0, 1, 0, 255))
                    .append(",")
                    .append((int) Util.map(hsvColor[2], 0, 1, 0, 255));
            stringVal = builder.toString();
        } else
        {
            if (value instanceof Integer)
                stringVal = value.toString();
            else if (value instanceof String)
                stringVal = (String) value;
            else if (value instanceof Boolean)
                stringVal = ((Boolean) value) ? "1" : "0";
        }

        return stringVal;
    }

    private static int RoundColor(int color)
    {
        int modValue = color % colorRoundValue;
        if (modValue == 0)
            return color;

        int cutoff = (int) (colorRoundValue * 0.8);
        if (modValue <= cutoff)
            return color - modValue;
        else
            return color + (colorRoundValue - modValue);

    }
}
