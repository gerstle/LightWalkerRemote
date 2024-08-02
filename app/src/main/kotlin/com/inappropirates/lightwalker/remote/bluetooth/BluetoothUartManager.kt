package com.inappropirates.lightwalker.remote.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.preference.PreferenceManager
import com.inappropirates.lightwalker.remote.config.Preferences
import com.inappropirates.lightwalker.remote.modes.Mode
import com.inappropirates.lightwalker.remote.modes.ModeManager
import com.inappropirates.lightwalker.remote.modes.Modes
import com.inappropirates.lightwalker.remote.util.PropertyFormatter
import com.inappropirates.lightwalker.remote.util.Util.TAG
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit
import java.util.function.Consumer
import kotlin.math.min

object BluetoothUartManager {
    private const val LIGHTWALKER_TWO_POINT_OH = "00:18:96:B0:01:8F"
    private const val LIGHTWALKER_THREE_POINT_OH = "98:76:B6:00:8A:E0"
    private const val LIGHTWALKER_FOUR_POINT_OH = "C1:BD:37:71:DE:BC"
    private const val BLE_SEND_BYTE_LIMIT = 20

    private var bluetoothGatt: BluetoothGatt? = null
    private var device: BluetoothDevice? = null
    private var bluetoothHandler: Handler? = null
    private var bluetoothState = BluetoothProfile.STATE_DISCONNECTED
    private val writeLock = Semaphore(1)

    private val gattCallback = GattCallbackHandler(
        Consumer { s: Int ->
            Log.w(TAG, "bt state change even: $s")
            if (bluetoothHandler == null) return@Consumer
            bluetoothState = s
            bluetoothHandler!!
                .obtainMessage(BluetoothMessageEnum.STATE_CHANGE.ordinal, s, 1)
                .sendToTarget()
        }
    ) { s: String ->
        // s should always be a non-blank string
        Log.w(TAG, "bt received $s")
        if (s.startsWith("K")) writeLock.release()
    }

    fun setHandler(statusHandler: Handler?) {
        this.bluetoothHandler = statusHandler
    }

    @SuppressLint("MissingPermission")
    fun connect(context: Context) {
        val adapter: BluetoothAdapter =
            getSystemService(context, BluetoothManager::class.java)!!.adapter

        if (bluetoothState != BluetoothProfile.STATE_DISCONNECTED) {
            Log.w(TAG, "cannot connect, state is not disconnected. Current state: $bluetoothState")
            return
        }
        // Always disconnect discovery because it will slow down a connection
        adapter.cancelDiscovery()
        val devices: Set<BluetoothDevice> = adapter.getBondedDevices()
        for (device in devices) {
            Log.d(
                TAG,
                "connect: looking at device " + device.getName() + " - " + device.getAddress()
            )
            if (device.getAddress() == LIGHTWALKER_FOUR_POINT_OH) this.device = device
        }

        if (device == null) {
            sendHandlerToast("LightWalker is MIA")
            return
        }

        Log.i(TAG, "Attempting to connect to " + device!!.getName())
        Toast.makeText(
            context,
            "attempting to connect...",
            Toast.LENGTH_SHORT,
        ).show()
        try {
            if (bluetoothGatt == null) {
                bluetoothGatt = device!!.connectGatt(context.applicationContext, true, gattCallback)
            } else if (!bluetoothGatt!!.connect()) {
                bluetoothState = BluetoothProfile.STATE_DISCONNECTED
            }
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            Log.e(TAG, "failed to connect to GATT", e)
            bluetoothState = BluetoothProfile.STATE_DISCONNECTED

            bluetoothGatt?.let {
                it.disconnect()
                bluetoothGatt = null
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun disconnect() {
        bluetoothGatt?.disconnect()
    }

    private fun sendHandlerToast(message: String) {
        if (bluetoothHandler == null) return

        val msg = bluetoothHandler!!.obtainMessage(BluetoothMessageEnum.TOAST.ordinal)
        val bundle = Bundle()
        bundle.putString(BluetoothStatusHandler.TOAST, message)
        msg.data = bundle
        bluetoothHandler!!.sendMessage(msg)
    }

    fun updateStatus() {
        if (bluetoothHandler == null) return

        bluetoothHandler!!
            .obtainMessage(BluetoothMessageEnum.STATE_CHANGE.ordinal, bluetoothState, 0)
            .sendToTarget()
    }

    fun sendSetting(key: String, value: String) {
        val preference: Preferences = Preferences.valueOf(key)
        val logMessage =
            java.lang.String.format("sending %s(%d)=%s... ", key, preference.ordinal, value)
        if (send(java.lang.String.format("%d=%s\r", preference.ordinal, value))) Log.d(
            TAG,
            logMessage + "sent!"
        )
        else Log.d(TAG, logMessage + "failed.")
    }

    @SuppressLint("MissingPermission")
    private fun send(message: String): Boolean {
        if (bluetoothGatt != null && gattCallback.txCharacteristic != null) {
            try {
                Log.d(TAG, "writing $message")
                val bytes = message.toByteArray()

                var i = 0
                while (i < bytes.size) {
                    val partitionLength =
                        min(BLE_SEND_BYTE_LIMIT.toDouble(), (bytes.size - i).toDouble())
                            .toInt()
                    val partition = ByteArray(partitionLength)
                    for (j in 0 until partitionLength) {
                        partition[j] = bytes[i + j]
                    }

                    var attempts = 0
                    Log.i(TAG, "available lock permits: " + writeLock.availablePermits())
                    var lockSucceeded = writeLock.tryAcquire(5, TimeUnit.SECONDS)
                    while (!lockSucceeded && attempts < 3) {
                        Log.w(TAG, "Failed to acquire write lock after 5s, releasing and retrying")
                        writeLock.release()
                        lockSucceeded = writeLock.tryAcquire(5, TimeUnit.SECONDS)
                        attempts++
                    }

                    if (!lockSucceeded) {
                        Log.e(TAG, "Failed to acquire lock after 3 attempts, unable to send")
                        return false
                    }

                    bluetoothGatt!!.writeCharacteristic(
                        gattCallback.txCharacteristic!!,
                        partition,
                        WRITE_TYPE_DEFAULT
                    )
                    i += BLE_SEND_BYTE_LIMIT
                }
                return true
            } catch (e: InterruptedException) {
                Log.e(TAG, "failed to send, interrupt message" + e.message)
            }
        } else {
            Log.d(TAG, "ble not available, can't send $message")
        }

        return false
    }

    fun sendAllCurrentSettings(context: Context) {
        val mode: Mode = ModeManager.modeState.value
        if (mode.enabled && bluetoothState == BluetoothProfile.STATE_CONNECTED) {
            Thread {
                Log.d(TAG, "setting mode " + mode.name)
                val preferences: SharedPreferences =
                    PreferenceManager.getDefaultSharedPreferences(context)

                preferences
                    .all
                    .entries
                    .filter { it.key.startsWith("main") || it.key.startsWith(mode.name) }
                    .forEach { pref ->
                        pref.value?.let { v ->
                            PropertyFormatter.getStringVal(pref.key, v)?.let {
                                sendSetting(pref.key, it)
                            }
                        }
                    }

                sendSetting(
                    "mode",
                    Integer.valueOf(Modes.valueOf(mode.name).ordinal).toString()
                )
            }.start()
        }
    }
}
