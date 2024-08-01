package com.inappropirates.lightwalker.remote

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.inappropirates.lightwalker.remote.bluetooth.BluetoothStatusHandler
import com.inappropirates.lightwalker.remote.bluetooth.BluetoothUartManager
import com.inappropirates.lightwalker.remote.bluetooth.BtButton
import com.inappropirates.lightwalker.remote.modes.ModeListView
import com.inappropirates.lightwalker.remote.modes.ModeManager
import com.inappropirates.lightwalker.remote.ui.theme.RemoteTheme

class MainActivity : ComponentActivity() {


    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RemoteTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(
                                    text = "LightWalker Remote",
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center,
                                    color = Color.Blue
                                )
                            }
                        )
                    }
                ) {
                    MainContainer(this)
                    checkPermission("android.permission.BLUETOOTH", 1);
                    checkPermission("android.permission.BLUETOOTH_CONNECT", 2);
                    checkPermission("android.permission.BLUETOOTH_SCAN", 3);
                }
            }
        }
    }

    @Composable
    fun MainContainer(context: Context) {
        val connected = remember { mutableStateOf(false) }
        val mode = remember { ModeManager.modeState }

        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    ModeListView(mode)
                    BtButton(connected)

                    BluetoothStatusHandler(context, connected)
                        .also {
                            BluetoothUartManager.setHandler(it)
                            BluetoothUartManager.updateStatus()
                        }
                }
            }
        }
    }


    private fun checkPermission(permission: String, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(
                this@MainActivity,
                permission
            ) == PackageManager.PERMISSION_DENIED
        ) {
            ActivityCompat.requestPermissions(this@MainActivity, arrayOf(permission), requestCode)
        }
    }
}