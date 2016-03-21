package com.inappropirates.lightwalker;

import android.content.Context;
import android.content.Intent;

import com.inappropirates.lightwalker.config.Mode;

public class MandyMode extends Mode
{

    public MandyMode(String name, Class intent, Integer layout, Boolean enabled)
    {
        super(name, intent, layout, enabled);
    }

    @Override
    public void init(Context context)
    {
        super.init(context);

        Intent intent = new Intent(context, MandyActivity.class);
        context.startActivity(intent);
    }
}
