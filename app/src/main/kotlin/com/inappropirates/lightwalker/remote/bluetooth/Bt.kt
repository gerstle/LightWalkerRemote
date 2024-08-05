package com.inappropirates.lightwalker.remote.bluetooth

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.MutableState
import com.inappropirates.lightwalker.remote.config.Preferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import quevedo.soares.leandro.blemadeeasy.BLE
import quevedo.soares.leandro.blemadeeasy.BluetoothConnection

object Bt {
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    var connection: BluetoothConnection? = null
    private val TAG = this::class.java.simpleName
    const val LIGHTWALKER_FOUR_POINT_OH = "C1:BD:37:71:DE:BC"
    private val UART_SERVICE_UUID = "6e400001-b5a3-f393-e0a9-e50e24dcca9e"
    private val TX_CHARACTERISTIC_UUID = "6e400002-b5a3-f393-e0a9-e50e24dcca9e"
    private val RX_CHARACTERISTIC_UUID = "6e400003-b5a3-f393-e0a9-e50e24dcca9e"

    @SuppressLint("MissingPermission")
    fun connect(ble: BLE, context: Context, connected: MutableState<Boolean>) {
        ble.verifyBluetoothAdapterStateAsync { active ->
            if (active) {
                coroutineScope.launch {
                    connection = ble.scanFor(macAddress = LIGHTWALKER_FOUR_POINT_OH, service = UART_SERVICE_UUID)
                    connection?.let {
                        connected.value = true
                        it.observe(RX_CHARACTERISTIC_UUID) {
                            Log.i(TAG, "------>   received ${String(it)}!")
                        }
                        it.onConnect = {
                            connected.value = true
                        }
                        it.onDisconnect = {
                            connected.value = false
                        }
                    }
                }
            } else {
                Toast
                    .makeText(context, "Turn on bluetooth!", Toast.LENGTH_SHORT)
                    .show()
            }
        }

    }

    suspend fun send(key: String, value: String) {
        val preference: Preferences = Preferences.valueOf(key)
        send("${preference.ordinal}=${value}")
        Log.i(TAG, "send: sent ${key}(${preference.ordinal})=${value}")
    }

    suspend fun send(msg: String): Boolean {
        connection
            ?.let { connection ->
                val job = coroutineScope.async {
                    connection.write(TX_CHARACTERISTIC_UUID, "${msg}\r")
                }
                job.await()
                return true
            } ?: run { Log.w(TAG, "send called with null connection") }

        return false
    }
}