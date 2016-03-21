package com.inappropirates.lightwalker.config;

import android.content.Context;
import android.widget.Button;

import com.inappropirates.lightwalker.ui.Color;

public class ColorButton extends Button {
    private Color color;

    public ColorButton(Context context, Color color) {
        super(context);

        this.color = color;

        setBackgroundColor(color.getAndroidColor());
        setText(color.getName());
    }

    public Color getColor() {
        return color;
    }
}
