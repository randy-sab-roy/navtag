package com.glados.navtag.core

data class SafetyLightPreset(
    val name : String,
    val blinkRate: BlinkRate
)

enum class BlinkRate {
    Still, Slow, Fast, SOS, Freedom, Rainbow
}