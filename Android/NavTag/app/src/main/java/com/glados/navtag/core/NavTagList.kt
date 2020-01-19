package com.glados.navtag.core

import android.util.Log
import android.widget.Toast
import java.util.*

object NavTagList : Observable() {
    private var list: ArrayList<NavTagPreset> = ArrayList()

    fun getValue(): ArrayList<NavTagPreset> {
        return list
    }

    fun setValue(newList: ArrayList<NavTagPreset>) {
        if (list != newList) {
            list.clear()
            for (element in newList) {
                list.add(element)
            }
            this.setChanged()
            this.notifyObservers(list)
        }
    }

    fun addElement(preset: NavTagPreset) {
        if (!list.any { it.name == preset.name }) {
            list.add(preset)
            Communication.applyMode(list[0].mode, list[0].blinkRate)
            this.setChanged()
            this.notifyObservers(list)
        }
    }

    fun removeElement(preset: NavTagPreset) {
        list.remove(preset)
        if (list.isEmpty())
            Communication.applyMode(NavTagMode.Off, null)
        else
            Communication.applyMode(list[0].mode, list[0].blinkRate)
        this.setChanged()
        this.notifyObservers(list)
    }

    fun reload() {
        this.setChanged()
        this.notifyObservers(list)
    }

    fun reset() {
        list.clear()
    }
}