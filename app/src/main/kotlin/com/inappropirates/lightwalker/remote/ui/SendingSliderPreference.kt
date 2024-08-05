package com.inappropirates.lightwalker.remote.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.inappropirates.lightwalker.remote.bluetooth.Bt
import com.inappropirates.lightwalker.remote.config.Preferences
import com.inappropirates.lightwalker.remote.config.title
import com.inappropirates.lightwalker.remote.util.PropertyFormatter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.zhanghai.compose.preference.SliderPreference
import me.zhanghai.compose.preference.rememberPreferenceState

inline fun LazyListScope.sendingSliderPreference(
    pref: Preferences,
    defaultValue: Float,
    modifier: Modifier = Modifier.fillMaxWidth(),
    crossinline rememberState: @Composable () -> MutableState<Float> = {
        rememberPreferenceState(pref.toString(), defaultValue)
    },
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    crossinline rememberSliderState: @Composable (Float) -> MutableState<Float> = {
        remember { mutableFloatStateOf(it) }
    },
    crossinline enabled: (Float) -> Boolean = { true },
    noinline summary: @Composable ((Float) -> Unit)? = { Text("%.0f".format(it))},
) {
    item(key = pref.toString(), contentType = "SliderPreference") {
        val state = rememberState()
        val value by state
        val sliderState = rememberSliderState(value)
        val sliderValue by sliderState
        SendingSliderPreference(
            key = pref.toString(),
            state = state,
            title = { Text(pref.title()) },
            modifier = modifier,
            valueRange = valueRange,
            sliderState = sliderState,
            enabled = enabled(value),
            summary = summary?.let { { it(sliderValue) } },
        )
    }
}

@Composable
fun SendingSliderPreference(
    key: String,
    state: MutableState<Float>,
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    valueSteps: Int = 0,
    sliderState: MutableState<Float> = remember { mutableFloatStateOf(state.value) },
    enabled: Boolean = true,
    icon: @Composable (() -> Unit)? = null,
    summary: @Composable (() -> Unit)? = null,
    valueText: @Composable (() -> Unit)? = null
) {
    var value by state
    var sliderValue by sliderState
    val coroutineScope = rememberCoroutineScope()
    SliderPreference(
        value = value,
        onValueChange = {

            coroutineScope.launch(Dispatchers.IO) {
                Bt.send(key, PropertyFormatter.getStringVal(key, it))
            }
            value = it
        },
        sliderValue = sliderValue,
        onSliderValueChange = {
            sliderValue = it
        },
        title = title,
        modifier = modifier,
        valueRange = valueRange,
        valueSteps = valueSteps,
        enabled = enabled,
        icon = icon,
        summary = summary,
        valueText = valueText
    )
}
