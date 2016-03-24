package com.inappropirates.lightwalker.bluetooth;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.inappropirates.lightwalker.MainActivity;
import com.inappropirates.lightwalker.config.Config;
import com.inappropirates.lightwalker.util.Util;

public class BluetoothHandler extends Handler
{
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    private Button button;
    private Context context;

    public BluetoothHandler(Button button, Context context)
    {
        this.button = button;
        this.context = context;
    }

    @Override
    public void handleMessage(Message msg)
    {
        switch (BluetoothMessageEnum.values()[msg.what])
        {
            case STATE_CHANGE:
                Log.i(Util.TAG, "STATE_CHANGE: " + msg.arg1);
                switch (BluetoothStatusEnum.values()[msg.arg1])
                {
                    case DISCONNECTED:
                        setConnectedButton(false);
                        break;
                    case CONNECTING:
                        break;
                    case CONNECTED:
                        setConnectedButton(true);
                        MainActivity.sendAllCurrentSettings();
                        break;
                    case LISTEN:
                    case NONE:
                        break;
                }
                break;
            case WRITE:
                break;
            case READ:
                // construct a string from the valid bytes in the buffer
                String message = (String) msg.obj;
                Log.d(Util.TAG, "message read: '" + message + "'");

                // ignoring this for now, I'm just going to send all settings on every connection
//                if (message.equals("SettingsPlease") && Config.currentMode != null)
//                    MainActivity.sendAllCurrentSettings();
                break;
            case DEVICE_NAME:
                Toast.makeText(context, msg.getData().getString(DEVICE_NAME), Toast.LENGTH_SHORT).show();
                break;
            case TOAST:
                Toast.makeText(context, msg.getData().getString(TOAST), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    // be careful calling this from non-ui threads...
    public void setConnectedButton(boolean connected)
    {
        if (connected)
        {
            button.setBackgroundColor(android.graphics.Color.argb(255, 144, 210, 142)); // green
            button.setText("connected");
        }
        else
        {
            button.setBackgroundColor(android.graphics.Color.argb(255, 206, 106, 108)); // red
            button.setEnabled(true);
            button.setText("connect");
        }
    }
}
