package com.inappropirates.lightwalker;

/***
 * android Color's HSV is 0-360, but LED stuff is all 0-255...
 */
public class Color
{
    private Integer hue;
    private Integer sat;
    private Integer val;
    private String name;
    private Integer androidColor = null;

    public Color(Integer hue, Integer sat, Integer val)
    {
        this.hue = hue;
        this.sat = sat;
        this.val = val;
    }

    public Color(String name, Integer hue, Integer sat, Integer val)
    {
        this.name = name;
        this.hue = hue;
        this.sat = sat;
        this.val = val;
    }

    public Color(String name, Integer hue, Integer sat, Integer val, Integer androidColor)
    {
        this.hue = hue;
        this.sat = sat;
        this.val = val;
        this.name = name;
        this.androidColor = androidColor;
    }

    public Integer getHue()
    {
        return hue;
    }

    public void setHue(Integer hue)
    {
        this.hue = hue;
    }

    public Integer getSat()
    {
        return sat;
    }

    public void setSat(Integer sat)
    {
        this.sat = sat;
    }

    public Integer getVal()
    {
        return val;
    }

    public void setVal(Integer val)
    {
        this.val = val;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Integer getAndroidColor()
    {
        if (androidColor != null)
            return androidColor;
        else
        {
            float[] androidColorHSV = new float[]{
                    Util.Map(hue, 0, 255, 0, 360),
                    Util.Map(sat, 0, 255, 0, 360),
                    Util.Map(val, 0, 255, 0, 360),
            };
            return android.graphics.Color.HSVToColor(androidColorHSV);
        }
    }
}
