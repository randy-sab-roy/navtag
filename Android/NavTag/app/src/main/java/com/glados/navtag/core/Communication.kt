package com.glados.navtag.core

import android.util.Log
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.android.extension.responseJson
import com.github.kittinunf.fuel.httpGet

object Communication {
    private const val IP = "http://192.168.137.27:8080/"

    fun applyMode(mode: NavTagMode){
        when(mode)
        {
            NavTagMode.Off -> setOff()
            NavTagMode.SafetyLight -> setSafetyLight()
        }
    }

    private fun setOff() {
        Fuel.get(IP + "000000000000000000000000000000").responseString{ _, response, result ->}
    }

    fun setSafetyLight(){
        Fuel.get(IP + "171711717117171020200202002020").responseString{ _, response, result ->}
    }
}