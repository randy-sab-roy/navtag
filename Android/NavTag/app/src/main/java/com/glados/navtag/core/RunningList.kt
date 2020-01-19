package com.glados.navtag.core

import java.util.*

object RunningList : Observable(){
    private var list: ArrayList<RunningPreset> = ArrayList()

    fun getValue(): ArrayList<RunningPreset> {
        return list
    }

    fun setValue(newList: ArrayList<RunningPreset>) {
        if (list != newList) {
            list.clear()
            for (element in newList) {
                list.add(element)
            }
            this.setChanged()
            this.notifyObservers(list)
        }
    }

    fun addElement(preset: RunningPreset){
        if (!list.any { it.name == preset.name }) {
            list.add(preset)
            this.setChanged()
            this.notifyObservers(list)
        }
    }

    fun removeElement(preset: RunningPreset){
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