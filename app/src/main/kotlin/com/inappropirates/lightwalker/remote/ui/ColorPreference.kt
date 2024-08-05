package com.inappropirates.lightwalker.remote.ui

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.inappropirates.lightwalker.remote.bluetooth.Bt
import com.inappropirates.lightwalker.remote.config.Preferences
import com.inappropirates.lightwalker.remote.config.title
import com.inappropirates.lightwalker.remote.util.PropertyFormatter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.zhanghai.compose.preference.BasicPreference
import me.zhanghai.compose.preference.LocalPreferenceTheme
import me.zhanghai.compose.preference.rememberPreferenceState
import java.util.UUID

inline fun LazyListScope.colorPreference(
    pref: Preferences,
    defaultValue: String,
    modifier: Modifier = Modifier.fillMaxWidth(),
    crossinline rememberState: @Composable () -> MutableState<String> = {
        rememberPreferenceState(pref.toString(), defaultValue)
    },
    crossinline enabled: (String) -> Boolean = { true },
) {
    item(key = pref.toString(), contentType = "ColorPreference") {
        val state = rememberState()
        val value by state

        ColorPreference(
            key = pref.toString(),
            state = state,
            title = { Text(pref.title()) },
            modifier = modifier,
            enabled = enabled(value),
        )
    }
}

@Composable
fun ColorPreference(
    key: String,
    state: MutableState<String>,
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    var value by state
    ColorPreference(
        key = key,
        value = value,
        onValueChange = { value = it },
        title = title,
        modifier = modifier,
        enabled = enabled,
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ColorPreference(
    key: String,
    value: String,
    onValueChange: (String) -> Unit,
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val launcher = context.getActivity()!!.registerActivityResultLauncher(
        contract = ActivityResultContracts.StartActivityForResult(),
        callback = { result ->
            result
                .data
                ?.extras
                ?.getString("color")
                ?.let {
                    onValueChange(it)
                    val color = HSVColor.fromAndroidColor(Color.fromHex(it))
                    coroutineScope.launch(Dispatchers.IO) {
                        Bt.send(key, PropertyFormatter.getStringVal(key, color))
                    }
                }
        }
    )

    BasicPreference(
        textContainer = {
            val theme = LocalPreferenceTheme.current
            Column(
                modifier =
                Modifier
                    .padding(
                        theme.padding.copy(
                            start = Dp.Unspecified,
                            bottom = 0.dp
                        )
                    )
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CompositionLocalProvider(
                        LocalMinimumInteractiveComponentEnforcement provides false
                    ) {
                        TitleContainer(title = title, enabled = enabled)
                        Spacer(Modifier.weight(1f))
                        IconButton(
                            onClick = {
                                Intent(context, ColorPickerActivity::class.java)
                                    .also {
                                        it.putExtra("key", key)
                                        it.putExtra("color", value)
                                        launcher.launch(it)
                                    }
                            },
                            modifier =
                            Modifier.padding(
                                theme.padding.copy(start = theme.horizontalSpacing)
                                    .offset((-12).dp)
                            ),
                            enabled = enabled,
                            colors =
                            IconButtonDefaults.iconButtonColors(
                                contentColor = theme.iconColor,
                                disabledContentColor = theme.iconColor.copy(alpha = theme.disabledOpacity)
                            ),
                            content = {
                                Surface(
                                    modifier = Modifier
                                        .width(150.dp)
                                        .height(50.dp)
                                        .padding(5.dp)
                                        .border(1.dp, Color.Black),
                                    color = Color.fromHex(value)
                                ) {}
                            }
                        )
                    }
                }
            }
        },
        modifier = modifier,
        enabled = enabled,
    )
}

@Composable
fun TitleContainer(title: @Composable () -> Unit, enabled: Boolean) {
    val theme = LocalPreferenceTheme.current
    CompositionLocalProvider(
        LocalContentColor provides
                theme.titleColor.let { if (enabled) it else it.copy(alpha = theme.disabledOpacity) }
    ) {
        ProvideTextStyle(value = theme.titleTextStyle, content = title)
    }
}

@Composable
internal fun PaddingValues.copy(
    start: Dp = Dp.Unspecified,
    top: Dp = Dp.Unspecified,
    end: Dp = Dp.Unspecified,
    bottom: Dp = Dp.Unspecified
): PaddingValues = CopiedPaddingValues(start, top, end, bottom, this)

@Stable
private class CopiedPaddingValues(
    private val start: Dp,
    private val top: Dp,
    private val end: Dp,
    private val bottom: Dp,
    private val paddingValues: PaddingValues
) : PaddingValues {
    override fun calculateLeftPadding(layoutDirection: LayoutDirection): Dp =
        (if (layoutDirection == LayoutDirection.Ltr) start else end).takeIf { it != Dp.Unspecified }
            ?: paddingValues.calculateLeftPadding(layoutDirection)

    override fun calculateTopPadding(): Dp =
        top.takeIf { it != Dp.Unspecified } ?: paddingValues.calculateTopPadding()

    override fun calculateRightPadding(layoutDirection: LayoutDirection): Dp =
        (if (layoutDirection == LayoutDirection.Ltr) end else start).takeIf { it != Dp.Unspecified }
            ?: paddingValues.calculateRightPadding(layoutDirection)

    override fun calculateBottomPadding(): Dp =
        bottom.takeIf { it != Dp.Unspecified } ?: paddingValues.calculateBottomPadding()

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other !is CopiedPaddingValues) {
            return false
        }
        return start == other.start &&
                top == other.top &&
                end == other.end &&
                bottom == other.bottom &&
                paddingValues == other.paddingValues
    }

    override fun hashCode(): Int {
        var result = start.hashCode()
        result = 31 * result + top.hashCode()
        result = 31 * result + end.hashCode()
        result = 31 * result + bottom.hashCode()
        result = 31 * result + paddingValues.hashCode()
        return result
    }

    override fun toString(): String {
        return "Copied($start, $top, $end, $bottom, $paddingValues)"
    }
}

@Composable
internal fun PaddingValues.offset(all: Dp = 0.dp): PaddingValues = offset(all, all, all, all)

@Composable
internal fun PaddingValues.offset(
    start: Dp = 0.dp,
    top: Dp = 0.dp,
    end: Dp = 0.dp,
    bottom: Dp = 0.dp
): PaddingValues = OffsetPaddingValues(start, top, end, bottom, this)

@Stable
private class OffsetPaddingValues(
    private val start: Dp,
    private val top: Dp,
    private val end: Dp,
    private val bottom: Dp,
    private val paddingValues: PaddingValues
) : PaddingValues {
    override fun calculateLeftPadding(layoutDirection: LayoutDirection): Dp =
        paddingValues.calculateLeftPadding(layoutDirection) +
                (if (layoutDirection == LayoutDirection.Ltr) start else end)

    override fun calculateTopPadding(): Dp = paddingValues.calculateTopPadding() + top

    override fun calculateRightPadding(layoutDirection: LayoutDirection): Dp =
        paddingValues.calculateRightPadding(layoutDirection) +
                (if (layoutDirection == LayoutDirection.Ltr) end else start)

    override fun calculateBottomPadding(): Dp = paddingValues.calculateBottomPadding() + bottom

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other !is OffsetPaddingValues) {
            return false
        }
        return start == other.start &&
                top == other.top &&
                end == other.end &&
                bottom == other.bottom &&
                paddingValues == other.paddingValues
    }

    override fun hashCode(): Int {
        var result = start.hashCode()
        result = 31 * result + top.hashCode()
        result = 31 * result + end.hashCode()
        result = 31 * result + bottom.hashCode()
        result = 31 * result + paddingValues.hashCode()
        return result
    }

    override fun toString(): String {
        return "Offset($start, $top, $end, $bottom, $paddingValues)"
    }
}

fun Context.getActivity(): ComponentActivity? = when (this) {
    is ComponentActivity -> this
    is ContextWrapper -> baseContext.getActivity()
    else -> null
}

fun <I, O> ComponentActivity.registerActivityResultLauncher(
    contract: ActivityResultContract<I, O>,
    callback: ActivityResultCallback<O>
): ActivityResultLauncher<I> {
    val key = UUID.randomUUID().toString()
    return activityResultRegistry.register(key, contract, callback)
}