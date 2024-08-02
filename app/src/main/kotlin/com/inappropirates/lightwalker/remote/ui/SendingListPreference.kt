package com.inappropirates.lightwalker.remote.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.inappropirates.lightwalker.remote.bluetooth.BluetoothUartManager
import com.inappropirates.lightwalker.remote.config.Preferences
import com.inappropirates.lightwalker.remote.config.title
import com.inappropirates.lightwalker.remote.util.PropertyFormatter
import me.zhanghai.compose.preference.ListPreferenceType
import me.zhanghai.compose.preference.rememberPreferenceState

inline fun LazyListScope.sendingListPreference(
    pref: Preferences,
    defaultValue: String,
    values: List<String>,
    modifier: Modifier = Modifier.fillMaxWidth(),
    crossinline rememberState: @Composable () -> MutableState<String> = {
        rememberPreferenceState(pref.toString(), defaultValue)
    },
    crossinline enabled: (String) -> Boolean = { true },
    noinline icon: @Composable ((String) -> Unit)? = null,
    noinline summary: @Composable ((String) -> Unit)? = null,
    type: ListPreferenceType = ListPreferenceType.ALERT_DIALOG,
    noinline valueToText: (String) -> AnnotatedString = { AnnotatedString(it) },
    noinline item: @Composable (value: String, currentValue: String, onClick: () -> Unit) -> Unit =
        ListPreferenceDefaults.item(type, valueToText)
) {
    item(key = pref.toString(), contentType = "ListPreference") {
        val state = rememberState()
        val value by state
        SendingListPreference(
            key = pref.toString(),
            state = state,
            values = values,
            title = { Text(pref.title()) },
            modifier = modifier,
            enabled = enabled(value),
            icon = icon?.let { { it(value) } },
            summary = summary?.let { { it(value) } },
            type = type,
            valueToText = valueToText,
            item = item
        )
    }
}

@Composable
fun SendingListPreference(
    key: String,
    state: MutableState<String>,
    values: List<String>,
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: @Composable (() -> Unit)? = null,
    summary: @Composable (() -> Unit)? = null,
    type: ListPreferenceType = ListPreferenceType.ALERT_DIALOG,
    valueToText: (String) -> AnnotatedString = { AnnotatedString(it) },
    item: @Composable (value: String, currentValue: String, onClick: () -> Unit) -> Unit =
        ListPreferenceDefaults.item(type, valueToText)
) {
    var value by state
    me.zhanghai.compose.preference.ListPreference(
        value = value,
        onValueChange = {
            val index = values.indexOf(it)
            BluetoothUartManager.sendSetting(key, PropertyFormatter.getStringVal(key, index))
            value = it
        },
        values = values,
        title = title,
        modifier = modifier,
        enabled = enabled,
        icon = icon,
        summary = summary,
        type = type,
        valueToText = valueToText,
        item = item
    )
}
@PublishedApi
internal object ListPreferenceDefaults {
    fun <T> item(
        type: ListPreferenceType,
        valueToText: (T) -> AnnotatedString
    ): @Composable (value: T, currentValue: T, onClick: () -> Unit) -> Unit =
        when (type) {
            ListPreferenceType.ALERT_DIALOG -> {
                { value, currentValue, onClick ->
                    DialogItem(value, currentValue, valueToText, onClick)
                }
            }
            ListPreferenceType.DROPDOWN_MENU -> {
                { value, currentValue, onClick ->
                    DropdownMenuItem(value, currentValue, valueToText, onClick)
                }
            }
        }

    @Composable
    private fun <T> DialogItem(
        value: T,
        currentValue: T,
        valueToText: (T) -> AnnotatedString,
        onClick: () -> Unit
    ) {
        val selected = value == currentValue
        Row(
            modifier =
            Modifier.fillMaxWidth()
                .heightIn(min = 48.dp)
                .selectable(selected, true, Role.RadioButton, onClick)
                .padding(horizontal = 24.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(selected = selected, onClick = null)
            Spacer(modifier = Modifier.width(24.dp))
            Text(
                text = valueToText(value),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }

    @Composable
    private fun <T> DropdownMenuItem(
        value: T,
        currentValue: T,
        valueToText: (T) -> AnnotatedString,
        onClick: () -> Unit
    ) {
        androidx.compose.material3.DropdownMenuItem(
            text = { Text(text = valueToText(value)) },
            onClick = onClick,
            modifier =
            Modifier.background(
                if (value == currentValue) MaterialTheme.colorScheme.secondaryContainer
                else Color.Transparent
            ),
            colors = MenuDefaults.itemColors()
        )
    }
}
