package com.inappropirates.lightwalker;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.inappropirates.lightwalker.config.Mode;

// <cgerstle> PreferenceFragment wants you to have a default constructor and a setter instead of
// a non-default constructor... not really sure that applies since I'm creating manually, but whatev
public class SettingsFragment extends PreferenceFragment {
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

        addPreferencesFromResource(mode.getLayout());
    }
}
