package com.glados.navtag.core

data class NavTagPreset(
    val name : String,
    val mode : NavTagMode,
    var blinkRate: BlinkRate?
)

enum class NavTagMode {
    Off,
    SafetyLight,
    Destination,
    Commute,
    Running
}
