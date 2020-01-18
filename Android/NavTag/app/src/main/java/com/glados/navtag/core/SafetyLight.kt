package com.glados.navtag.core

import java.util.*

object SafetyLight : Observable(){
    private var list: ArrayList<SafetyLightPreset> = ArrayList()

    fun getValue(): ArrayList<SafetyLightPreset> {
        return list
    }

    fun setValue(newList: ArrayList<SafetyLightPreset>) {
        if (list != newList) {
            list.clear()
            for (element in newList) {
                list.add(element)
            }
            this.setChanged()
            this.notifyObservers(list)
        }
    }

    fun addElement(preset: SafetyLightPreset){
        list.add(preset)
        this.setChanged()
        this.notifyObservers(list)
    }

    fun removeElement(preset: SafetyLightPreset){
        list.remove(preset)
        this.setChanged()
        this.notifyObservers(list)
    }

    fun reload(){
        this.setChanged()
        this.notifyObservers(list)
    }

    fun reset(){
        list.clear()
    }
}