/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * 20mar2016 Casey Gerstle
 * blah... all sorts of mods
 *
 */

package com.inappropirates.lightwalker.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.inappropirates.lightwalker.ui.BluetoothStatusHandler;
import com.inappropirates.lightwalker.util.AppUtil;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.UUID;

public class BluetoothBoss {
    private static final String NAME = "LW";
    private static final UUID MY_UUID_SECURE = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private final BluetoothAdapter adapter;
    private BluetoothDevice device;
    private Context context;
    private final Handler handler;
    private ConnectThread connectThread;
    private ConnectedThread connectedThread;
    private int state;

    // Constants that indicate the current connection state
    public static final int STATE_NONE = 0;
    public static final int STATE_LISTEN = 1;
    public static final int STATE_CONNECTING = 2;
    public static final int STATE_CONNECTED = 3;
    public static final int STATE_DISCONNECTED = 4;

    public BluetoothBoss(Context context, Handler handler) {
        adapter = BluetoothAdapter.getDefaultAdapter();
        state = STATE_NONE;
        this.context = context;
        this.handler = handler;
    }

    private synchronized void setState(int state) {
        Log.d(AppUtil.TAG, "setState() " + this.state + " -> " + state);
        this.state = state;

        handler.obtainMessage(BluetoothStatus.MESSAGE_STATE_CHANGE.ordinal(), state, -1).sendToTarget();
    }

    public synchronized int getState() {
        return state;
    }

    public synchronized void connect() {
        Set<BluetoothDevice> devices = adapter.getBondedDevices();
        for (BluetoothDevice device : devices)
            if (device.getAddress().equals("00:18:96:B0:01:8F"))
                this.device = device;

        if (device == null) {
            Message msg = handler.obtainMessage(BluetoothStatus.MESSAGE_TOAST.ordinal());
            Bundle bundle = new Bundle();
            bundle.putString(BluetoothStatusHandler.TOAST, "LightWalker is MIA");
            msg.setData(bundle);
            handler.sendMessage(msg);

            msg = handler.obtainMessage(BluetoothStatus.MESSAGE_STATE_CHANGE.ordinal());
            return;
        }

        Log.d(AppUtil.TAG, "connecting to: " + device);

        // Cancel any thread attempting to make a connection
        if (state == STATE_CONNECTING) {
            if (connectThread != null) {
                connectThread.cancel();
                connectThread = null;
            }
        }

        // Cancel any thread currently running a connection
        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }

        // Start the thread to connect with the given device
        connectThread = new ConnectThread(device);
        connectThread.start();
        setState(STATE_CONNECTING);
    }

    /**
     * Start the ConnectedThread to begin managing a Bluetooth connection
     *
     * @param socket The BluetoothSocket on which the connection was made
     * @param device The BluetoothDevice that has been connected
     */
    public synchronized void connected(BluetoothSocket socket, BluetoothDevice
            device, final String socketType) {
        Log.d(AppUtil.TAG, "connected, Socket Type:" + socketType);

        // Cancel the thread that completed the connection
        if (connectThread != null) {
            connectThread.cancel();
            connectThread = null;
        }

        // Cancel any thread currently running a connection
        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }

        // Start the thread to manage the connection and perform transmissions
        connectedThread = new ConnectedThread(context, handler, socket, socketType);
        connectedThread.start();

        // Send the name of the connected device back to the UI Activity
        Message msg = handler.obtainMessage(BluetoothStatus.MESSAGE_DEVICE_NAME.ordinal());
        Bundle bundle = new Bundle();
        bundle.putString(BluetoothStatusHandler.DEVICE_NAME, device.getName());
        msg.setData(bundle);
        handler.sendMessage(msg);

        setState(STATE_CONNECTED);
    }

    public synchronized void stop() {
        Log.d(AppUtil.TAG, "stop");

        if (connectThread != null) {
            connectThread.interrupt();
            connectThread.cancel();
            connectThread = null;
        }

        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }

        setState(STATE_NONE);
    }

    /**
     * Indicate that the connection attempt failed and notify the UI Activity.
     */
    private void connectionFailed() {
        // Send a failure message back to the Activity
        Message msg = handler.obtainMessage(BluetoothStatus.MESSAGE_TOAST.ordinal());
        Bundle bundle = new Bundle();
        bundle.putString(BluetoothStatusHandler.TOAST, "Unable to connect device");
        msg.setData(bundle);
        handler.sendMessage(msg);
        handler.obtainMessage(BluetoothStatus.MESSAGE_STATE_CHANGE.ordinal(), BluetoothBoss.STATE_DISCONNECTED, -1).sendToTarget();
    }

    /**
     * This thread runs while attempting to make an outgoing connection
     * with a device. It runs straight through; the connection either
     * succeeds or fails.
     */
    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;
        private String mSocketType;

        public ConnectThread(BluetoothDevice device) {
            mmDevice = device;
            BluetoothSocket tmp = null;

            // Get a BluetoothSocket for a connection with the
            // given BluetoothDevice
            try {
                Method m = device.getClass().getMethod("createRfcommSocket", new Class[]{int.class});
                tmp = (BluetoothSocket) m.invoke(device, Integer.valueOf(1));
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
                Log.e(AppUtil.TAG, "Socket Type: " + mSocketType + "create() failed - NoSuchMethodException", e);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                Log.e(AppUtil.TAG, "Socket Type: " + mSocketType + "create() failed - IllegalArgumentExecption", e);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                Log.e(AppUtil.TAG, "Socket Type: " + mSocketType + "create() failed - IllegalAccessException", e);
            } catch (InvocationTargetException e) {
                e.printStackTrace();
                Log.e(AppUtil.TAG, "Socket Type: " + mSocketType + "create() failed - InvocationTargetException", e);
            }
            mmSocket = tmp;
        }

        public void run() {
            Log.i(AppUtil.TAG, "BEGIN connectThread SocketType:" + mSocketType);
            setName("ConnectThread" + mSocketType);

            // Always cancel discovery because it will slow down a connection
            adapter.cancelDiscovery();

            // Make a connection to the BluetoothSocket
            try {
                // This is a blocking call and will only return on a
                // successful connection or an exception
                mmSocket.connect();
            } catch (IOException e) {
                // Close the socket
                try {
                    mmSocket.close();
                } catch (IOException e2) {
                    Log.e(AppUtil.TAG, "unable to close() " + mSocketType +
                            " socket during connection failure", e2);
                }
                connectionFailed();
                return;
            }

            // Reset the ConnectThread because we're done
            synchronized (BluetoothBoss.this) {
                connectThread = null;
            }

            // Start the connected thread
            connected(mmSocket, mmDevice, mSocketType);
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(AppUtil.TAG, "close() of connect " + mSocketType + " socket failed", e);
            }
        }
    }
}
