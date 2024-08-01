package com.inappropirates.lightwalker.remote.bluetooth

import android.content.Context
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

@Composable
fun BtButton(connected: MutableState<Boolean>) {

    val context = LocalContext.current
    Button(
        onClick = { BluetoothUartManager.connect(context) },
        enabled = !connected.value,
        colors = if (connected.value) {
            ButtonDefaults.buttonColors(containerColor = Color.Green)
        } else {
            ButtonDefaults.buttonColors(containerColor = Color.Red)
        }
    ) {
        Text(
            text = if (connected.value) {
                "connected"
            } else {
                "connect"
            }
        )
    }
}