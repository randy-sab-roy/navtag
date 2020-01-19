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
            NavTagMode.Destination -> setDestination()
            NavTagMode.Commute -> setCommute()
            NavTagMode.Running -> setRunning()
        }
    }

    private fun setOff() {
        Fuel.get(IP + "000000000000000000000000000000").responseString{ _, _, _ ->}
    }

    private fun setSafetyLight(){
        Fuel.get(IP + "171711717117171020200202002020").responseString{ _, _, _ ->}
    }

    private fun setDestination(){
        Fuel.get(IP + "000707777700070000000000000000").responseString{ _, _, _ ->}
    }

    private fun setCommute(){
        Fuel.get(IP + "055500555005550444444444444444").responseString{ _, _, _ ->}
    }

    private fun setRunning(){
        Fuel.get(IP + "111110000033330000000000000020").responseString{ _, _, _ ->}
    }
}