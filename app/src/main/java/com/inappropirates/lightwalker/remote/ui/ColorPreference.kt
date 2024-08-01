package com.inappropirates.lightwalker.remote.ui

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.registerForActivityResult
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
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
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat.startActivity
import com.github.skydoves.colorpicker.compose.AlphaTile
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import me.zhanghai.compose.preference.BasicPreference
import me.zhanghai.compose.preference.LocalPreferenceTheme
import me.zhanghai.compose.preference.rememberPreferenceState
import java.util.UUID


inline fun LazyListScope.colorPreference(
    key: String,
    defaultValue: String,
    crossinline title: @Composable (String) -> Unit,
    modifier: Modifier = Modifier.fillMaxWidth(),
    crossinline rememberState: @Composable () -> MutableState<String> = {
        rememberPreferenceState(key, defaultValue)
    },
    crossinline rememberColorState: @Composable (String) -> MutableState<String> = {
        remember { mutableStateOf(it) }
    },
    crossinline enabled: (String) -> Boolean = { true },
    noinline icon: @Composable ((String) -> Unit)? = null,
    noinline summary: @Composable ((String) -> Unit)? = null,
    noinline valueText: @Composable ((String) -> Unit)? = null,
    resultLauncher: ActivityResultLauncher<Intent>? = null
) {
    item(key = key, contentType = "ColorPreference") {
        val state = rememberState()
        val value by state
        val sliderState = rememberColorState(value)
        val sliderValue by sliderState

        ColorPreference(
            key = key,
            state = state,
            title = { title(sliderValue) },
            modifier = modifier,
            sliderState = sliderState,
            enabled = enabled(value),
            icon = icon?.let { { it(sliderValue) } },
            summary = summary?.let { { it(sliderValue) } },
            valueText = valueText?.let { { it(sliderValue) } }
        )
    }
}

@Composable
fun ColorPreference(
    key: String,
    state: MutableState<String>,
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    sliderState: MutableState<String> = remember { mutableStateOf(state.value) },
    enabled: Boolean = true,
    icon: @Composable (() -> Unit)? = null,
    summary: @Composable (() -> Unit)? = null,
    valueText: @Composable (() -> Unit)? = null
) {
    var value by state
    var sliderValue by sliderState
    ColorPreference(
        key = key,
        value = value,
        onValueChange = { value = it },
        sliderValue = sliderValue,
        onSliderValueChange = { sliderValue = it },
        title = title,
        modifier = modifier,
        enabled = enabled,
        icon = icon,
        summary = summary,
        valueText = valueText
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ColorPreference(
    key: String,
    value: String,
    onValueChange: (String) -> Unit,
    sliderValue: String,
    onSliderValueChange: (String) -> Unit,
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: @Composable (() -> Unit)? = null,
    summary: @Composable (() -> Unit)? = null,
    valueText: @Composable (() -> Unit)? = null
) {
    val context = LocalContext.current
    var lastValue = remember { mutableStateOf(value) }

    val launcher = context.getActivity()!!.registerActivityResultLauncher(
        contract = ActivityResultContracts.StartActivityForResult(),
        callback = { result ->
            result
                .data
                ?.extras
                ?.getString("color")
                ?.let {
                    onValueChange(it)
                }
        }
    )


    SideEffect {
        if (value != lastValue.value) {
            onSliderValueChange(value)
            lastValue.value = value
        }
    }

    BasicPreference(
        textContainer = {
            Column {
                val theme = LocalPreferenceTheme.current
                Column(
                    modifier =
                    Modifier.padding(
                        theme.padding.copy(
                            start = if (icon != null) 8.dp else Dp.Unspecified,
                            bottom = 0.dp
                        )
                    )
                ) {
                    TitleContainer(title = title, enabled = enabled)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CompositionLocalProvider(
                        LocalMinimumInteractiveComponentEnforcement provides false
                    ) {
                        // onValueChangeFinished() may be invoked before a recomposition has
                        // happened for onValueChange(), for example in the clicking case, so make
                        // onValueChange() share the latest value to onValueChangeFinished().
                        var latestSliderValue = sliderValue
                        IconButton(
                            onClick = {
                                Intent(context, ColorPickerActivity::class.java)
                                    .also {
                                        it.putExtra("key", key)
                                        launcher.launch(it)
                                    }
                            },
                            modifier =
                            Modifier.padding(
                                theme.padding.copy(start = theme.horizontalSpacing).offset((-12).dp)
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
                                        .padding(5.dp),
                                    color = Color.fromHex(lastValue.value)
                                ) {}
                            }
                        )
                    }
                }
            }
        },
        modifier = modifier,
        enabled = enabled,
        iconContainer = {
            IconContainer(
                icon = icon,
                enabled = enabled,
                excludedEndPadding = 8.dp
            )
        }
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
fun IconContainer(
    icon: @Composable (() -> Unit)?,
    enabled: Boolean,
    excludedEndPadding: Dp = 0.dp
) {
    if (icon != null) {
        val theme = LocalPreferenceTheme.current
        Box(
            modifier =
            Modifier
                .widthIn(min = theme.iconContainerMinWidth - excludedEndPadding)
                .padding(theme.padding.copy(end = 0.dp)),
            contentAlignment = Alignment.CenterStart
        ) {
            CompositionLocalProvider(
                LocalContentColor provides
                        theme.iconColor.let {
                            if (enabled) it else it.copy(alpha = theme.disabledOpacity)
                        },
                content = icon
            )
        }
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
        if (other !is com.inappropirates.lightwalker.remote.ui.CopiedPaddingValues) {
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