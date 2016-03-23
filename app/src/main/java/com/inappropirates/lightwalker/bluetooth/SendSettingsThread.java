package com.inappropirates.lightwalker.bluetooth;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.inappropirates.lightwalker.MainActivity;
import com.inappropirates.lightwalker.config.Config;
import com.inappropirates.lightwalker.config.Mode;
import com.inappropirates.lightwalker.config.Modes;
import com.inappropirates.lightwalker.util.Util;
import com.inappropirates.util.PropertyFormatter;

import java.util.Map;

public class SendSettingsThread extends Thread
{
    Mode mode;
    Context context;

    public SendSettingsThread(Mode mode, Context context)
    {
        this.mode = mode;
        this.context = context;
    }

    @Override
    public void run()
    {
        if (mode == null)
            return;

        Log.d(Util.TAG, "setting mode " + mode.getName());
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        // set the mode first, then trickle out the settings. sometimes a mode is running so slow,
        // the messages back up and it never gets to the mode change
        // most of the time, we're not switching settings when we change modes anyway
        MainActivity.sendSetting("mode", new Integer(Modes.valueOf(mode.getName()).ordinal()).toString());

        // TODO this loops over *all* preferences for *all* modes... should probably update the
        // settings activity to send as things change so don't have to send all at the end?
        for (Map.Entry<String, ?> entry : preferences.getAll().entrySet())
            if (entry.getKey().startsWith("main") || entry.getKey().startsWith(mode.getName()))
            {
                MainActivity.sendSetting(entry.getKey(), PropertyFormatter.getStringVal(entry.getKey(), entry.getValue()));
                try
                {
                    Thread.sleep(250);
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
    }
}
