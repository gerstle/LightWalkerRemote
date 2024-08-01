package com.inappropirates.lightwalker.remote.modes

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.inappropirates.lightwalker.remote.ui.ConfigActivity


@Composable
fun ModeListView(modeState: MutableState<Mode>) {
    LazyColumn(
        contentPadding = PaddingValues(top = 120.dp)
    ) {
        itemsIndexed(ModeManager.modes.filter { it.enabled }) { index, mode ->
            val context = LocalContext.current
            Card(
                onClick = {
                    Toast.makeText(
                        context,
                        mode.name + " selected..",
                        Toast.LENGTH_SHORT,
                    ).show()

                    ModeManager.setMode(context, mode)
                },
                modifier = Modifier.padding(8.dp),

                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (modeState.value == mode) {
                        Color.Magenta
                    } else {
                        Color.DarkGray
                    }
                ),
            )
            {
                Row(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                ) {
                    Spacer(modifier = Modifier.width(5.dp))

                    Text(
                        fontSize = 30.sp,
                        text = mode.name,
                        modifier = Modifier.padding(4.dp),
                        color = Color.Blue,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(Modifier.weight(1f))

                    mode
                        .configActivity
                        ?.let {
                            Button(
                                onClick = {
                                    Intent(context, mode.configActivity)
                                        .also { context.startActivity(it) }
                                }) {
                                Text(text = "configure")
                            }
                        }
                }
            }
        }
    }
}
