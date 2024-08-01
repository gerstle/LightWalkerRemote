package com.inappropirates.lightwalker.remote.bluetooth

import android.bluetooth.BluetoothProfile
import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.os.Message
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.compose.runtime.MutableState
import com.inappropirates.lightwalker.remote.util.Util.TAG

class BluetoothStatusHandler(private val context: Context, private var connected: MutableState<Boolean>) : Handler() {
    override fun handleMessage(msg: Message) {
        when (BluetoothMessageEnum.entries[msg.what]) {
            BluetoothMessageEnum.STATE_CHANGE -> {
                Log.i(TAG, "STATE_CHANGE: " + msg.arg1)
                when (msg.arg1) {
                    BluetoothProfile.STATE_DISCONNECTED ->                         //Toast.makeText(context, "Disconnected", Toast.LENGTH_SHORT).show();
                        connected.value = false // setConnectedButton(false)

                    BluetoothProfile.STATE_CONNECTING -> {}
                    BluetoothProfile.STATE_CONNECTED -> {
                        connected.value = true // setConnectedButton(true)
                        if (msg.arg2 == 1) BluetoothUartManager.sendAllCurrentSettings(context)
                    }
                }
            }

            BluetoothMessageEnum.READ -> {
                val message = msg.obj as String
                Log.d(TAG, "message received: '$message'")
            }

            BluetoothMessageEnum.TOAST -> Toast.makeText(
                context,
                msg.data.getString(TOAST),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // be careful calling this from non-ui threads...
//    private fun setConnectedButton(connected: Boolean) {
//        if (connected) {
//            button.setBackgroundColor(Color.argb(255, 144, 210, 142)) // green
//            button.text = "connected"
//        } else {
//            button.setBackgroundColor(Color.argb(255, 206, 106, 108)) // red
//            button.isEnabled = true
//            button.text = "connect"
//        }
//    }

    companion object {
        const val TOAST: String = "toast"
    }
}
