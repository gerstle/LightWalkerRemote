package com.inappropirates.lightwalker.remote.modes

data class Mode(
    val name: String,
    val enabled: Boolean = true,
    val configActivity: Class<*>? = null
)
