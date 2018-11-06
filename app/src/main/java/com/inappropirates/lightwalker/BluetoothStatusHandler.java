package com.inappropirates.lightwalker;

import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.inappropirates.lightwalker.bluetooth.BluetoothMessageEnum;
import com.inappropirates.lightwalker.bluetooth.BluetoothUartManager;
import com.inappropirates.lightwalker.util.Util;

public class BluetoothStatusHandler extends Handler {
    public static final String TOAST = "toast";

    private Button button;
    private Context context;

    public BluetoothStatusHandler(Button button, Context context) {
        this.button = button;
        this.context = context;
    }

    @Override
    public void handleMessage(Message msg) {
        switch (BluetoothMessageEnum.values()[msg.what]) {
            case STATE_CHANGE:
                Log.i(Util.TAG, "STATE_CHANGE: " + msg.arg1);
                switch (msg.arg1) {
                    case BluetoothProfile.STATE_DISCONNECTED:
                        //Toast.makeText(context, "Disconnected", Toast.LENGTH_SHORT).show();
                        setConnectedButton(false);
                        break;
                    case BluetoothProfile.STATE_CONNECTING:
                        break;
                    case BluetoothProfile.STATE_CONNECTED:
                        setConnectedButton(true);
                        if (msg.arg2 == 1)
                            BluetoothUartManager.INSTANCE.sendAllCurrentSettings();
                        break;
                }
                break;
            case READ:
                String message = (String) msg.obj;
                Log.d(Util.TAG, "message received: '" + message + "'");
                break;
            case TOAST:
                Toast.makeText(context, msg.getData().getString(TOAST), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    // be careful calling this from non-ui threads...
    private void setConnectedButton(boolean connected) {
        if (connected) {
            button.setBackgroundColor(android.graphics.Color.argb(255, 144, 210, 142)); // green
            button.setText("connected");
        } else {
            button.setBackgroundColor(android.graphics.Color.argb(255, 206, 106, 108)); // red
            button.setEnabled(true);
            button.setText("connect");
        }
    }
}
