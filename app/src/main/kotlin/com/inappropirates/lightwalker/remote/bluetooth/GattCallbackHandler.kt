package com.inappropirates.lightwalker.remote.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothGattService
import android.bluetooth.BluetoothProfile
import android.util.Log
import com.inappropirates.lightwalker.remote.util.Util.TAG
import java.util.UUID
import java.util.function.Consumer

@SuppressLint("MissingPermission")
class GattCallbackHandler internal constructor(
    private val statusConsumer: Consumer<Int>,
    private val messageConsumer: Consumer<String>
) : BluetoothGattCallback() {
    private var txChar: BluetoothGattCharacteristic? = null

    val txCharacteristic: BluetoothGattCharacteristic?
        get() = txChar

    override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
        statusConsumer.accept(newState)
        if (newState == BluetoothProfile.STATE_CONNECTED) {
            Log.i(TAG, "service discovery result: " + gatt.discoverServices())
        }
    }

    override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
        if (status == BluetoothGatt.GATT_SUCCESS) {
            Log.i(TAG, "onServicesDiscovered received: $status")

            val service: BluetoothGattService = gatt.getService(UART_SERVICE_UUID)

            txChar = service.getCharacteristic(TX_CHARACTERISTIC_UUID)
            if (txChar == null) {
                Log.e(TAG, "Tx characteristic not found!")
                gatt.disconnect()
                return
            } else {
                txChar!!.setValue("initial load\r")
            }

            val rxChar: BluetoothGattCharacteristic = service.getCharacteristic(RX_CHARACTERISTIC_UUID)
            gatt.setCharacteristicNotification(rxChar, true)
            val descriptor: BluetoothGattDescriptor = rxChar.getDescriptor(
                CHARACTERISTIC_UPDATE_NOTIFICATION_DESCRIPTOR_UUID
            )
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE)
            gatt.writeDescriptor(descriptor)
        } else {
            Log.w(TAG, "onServicesDiscovered received: $status")
        }
    }

    override fun onCharacteristicChanged(
        gatt: BluetoothGatt,
        characteristic: BluetoothGattCharacteristic
    ) {
        val response: String = characteristic.getStringValue(0)
        Log.d(TAG, "on change - received: $response")
        if (response != null && response.length > 0) messageConsumer.accept(response)
    }

    override fun onCharacteristicWrite(
        gatt: BluetoothGatt,
        characteristic: BluetoothGattCharacteristic,
        status: Int
    ) {
        // nothing, just waiting for the ack response
    }

    companion object {
        private val CHARACTERISTIC_UPDATE_NOTIFICATION_DESCRIPTOR_UUID: UUID =
            UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")
        private val UART_SERVICE_UUID: UUID =
            UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e")
        private val TX_CHARACTERISTIC_UUID: UUID =
            UUID.fromString("6e400002-b5a3-f393-e0a9-e50e24dcca9e")
        private val RX_CHARACTERISTIC_UUID: UUID =
            UUID.fromString("6e400003-b5a3-f393-e0a9-e50e24dcca9e")
    }
}
