package com.glados.navtag.core

import android.util.Log
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.android.extension.responseJson
import com.github.kittinunf.fuel.httpGet
import java.lang.Thread.sleep

object Communication {
    private const val IP = "http://192.168.137.27:8080/"

    private lateinit var mode: NavTagMode

    fun applyMode(mode: NavTagMode, blinkRate: BlinkRate?) {
        this.mode = mode
        when (mode) {
            NavTagMode.Off -> setOff()
            NavTagMode.SafetyLight -> setSafetyLight(blinkRate!!)
            NavTagMode.Destination -> setDestination()
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

    private fun setDestination() {
        Thread {
            if (mode == NavTagMode.Destination) {
                Fuel.get(IP + "000707777700070000000000000000").responseString { _, _, _ -> }
                sleep(7000)
            }
            if (mode == NavTagMode.Destination) {
                Fuel.get(IP + "000707777700070000000000700000").responseString { _, _, _ -> }
                sleep(7000)
            }
            if (mode == NavTagMode.Destination) {
                Fuel.get(IP + "000707777700070000000007000000").responseString { _, _, _ -> }
                sleep(7000)
            }
            if (mode == NavTagMode.Destination) {
                Fuel.get(IP + "000707777700070000000070000000").responseString { _, _, _ -> }
                sleep(7000)
            }
            if (mode == NavTagMode.Destination) {
                Fuel.get(IP + "000707777700070000000700000000").responseString { _, _, _ -> }
                sleep(7000)
            }
            if (mode == NavTagMode.Destination) {
                Fuel.get(IP + "000707777700070000007000000000").responseString { _, _, _ -> }
                sleep(7000)
            }
            if (mode == NavTagMode.Destination)
                Fuel.get(IP + "070007777707000000000000000000").responseString { _, _, _ -> }
        }.start()
    }

    private fun setCommute() {
        Thread {
            if (mode == NavTagMode.Commute) {
                Fuel.get(IP + "022200222002220000000000000000").responseString { _, _, _ -> }
                sleep(10000)
            }
            if (mode == NavTagMode.Commute) {
                Fuel.get(IP + "055500555005550555555555555555").responseString { _, _, _ -> }
                sleep(10000)
            }
            if (mode == NavTagMode.Commute) {
                Fuel.get(IP + "011100111001110222222222222222").responseString { _, _, _ -> }
                sleep(10000)
            }
        }.start()
    }


    private fun setRunning() {
        Thread {
            if (mode == NavTagMode.Running) {
                Fuel.get(IP + "111110000070000000000000040000").responseString { _, _, _ -> }
                sleep(7000)
            }
            if (mode == NavTagMode.Running) {
                Fuel.get(IP + "111110000077000000000000004000").responseString { _, _, _ -> }
                sleep(7000)
            }
            if (mode == NavTagMode.Running) {
                Fuel.get(IP + "555550000077700000000000000400").responseString { _, _, _ -> }
                sleep(7000)
            }
            if (mode == NavTagMode.Running) {
                Fuel.get(IP + "222220000077770000000000000040").responseString { _, _, _ -> }
                sleep(7000)
            }
            if (mode == NavTagMode.Running) {
                Fuel.get(IP + "555550000077777000000000000004").responseString { _, _, _ -> }
                sleep(7000)
            }
            if (mode == NavTagMode.Running) {
                Fuel.get(IP + "666660000066666000000000000000").responseString { _, _, _ -> }
                sleep(7000)
            }
        }.start()
    }
}