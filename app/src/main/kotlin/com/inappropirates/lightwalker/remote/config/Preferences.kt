package com.inappropirates.lightwalker.remote.config

import org.apache.commons.lang3.StringUtils

enum class Preferences {
    // ------------------------------------------------------------------------
    // Main
    // ------------------------------------------------------------------------
    mainMaxBrightness,
    mainDefaultMode,

    //------------------------------------------------------------------------
    // Mode
    //------------------------------------------------------------------------
    mode,

    //------------------------------------------------------------------------
    // Sparkle
    //------------------------------------------------------------------------
    sparkleFadeRate,
    sparkleFlashLength,
    sparkleSparkleLength,
    sparkleFootFlashColor,
    sparkleSparkleColor,
    sparkleMinValue,

    //------------------------------------------------------------------------
    // Equalizer
    //------------------------------------------------------------------------
    eqMode,
    eqColor,
    eqAllBands,
    eqMinValue,
    eqLevel,

    //------------------------------------------------------------------------
    // Gravity
    //------------------------------------------------------------------------
    gravityRotate,
    gravityMinValue,

    //------------------------------------------------------------------------
    // Bubble
    //------------------------------------------------------------------------
    bubbleBackgroundColor,
    bubbleBubbleColor,
    bubbleSpeed,
    bubbleWidth,
    bubbleTrail,

    //------------------------------------------------------------------------
    // Rainbow
    //------------------------------------------------------------------------
    rainbowMode,
    rainbowMinValue,
    rainbowDelay,

    //------------------------------------------------------------------------
    // Zebra
    //------------------------------------------------------------------------
    zebraColorOne,
    zebraColorTwo,

    //------------------------------------------------------------------------
    // Chaos
    //------------------------------------------------------------------------
    chaosMinValue,
    chaosStepLength,
    chaosSpeed,
    chaosColor,
    chaosSwing,
    chaosSparse,

    //------------------------------------------------------------------------
    // Flames
    //------------------------------------------------------------------------
    flamesStepMillis,
    flamesDelay
}

fun Preferences.title(): String =
    this
        .toString()
        .let { StringUtils.splitByCharacterTypeCamelCase(it) }
        .filterIndexed { index, _ -> index > 0 }
        .joinToString(" ")
