package com.inappropirates.lightwalker.ui;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.inappropirates.lightwalker.util.AppUtil;
import com.inappropirates.lightwalker.bluetooth.BluetoothBoss;

public class BTStatusHandler extends Handler {

    private static final boolean DEBUG = true;

    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    private String connectedDeviceName = "";
    private Context context;
    private Button button;

    public BTStatusHandler(Context context, Button button) {
        this.context = context;
        this.button = button;
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case MESSAGE_STATE_CHANGE:
                if (DEBUG) Log.i(AppUtil.TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                switch (msg.arg1) {
                    case BluetoothBoss.STATE_DISCONNECTED:
                        button.setBackgroundColor(android.graphics.Color.BLUE);
                        button.setEnabled(true);
                        break;
                    case BluetoothBoss.STATE_CONNECTING:
                        break;
                    case BluetoothBoss.STATE_CONNECTED:
                        button.setBackgroundColor(android.graphics.Color.GREEN);
                        break;
                    case BluetoothBoss.STATE_LISTEN:
                    case BluetoothBoss.STATE_NONE:
                        break;
                }
                break;
            case MESSAGE_WRITE:
                break;
            case MESSAGE_READ:
                break;
            case MESSAGE_DEVICE_NAME:
                // save the connected device's name
                connectedDeviceName = msg.getData().getString(DEVICE_NAME);
                Toast.makeText(context, "Connected to "
                        + connectedDeviceName, Toast.LENGTH_SHORT).show();
                break;
            case MESSAGE_TOAST:
                Toast.makeText(context, msg.getData().getString(TOAST), Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
