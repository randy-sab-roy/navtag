package com.glados.navtag.core

import com.github.kittinunf.fuel.Fuel

object Communication {
    private const val IP = "192.168.137.27:8080/"

    fun applyMode(mode: NavTagMode){
        when(mode)
        {
            NavTagMode.Off -> setOff()
            NavTagMode.SafetyLight -> setSafetyLight()
        }
    }

    private fun setOff() {
        Fuel.post(IP + "000000000000000000000000000000")
    }

    fun setSafetyLight(){
        Fuel.post(IP + "171711717117171020200202002020")
    }
}