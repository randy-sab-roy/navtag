package com.glados.navtag.core

import java.util.*

object DestinationList : Observable(){
    private var list: ArrayList<DestinationPreset> = ArrayList()

    fun getValue(): ArrayList<DestinationPreset> {
        return list
    }

    fun setValue(newList: ArrayList<DestinationPreset>) {
        if (list != newList) {
            list.clear()
            for (element in newList) {
                list.add(element)
            }
            this.setChanged()
            this.notifyObservers(list)
        }
    }

    fun addElement(preset: DestinationPreset){
        if (!list.any { it.name == preset.name }) {
            list.add(preset)
            this.setChanged()
            this.notifyObservers(list)
        }
    }

    fun removeElement(preset: DestinationPreset){
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