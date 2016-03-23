package com.inappropirates.lightwalker.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.inappropirates.lightwalker.MainActivity;
import com.inappropirates.lightwalker.config.Mode;
import com.inappropirates.util.PropertyFormatter;

// <cgerstle> PreferenceFragment wants you to have a default constructor and a setter instead of
// a non-default constructor... not really sure that applies since I'm creating manually, but whatev
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener
{
    private Mode mode;

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(mode.getResource());
    }

    @Override
    public void onResume()
    {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
    {
        Object value = sharedPreferences.getAll().get(key);
        MainActivity.sendSetting(key, PropertyFormatter.getStringVal(key, value));
    }
}
