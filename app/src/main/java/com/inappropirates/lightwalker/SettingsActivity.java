package com.inappropirates.lightwalker;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;

import com.inappropirates.lightwalker.config.ModeManager;
import com.inappropirates.lightwalker.ui.SettingsFragment;
import com.inappropirates.lightwalker.util.Util;
import com.inappropirates.lightwalker.config.Mode;

public class SettingsActivity extends Activity {
    private Mode mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mode = ModeManager.INSTANCE.getMode(getIntent().getExtras().getString(Util.INTENT_EXTRA_MODE_NAME));

        if (mode == null)
            throw new RuntimeException(String.format("Could not find mode '%s'", getIntent().getExtras().getString(Util.INTENT_EXTRA_MODE_NAME)));

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        SettingsFragment prefFragment = new SettingsFragment();
        prefFragment.setMode(mode);
        fragmentTransaction.replace(android.R.id.content, prefFragment);
        fragmentTransaction.commit();
    }
}
