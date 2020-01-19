package com.glados.navtag.core

import java.util.*

object CommuteList : Observable(){
    private var list: ArrayList<CommutePreset> = ArrayList()

    fun getValue(): ArrayList<CommutePreset> {
        return list
    }

    fun setValue(newList: ArrayList<CommutePreset>) {
        if (list != newList) {
            list.clear()
            for (element in newList) {
                list.add(element)
            }
            this.setChanged()
            this.notifyObservers(list)
        }
    }

    fun addElement(preset: CommutePreset){
        if (!list.any { it.name == preset.name }) {
            list.add(preset)
            this.setChanged()
            this.notifyObservers(list)
        }
    }

    fun removeElement(preset: CommutePreset){
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