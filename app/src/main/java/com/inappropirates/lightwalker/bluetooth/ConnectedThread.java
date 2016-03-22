package com.inappropirates.lightwalker.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.inappropirates.lightwalker.ui.BluetoothStatusHandler;
import com.inappropirates.lightwalker.util.AppUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

class ConnectedThread extends Thread {
    private BluetoothSocket mmSocket;
    private InputStream mmInStream;
    private OutputStream mmOutStream;
    private Boolean connected = true;
    private Context context;
    private Handler statusHandler;

    public ConnectedThread(Context context, Handler statusHandler, BluetoothSocket socket, String socketType) {
        Log.d(AppUtil.TAG, "create ConnectedThread: " + socketType);

        this.context = context;
        this.statusHandler = statusHandler;

        mmSocket = socket;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        // Get the BluetoothSocket input and output streams
        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) {
            Log.e(AppUtil.TAG, "temp sockets not created", e);
        }

        mmInStream = tmpIn;
        mmOutStream = tmpOut;

        setupStatusListener(context);
    }

    public void run() {
        Log.i(AppUtil.TAG, "BEGIN connectedThread");
        byte[] buffer = new byte[1024];
        int bytes, byteIndex = 0;
        Message m = null;

        while (connected) {
            boolean eom = false;
            try {
                while (!eom && (mmInStream != null)) {
                    // Read from bluetooth
                    if (mmInStream.available() > 0) {
                        bytes = mmInStream.read(buffer, byteIndex, 1024 - byteIndex);
                        byteIndex += bytes;
                        if (buffer[byteIndex - 1] == '\r')
                            eom = true;
                    }
                }

            } catch (IOException e) {
                Log.e(AppUtil.TAG, "io exception disconnected", e);
                cancel();
            } catch (Exception e) {
                Log.e(AppUtil.TAG, "disconnected", e);
                cancel();
            }

            if (byteIndex > 0) {
                Log.i(AppUtil.TAG, "Received: " + new String(buffer, 0, (byteIndex - 1)));
                m = statusHandler.obtainMessage(BluetoothStatus.MESSAGE_READ.ordinal(), byteIndex - 1, -1, buffer);
                m.sendToTarget();
            }
            byteIndex = 0;
        }
    }

    public void cancel() {
        if (mmInStream != null) {
            try {
                mmInStream.close();
            } catch (IOException e) {
                Log.e(AppUtil.TAG, "close() of input stream failed", e);
            }
            mmInStream = null;
        }

        if (mmOutStream != null) {
            try {
                mmOutStream.close();
            } catch (IOException e) {
                Log.e(AppUtil.TAG, "close() of output stream failed", e);
            }
            mmOutStream = null;
        }

        if (mmSocket != null) {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(AppUtil.TAG, "close() of connect socket failed", e);
            }
            mmSocket = null;
        }

        synchronized (connected) {
            if (connected) {
                connected = false;

                // Send a failure message back to the Activity
                Message msg = statusHandler.obtainMessage(BluetoothStatus.MESSAGE_TOAST.ordinal());
                Bundle bundle = new Bundle();
                bundle.putString(BluetoothStatusHandler.TOAST, "Device connection was lost");
                msg.setData(bundle);
                statusHandler.sendMessage(msg);
                statusHandler.obtainMessage(BluetoothStatus.MESSAGE_STATE_CHANGE.ordinal(), BluetoothBoss.STATE_DISCONNECTED, -1).sendToTarget();
            }
        }
    }

    private void setupStatusListener(Context context) {
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action.equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)) {
                    cancel();
                }
            }
        };

        context.registerReceiver(receiver, new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED));
    }
}
