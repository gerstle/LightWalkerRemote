package com.inappropirates.lightwalker.remote.ui

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.inappropirates.lightwalker.remote.modes.ModeManager
import com.inappropirates.lightwalker.remote.ui.theme.RemoteTheme

class MandyActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        ModeManager.setMode(this, "zebra")
        val context: Context = this
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
                                    text = "Mandy Mode",
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center,
                                    color = Color.Blue
                                )
                            }
                        )
                    }
                ) {
                    val radioOptions = listOf("sparkle", "zebra", "rainbow")
                    val (selectedOption, onOptionSelected) = remember { mutableStateOf(radioOptions[1]) }

                    Column {
                        Spacer(modifier = Modifier.padding(top = 104.dp))
                        radioOptions.forEach { text ->
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .selectable(
                                        selected = (text == selectedOption),
                                        onClick = {
                                            onOptionSelected(text)
                                            ModeManager.setMode(context, text)
                                        }
                                    )
                                    .padding(horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = (text == selectedOption),
                                    onClick = {
                                        onOptionSelected(text)
                                        ModeManager.setMode(context, text)
                                    }
                                )
                                Text(
                                    text = text,
                                    style = MaterialTheme.typography.bodyLarge.merge(),
                                    modifier = Modifier.padding(start = 8.dp),
                                )
                            }
                        }
                        ColorTilesGrid(modifier = Modifier)
                    }
                }
            }
        }
    }

}