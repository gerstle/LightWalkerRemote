package com.inappropirates.lightwalker.remote

import android.Manifest

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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.inappropirates.lightwalker.remote.bluetooth.Bt
import com.inappropirates.lightwalker.remote.modes.ModeListView
import com.inappropirates.lightwalker.remote.modes.ModeManager
import com.inappropirates.lightwalker.remote.ui.theme.RemoteTheme
import quevedo.soares.leandro.blemadeeasy.BLE

class MainActivity : ComponentActivity() {
    private val connected = mutableStateOf(false)

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
                    MainContainer()
                    checkPermission(Manifest.permission.BLUETOOTH, 1)
                    checkPermission(Manifest.permission.BLUETOOTH_SCAN, 2)
                    checkPermission(Manifest.permission.BLUETOOTH_CONNECT, 3)
                    checkPermission(Manifest.permission.BLUETOOTH_ADMIN, 4)
                }
            }
        }

        BLE(this)
            .apply {
                // verbose = true// Optional variable for debugging purposes
            }
            .also { Bt.connect(it, this@MainActivity, connected) }
    }

    @Composable
    fun MainContainer() {
        val connected = remember { connected }
        val mode = remember { ModeManager.modeState }

        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    ModeListView(mode)

                    Button(
                        onClick = {},
                        enabled = false,
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
                                "disconnected"
                            }
                        )
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