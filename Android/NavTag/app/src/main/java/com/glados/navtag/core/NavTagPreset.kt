package com.glados.navtag.core

data class NavTagPreset(
    val name : String,
    val mode : NavTagMode
)

enum class NavTagMode {
    Off,
    SafetyLight,
    Destination,
    Commute,
    Running
}
