package com.inappropirates.lightwalker;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.RadioButton;

import com.inappropirates.lightwalker.config.ColorButton;
import com.inappropirates.lightwalker.config.Config;
import com.inappropirates.lightwalker.config.Mode;
import com.inappropirates.lightwalker.config.Preferences;
import com.inappropirates.lightwalker.ui.Color;
import com.inappropirates.lightwalker.ui.ColorGridAdapter;
import com.inappropirates.lightwalker.util.Util;
import com.inappropirates.util.PropertyFormatter;

public class MandyActivity extends AppCompatActivity {
    Color currentColor = new Color(192, 255, 255);
    Mode sparkleMode = Config.getMode("sparkle");
    Mode zebraMode = Config.getMode("zebra");
    Mode rainbowMode = Config.getMode("rainbow");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mandy);

        GridView gridView = (GridView) findViewById(R.id.gridView);
        gridView.setAdapter(new ColorGridAdapter(this, new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (v instanceof ColorButton)
                {
                    ColorButton button = (ColorButton) v;
                    Log.d(Util.TAG, button.getColor().getName() + " clicked!");
                    currentColor = button.getColor();

                    Preferences preference = null;
                    if (Config.currentMode.equals(sparkleMode))
                        preference = Preferences.sparkleSparkleColor;
                    else if (Config.currentMode.equals(zebraMode))
                        preference = Preferences.zebraColorOne;

                    if (preference != null)
                        MainActivity.sendSetting(preference.toString(), PropertyFormatter.getStringVal("Color", currentColor.getAndroidColor()));
                }
            }
        }));

        RadioButton radioButton = (RadioButton) findViewById(R.id.sparkleRadioButton);
        radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    setMode(sparkleMode);
                    MainActivity.sendSetting(Preferences.sparkleSparkleColor.toString(), PropertyFormatter.getStringVal("Color", currentColor.getAndroidColor()));
                    MainActivity.sendSetting(Preferences.sparkleFootFlashColor.toString(), PropertyFormatter.getStringVal("Color", new Color(0, 0, 255).getAndroidColor()));
                }
            }
        });

        setMode(zebraMode);
        radioButton = (RadioButton) findViewById(R.id.zebraRadioButton);
        radioButton.setChecked(true);
        radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    setMode(zebraMode);
                    MainActivity.sendSetting(Preferences.zebraColorOne.toString(), PropertyFormatter.getStringVal("Color", currentColor.getAndroidColor()));
                    MainActivity.sendSetting(Preferences.zebraColorTwo.toString(), PropertyFormatter.getStringVal("Color", new Color(0, 0, 0).getAndroidColor()));
                }
            }
        });

        radioButton = (RadioButton) findViewById(R.id.rainbowRadioButton);
        radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    setMode(rainbowMode);
                }
            }
        });
    }

    private void setMode(Mode mode)
    {
        Log.d(Util.TAG, "mandy mode -> " + mode.getName());
        Config.currentMode = mode;
        MainActivity.sendAllCurrentSettings();
    }
}
