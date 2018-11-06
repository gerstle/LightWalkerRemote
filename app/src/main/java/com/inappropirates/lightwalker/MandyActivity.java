package com.inappropirates.lightwalker;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.GridView;
import android.widget.RadioButton;

import com.inappropirates.lightwalker.bluetooth.BluetoothUartManager;
import com.inappropirates.lightwalker.config.ColorButton;
import com.inappropirates.lightwalker.config.Mode;
import com.inappropirates.lightwalker.config.ModeManager;
import com.inappropirates.lightwalker.config.Preferences;
import com.inappropirates.lightwalker.ui.Color;
import com.inappropirates.lightwalker.ui.ColorGridAdapter;
import com.inappropirates.lightwalker.util.PropertyFormatter;
import com.inappropirates.lightwalker.util.Util;

public class MandyActivity extends AppCompatActivity {
    Color currentColor = new Color(192, 255, 255);
    Mode sparkleMode = ModeManager.INSTANCE.getMode("sparkle");
    Mode zebraMode = ModeManager.INSTANCE.getMode("zebra");
    Mode rainbowMode = ModeManager.INSTANCE.getMode("rainbow");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mandy);

        GridView gridView = findViewById(R.id.gridView);
        gridView.setAdapter(new ColorGridAdapter(this, v -> {
            if (v instanceof ColorButton) {
                ColorButton button = (ColorButton) v;
                Log.d(Util.TAG, button.getColor().getName() + " clicked!");
                currentColor = button.getColor();

                Preferences preference = null;
                if (ModeManager.INSTANCE.getCurrentMode().equals(sparkleMode))
                    preference = Preferences.sparkleSparkleColor;
                else if (ModeManager.INSTANCE.getCurrentMode().equals(zebraMode))
                    preference = Preferences.zebraColorOne;

                if (preference != null)
                    BluetoothUartManager.INSTANCE.sendSetting(preference.toString(), PropertyFormatter.getStringVal("Color", currentColor.getAndroidColor()));
            }
        }));

        RadioButton radioButton = findViewById(R.id.sparkleRadioButton);
        radioButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                setMode(sparkleMode);
                BluetoothUartManager.INSTANCE.sendSetting(Preferences.sparkleSparkleColor.toString(), PropertyFormatter.getStringVal("Color", currentColor.getAndroidColor()));
                BluetoothUartManager.INSTANCE.sendSetting(Preferences.sparkleFootFlashColor.toString(), PropertyFormatter.getStringVal("Color", new Color(0, 0, 255).getAndroidColor()));
            }
        });

        setMode(zebraMode);
        radioButton = findViewById(R.id.zebraRadioButton);
        radioButton.setChecked(true);
        radioButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                setMode(zebraMode);
                BluetoothUartManager.INSTANCE.sendSetting(Preferences.zebraColorOne.toString(), PropertyFormatter.getStringVal("Color", currentColor.getAndroidColor()));
                BluetoothUartManager.INSTANCE.sendSetting(Preferences.zebraColorTwo.toString(), PropertyFormatter.getStringVal("Color", new Color(0, 0, 0).getAndroidColor()));
            }
        });

        radioButton = findViewById(R.id.rainbowRadioButton);
        radioButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                setMode(rainbowMode);
            }
        });
    }

    private void setMode(Mode mode) {
        Log.d(Util.TAG, "mandy mode -> " + mode.getName());
        ModeManager.INSTANCE.setCurrentMode(mode);
        BluetoothUartManager.INSTANCE.sendAllCurrentSettings();
    }
}
