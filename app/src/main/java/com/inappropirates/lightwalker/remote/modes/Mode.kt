package com.inappropirates.lightwalker.remote.modes

import androidx.compose.runtime.Composable

data class Mode(
    val name: String,
    val enabled: Boolean = true,
    val configActivity: Class<*>? = null
)
