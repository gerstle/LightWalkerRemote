package com.inappropirates.lightwalker;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.RadioButton;

public class MandyActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mandy);

        GridView gridView = (GridView) findViewById(R.id.gridView);
        gridView.setAdapter(new ColorGridAdapter(this));

        RadioButton radioButton = (RadioButton) findViewById(R.id.sparkleRadioButton);
        radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (isChecked)
                {
                    System.out.println("Go SPARKLE!");
                }
            }
        });

        radioButton = (RadioButton) findViewById(R.id.zebraRadioButton);
        radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (isChecked)
                {
                    System.out.println("Go ZEBRA!");
                }

            }
        });

        radioButton = (RadioButton) findViewById(R.id.rainbowRadioButton);
        radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (isChecked)
                {
                    System.out.println("Go RAINBOW!");
                }

            }
        });
    }
}
