package com.inappropirates.lightwalker.config;

import com.inappropirates.lightwalker.R;
import com.inappropirates.lightwalker.SettingsActivity;

import java.util.ArrayList;
import java.util.List;

public enum ModeManager
{
    INSTANCE;

    private Mode currentMode = null;
    private List<Mode> modes = new ArrayList<Mode>()
    {
        {
            add(new Mode("main", SettingsActivity.class, R.layout.main_preferences, false));
            add(new MandyMode("mandy", null, null, true));
            add(new Mode("sparkle", SettingsActivity.class, R.layout.sparkle_preferences, true));
            add(new Mode("rainbow", SettingsActivity.class, R.layout.rainbow_preferences, true));
            add(new Mode("zebra", SettingsActivity.class, R.layout.zebra_preferences, true));
            add(new Mode("chaos", SettingsActivity.class, R.layout.chaos_preferences, true));
            add(new Mode("flames", SettingsActivity.class, R.layout.flames_preferences, true));
            add(new Mode("equalizer", SettingsActivity.class, R.layout.equalizer_preferences, true));
            add(new Mode("bubble", SettingsActivity.class, R.layout.bubble_preferences, true));
            add(new Mode("gravity", SettingsActivity.class, R.layout.gravity_preferences, true));
        }
    };

    public Mode getMode(String modeName)
    {
        for (Mode mode : modes)
            if (mode.getName().equals(modeName))
                return mode;

        return null;
    }

    public Mode getCurrentMode() {
        return currentMode;
    }

    public void setCurrentMode(Mode currentMode) {
        this.currentMode = currentMode;
    }

    public List<Mode> getModes() {
        return modes;
    }
}
