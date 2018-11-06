package com.inappropirates.lightwalker.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;

import com.inappropirates.lightwalker.Application;
import com.inappropirates.lightwalker.BluetoothStatusHandler;
import com.inappropirates.lightwalker.config.Mode;
import com.inappropirates.lightwalker.config.ModeManager;
import com.inappropirates.lightwalker.config.Modes;
import com.inappropirates.lightwalker.config.Preferences;
import com.inappropirates.lightwalker.util.PropertyFormatter;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import static com.inappropirates.lightwalker.util.Util.TAG;

public enum BluetoothUartManager {
    INSTANCE;

    private static final String LIGHTWALKER_TWO_POINT_OH = "00:18:96:B0:01:8F";
    private static final String LIGHTWALKER_THREE_POINT_OH = "98:76:B6:00:8A:E0";
    private static final String LIGHTWALKER_FOUR_POINT_OH = "C1:BD:37:71:DE:BC";
    private static final int BLE_SEND_BYTE_LIMIT = 20;

    private final BluetoothAdapter adapter;
    private final Context context;

    private BluetoothGatt bluetoothGatt;
    private BluetoothDevice device;
    private Handler bluetoothHandler;
    private int bluetoothState = BluetoothProfile.STATE_DISCONNECTED;
    private Semaphore writeLock = new Semaphore(1);

    private GattCallbackHandler gattCallback = new GattCallbackHandler(
            s -> {
                if (bluetoothHandler == null)
                    return;

                bluetoothState = s;
                bluetoothHandler
                        .obtainMessage(BluetoothMessageEnum.STATE_CHANGE.ordinal(), s, 1)
                        .sendToTarget();
            },
            s -> {
                // s should always be a non-blank string
                if (s.startsWith("K"))
                    writeLock.release();
            });

    BluetoothUartManager() {
        adapter = BluetoothAdapter.getDefaultAdapter();
        context = Application.getAppContext();
    }

    public void setHandler(Handler statusHandler) {
        this.bluetoothHandler = statusHandler;
    }

    public void connect() {

        if (bluetoothState != BluetoothProfile.STATE_DISCONNECTED) {
            Log.w(TAG, "cannot connect, state is not disconnected. Current state: " + bluetoothState);
            return;
        }
        // Always disconnect discovery because it will slow down a connection
        adapter.cancelDiscovery();
        Set<BluetoothDevice> devices = adapter.getBondedDevices();
        for (BluetoothDevice device : devices) {
            Log.d(TAG, "connect: looking at device " + device.getName() + " - " + device.getAddress());
            if (device.getAddress().equals(LIGHTWALKER_FOUR_POINT_OH))
                this.device = device;
        }

        if (device == null) {
            sendHandlerToast("LightWalker is MIA");
            return;
        }

        Log.i(TAG, "Attempting to connect to " + device.getName());
        try {
            if (bluetoothGatt == null)
                bluetoothGatt = device.connectGatt(context.getApplicationContext(), true, gattCallback);
            else if (!bluetoothGatt.connect())
                bluetoothState = BluetoothProfile.STATE_DISCONNECTED;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            Log.e(TAG, "failed to connect to GATT", e);
            bluetoothState = BluetoothProfile.STATE_DISCONNECTED;

            if (bluetoothGatt != null)
                bluetoothGatt.disconnect();
        }
    }

    public void disconnect() {
        bluetoothGatt.disconnect();
    }

    private void sendHandlerToast(String message) {
        if (bluetoothHandler == null)
            return;

        Message msg = bluetoothHandler.obtainMessage(BluetoothMessageEnum.TOAST.ordinal());
        Bundle bundle = new Bundle();
        bundle.putString(BluetoothStatusHandler.TOAST, message);
        msg.setData(bundle);
        bluetoothHandler.sendMessage(msg);
    }

    public void updateStatus() {
        if (bluetoothHandler == null)
            return;

        bluetoothHandler
                .obtainMessage(BluetoothMessageEnum.STATE_CHANGE.ordinal(), bluetoothState, 0)
                .sendToTarget();
    }

    public void sendSetting(String key, String value) {
        Preferences preference = Preferences.valueOf(key);
        String logMessage = String.format("sending %s(%d)=%s... ", key, preference.ordinal(), value);
        if (send(String.format("%d=%s\r", preference.ordinal(), value)))
            Log.d(TAG, logMessage + "success!");
        else
            Log.d(TAG, logMessage + "failed.");
    }

    private boolean send(String message) {
        if (bluetoothGatt != null && gattCallback.getTxCharacteristic() != null) {
            try {
                Log.d(TAG, "writing " + message);
                byte[] bytes = message.getBytes();

                for (int i = 0; i < bytes.length; i += BLE_SEND_BYTE_LIMIT) {
                    int partitionLength = Math.min(BLE_SEND_BYTE_LIMIT, bytes.length - i);
                    byte[] partition = new byte[partitionLength];
                    for (int j = 0; j < partitionLength; j++) {
                        partition[j] = bytes[i + j];
                    }

                    int attempts = 0;
                    Log.i(TAG, "available lock permits: " + writeLock.availablePermits());
                    boolean lockSucceeded = writeLock.tryAcquire(5, TimeUnit.SECONDS);
                    while (!lockSucceeded && attempts < 3) {
                        Log.w(TAG, "Failed to acquire write lock after 5s, releasing and retrying");
                        writeLock.release();
                        lockSucceeded = writeLock.tryAcquire(5, TimeUnit.SECONDS);
                        attempts++;
                    }

                    if (!lockSucceeded) {
                        Log.e(TAG, "Failed to acquire lock after 3 attempts, unable to send");
                        return false;
                    }

                    gattCallback.getTxCharacteristic().setValue(partition);
                    bluetoothGatt.writeCharacteristic(gattCallback.getTxCharacteristic());
                }
                return true;
            } catch (InterruptedException e) {
                Log.e(TAG, "failed to send, interrupt message" + e.getMessage());
            }
        } else {
            Log.d(TAG, "ble not available, can't send " + message);
        }

        return false;
    }

    public void sendAllCurrentSettings() {
        Mode mode = ModeManager.INSTANCE.getCurrentMode();
        if (bluetoothState == BluetoothProfile.STATE_CONNECTED && mode != null) {
            new Thread(() -> {
                Log.d(TAG, "setting mode " + mode.getName());
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

                sendSetting("mode", Integer.valueOf(Modes.valueOf(mode.getName()).ordinal()).toString());
                for (Map.Entry<String, ?> entry : preferences.getAll().entrySet())
                    if (entry.getKey().startsWith("main") || entry.getKey().startsWith(mode.getName())) {
                        sendSetting(entry.getKey(), PropertyFormatter.getStringVal(entry.getKey(), entry.getValue()));
                    }
            }).start();
        }
    }
}
