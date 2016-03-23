package com.inappropirates.lightwalker.ui;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.inappropirates.lightwalker.config.ColorButton;

import java.util.ArrayList;
import java.util.List;

public class ColorGridAdapter extends BaseAdapter
{
    private Context context;
    private static final int COLOR_COUNT = 34;
    private View.OnClickListener onColorClickListener;


    private List<Color> colors = new ArrayList<Color>(){
        {
            add(new Color("purple", 192, 255, 255));
            add(new Color(208, 255, 255));
            add(new Color("pink", 224, 255, 255));
            add(new Color(240, 255, 255));
            add(new Color("red", 0, 255, 255, android.graphics.Color.RED));
            add(new Color(16, 255, 255));
            add(new Color("orange", 32, 255, 255));
            add(new Color(48, 255, 255));
            add(new Color("yellow", 64, 255, 255, android.graphics.Color.YELLOW));
            add(new Color(80, 255, 255));
            add(new Color("green", 96, 255, 255, android.graphics.Color.GREEN));
            add(new Color(112, 255, 255));
            add(new Color("aqua", 128, 255, 255));
            add(new Color(144, 255, 255));
            add(new Color("blue", 160, 255, 255, android.graphics.Color.BLUE));
            add(new Color(176, 255, 255));
            add(new Color("white", 0, 0, 255, android.graphics.Color.WHITE));
            add(new Color("black", 0, 0, 0, android.graphics.Color.BLACK));
        }
    };


    public ColorGridAdapter(Context context, View.OnClickListener onColorClickListener)
    {
        this.context = context;
        this.onColorClickListener = onColorClickListener;
    }

    @Override
    public int getCount()
    {
        return colors.size();
    }

    @Override
    public Object getItem(int position)
    {
        return colors.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ColorButton button;
        if (convertView == null)
        {
            button = new ColorButton(context, colors.get(position));
            button.setOnClickListener(onColorClickListener);
        } else
        {
            button = (ColorButton) convertView;
        }

        return button;
    }
}
