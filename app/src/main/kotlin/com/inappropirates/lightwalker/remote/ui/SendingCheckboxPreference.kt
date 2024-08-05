package com.inappropirates.lightwalker.remote.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.inappropirates.lightwalker.remote.bluetooth.Bt
import com.inappropirates.lightwalker.remote.config.Preferences
import com.inappropirates.lightwalker.remote.config.title
import com.inappropirates.lightwalker.remote.util.PropertyFormatter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.zhanghai.compose.preference.rememberPreferenceState

inline fun LazyListScope.sendingCheckboxPreference(
    pref: Preferences,
    defaultValue: Boolean,
    modifier: Modifier = Modifier.fillMaxWidth(),
    crossinline rememberState: @Composable () -> MutableState<Boolean> = {
        rememberPreferenceState(pref.toString(), defaultValue)
    },
    crossinline enabled: (Boolean) -> Boolean = { true },
    noinline icon: @Composable ((Boolean) -> Unit)? = null,
    noinline summary: @Composable ((Boolean) -> Unit)? = null
) {
    item(key = pref.toString(), contentType = "CheckboxPreference") {
        val state = rememberState()
        val value by state
        SendingCheckboxPreference(
            key = pref.toString(),
            state = state,
            title = { Text(pref.title()) },
            modifier = modifier,
            enabled = enabled(value),
            icon = icon?.let { { it(value) } },
            summary = summary?.let { { it(value) } }
        )
    }
}

@Composable
fun SendingCheckboxPreference(
    key: String,
    state: MutableState<Boolean>,
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: @Composable (() -> Unit)? = null,
    summary: @Composable (() -> Unit)? = null
) {
    var value by state
    val coroutineScope = rememberCoroutineScope()
    me.zhanghai.compose.preference.CheckboxPreference(
        value = value,
        onValueChange = {
            coroutineScope.launch(Dispatchers.IO) {
                Bt.send(key, PropertyFormatter.getStringVal(key, it))
            }
            value = it
        },
        title = title,
        modifier = modifier,
        enabled = enabled,
        icon = icon,
        summary = summary
    )
}
