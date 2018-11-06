package com.inappropirates.lightwalker.bluetooth;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.util.Log;

import java.util.UUID;
import java.util.function.Consumer;

import static com.inappropirates.lightwalker.util.Util.TAG;

public class GattCallbackHandler extends BluetoothGattCallback {
    private static final UUID UART_SERVICE_UUID = UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e");
    private static final UUID TX_CHARACTERISTIC_UUID = UUID.fromString("6e400002-b5a3-f393-e0a9-e50e24dcca9e");
    private static final UUID RX_CHARACTERISTIC_UUID = UUID.fromString("6e400003-b5a3-f393-e0a9-e50e24dcca9e");
    private final Consumer<Integer> statusConsumer;
    private final Consumer<String> messageConsumer;
    private BluetoothGattCharacteristic txChar;

    GattCallbackHandler(Consumer<Integer> statusConsumer, Consumer<String> messageConsumer) {
        this.statusConsumer = statusConsumer;
        this.messageConsumer = messageConsumer;
    }

    BluetoothGattCharacteristic getTxCharacteristic() {
        return txChar;
    }

    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        statusConsumer.accept(newState);
        if (newState == BluetoothProfile.STATE_CONNECTED) {
            Log.i(TAG, "service discovery result: " + gatt.discoverServices());
        }
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {

        if (status == BluetoothGatt.GATT_SUCCESS) {
            Log.i(TAG, "onServicesDiscovered received: " + status);

            BluetoothGattService service = gatt.getService(UART_SERVICE_UUID);
            if (service == null) {
                Log.e(TAG, "UART service not found!");
                gatt.disconnect();
                return;
            }

            txChar = service.getCharacteristic(TX_CHARACTERISTIC_UUID);
            if (txChar == null) {
                Log.e(TAG, "Tx characteristic not found!");
                gatt.disconnect();
                return;
            }
            txChar.setValue("initial load\r");

            BluetoothGattCharacteristic rxChar = service.getCharacteristic(RX_CHARACTERISTIC_UUID);
            if (rxChar == null) {
                Log.e(TAG, "Rx characteristic not found!");
                gatt.disconnect();
                return;
            }

            gatt.setCharacteristicNotification(rxChar, true);
        } else {
            Log.w(TAG, "onServicesDiscovered received: " + status);
        }
    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        String response = characteristic.getStringValue(0);
        Log.d(TAG, "on change - received: " + response);
        if (response != null && response.length() > 0)
            messageConsumer.accept(response);
    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        // nothing, just waiting for the ack response
    }
}
