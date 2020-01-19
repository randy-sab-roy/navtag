package com.glados.navtag.core

import android.util.Log
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.android.extension.responseJson
import com.github.kittinunf.fuel.httpGet
import java.lang.Thread.sleep

object Communication {
    private const val IP = "http://192.168.137.27:8080/"

    fun applyMode(mode: NavTagMode, blinkRate: BlinkRate?) {
        when (mode) {
            NavTagMode.Off -> setOff()
            NavTagMode.SafetyLight -> setSafetyLight(blinkRate!!)
            NavTagMode.Destination -> setDestination(mode)
            NavTagMode.Commute -> setCommute()
            NavTagMode.Running -> setRunning()
        }
    }

    private fun setOff() {
        Fuel.get(IP + "000000000000000000000000000000").responseString { _, _, _ -> }
    }

    private fun setSafetyLight(blinkRate: BlinkRate) {
        when (blinkRate) {
            BlinkRate.Still -> {
                Fuel.get(IP + "777777777777777000000000000000").responseString { _, _, _ -> }
            }
            BlinkRate.Slow -> {
                Fuel.get(IP + "777777777777777888888888888888").responseString { _, _, _ -> }
            }
            BlinkRate.Fast -> {
                Fuel.get(IP + "777777777777777111111111111111").responseString { _, _, _ -> }
            }
            BlinkRate.SOS -> {
                Fuel.get(IP + "777777777777777999999999999999").responseString { _, _, _ -> }
            }
            BlinkRate.Freedom -> {
                Fuel.get(IP + "331117773311111000000000000000").responseString { _, _, _ -> }
            }
            BlinkRate.Rainbow -> {
                Fuel.get(IP + "152344325115234000000000000000").responseString { _, _, _ -> }
            }
        }
    }

    private fun setDestination(mode: NavTagMode) {
        Thread {
            Fuel.get(IP + "000707777700070000000000000000").responseString { _, _, _ -> }
            sleep(7000)
            Fuel.get(IP + "000707777700070000000000700000").responseString { _, _, _ -> }
            sleep(7000)
            Fuel.get(IP + "000707777700070000000007000000").responseString { _, _, _ -> }
            sleep(7000)
            Fuel.get(IP + "000707777700070000000070000000").responseString { _, _, _ -> }
            sleep(7000)
            Fuel.get(IP + "000707777700070000000700000000").responseString { _, _, _ -> }
            sleep(7000)
            Fuel.get(IP + "000707777700070000007000000000").responseString { _, _, _ -> }
            sleep(7000)
            Fuel.get(IP + "070007777707000000000000000000").responseString { _, _, _ -> }
        }.start()
    }

    private fun setCommute() {
        Fuel.get(IP + "055500555005550444444444444444").responseString { _, _, _ -> }
    }

    private fun setRunning() {
        Fuel.get(IP + "111110000033330000000000000020").responseString { _, _, _ -> }
    }
}