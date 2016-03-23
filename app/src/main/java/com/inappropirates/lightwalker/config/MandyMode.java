package com.inappropirates.lightwalker.config;

import android.content.Context;
import android.content.Intent;

import com.inappropirates.lightwalker.MandyActivity;

public class MandyMode extends Mode
{

    public MandyMode(String name, Class intent, Integer layout, Boolean enabled)
    {
        super(name, intent, layout, enabled);
    }

    @Override
    public void init(Context context)
    {
        Intent intent = new Intent(context, MandyActivity.class);
        context.startActivity(intent);
    }
}
