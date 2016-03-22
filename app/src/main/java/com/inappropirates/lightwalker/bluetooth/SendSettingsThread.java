package com.inappropirates.lightwalker.bluetooth;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.inappropirates.lightwalker.config.Mode;
import com.inappropirates.lightwalker.util.AppUtil;

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
        System.out.println("run bitches");

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        // TODO this loops over *all* preferences for *all* modes... should probably update the
        // settings activity to send as things change so don't have to send all at the end?
        for (Map.Entry<String, ?> entry : preferences.getAll().entrySet())
            if (entry.getKey().startsWith("main") || entry.getKey().startsWith(mode.getName()))
                send(entry.getKey(), entry.getValue().toString());
    }

    public void send(String key, String value)
    {
        System.out.println(String.format("sending %s: %s", key, value));
    }
}
