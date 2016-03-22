package com.inappropirates.lightwalker.bluetooth;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Button;

import com.inappropirates.lightwalker.config.Config;
import com.inappropirates.lightwalker.ui.AppStatusHandler;
import com.inappropirates.lightwalker.util.AppUtil;

public class BluetoothHandler extends Handler
{
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    private Button button;
    private AppStatusHandler appStatusHandler;
    private Context context;

    public BluetoothHandler(Button button, AppStatusHandler appStatusHandler, Context context)
    {
        this.button = button;
        this.appStatusHandler = appStatusHandler;
        this.context = context;
    }

    @Override
    public void handleMessage(Message msg)
    {
        switch (BluetoothStatus.values()[msg.what])
        {
            case MESSAGE_STATE_CHANGE:
                Log.i(AppUtil.TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                switch (msg.arg1)
                {
                    case BluetoothBoss.STATE_DISCONNECTED:
                        button.setBackgroundColor(android.graphics.Color.argb(255, 206, 106, 108));
                        button.setEnabled(true);
                        button.setText("connect");
                        break;
                    case BluetoothBoss.STATE_CONNECTING:
                        break;
                    case BluetoothBoss.STATE_CONNECTED:
                        button.setBackgroundColor(android.graphics.Color.argb(255, 144, 210, 142));
                        button.setText("connected");
                        break;
                    case BluetoothBoss.STATE_LISTEN:
                    case BluetoothBoss.STATE_NONE:
                        break;
                }
                break;
            case MESSAGE_WRITE:
                break;
            case MESSAGE_READ:
                // construct a string from the valid bytes in the buffer
                String message = (String) msg.obj;
                System.out.println("received '" + message + "'");

                if (message.equals("SettingsPlease") && Config.currentMode != null)
                    sendCurrentSettings();
                break;
            case MESSAGE_DEVICE_NAME:
                appStatusHandler.sendStatus("Connected to " + msg.getData().getString(DEVICE_NAME));
                break;
            case MESSAGE_TOAST:
                appStatusHandler.sendStatus(msg.getData().getString(TOAST));
                break;
        }
    }

    public void sendCurrentSettings()
    {
        SendSettingsThread sendSettingsThread = new SendSettingsThread(Config.currentMode, context);
        sendSettingsThread.start();
    }
}