package com.glados.navtag.core

import com.google.gson.annotations.SerializedName
import java.util.*

data class Route(
    @SerializedName("id") var id: String,
    @SerializedName("stops") val intersections: IntArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Route

        if (id != other.id) return false
        if (!intersections.contentEquals(other.intersections)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + intersections.contentHashCode()
        return result
    }
}

data class IntersectionData (
    @SerializedName("data") val data: Array<Intersection>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as IntersectionData

        if (!data.contentEquals(other.data)) return false

        return true
    }

    override fun hashCode(): Int {
        return data.contentHashCode()
    }
}

data class Intersection(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val intersection: String
)

object CommuteList : Observable() {
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

    fun addElement(preset: CommutePreset) {
        if (!list.any { it.name == preset.name }) {
            list.add(preset)
            this.setChanged()
            this.notifyObservers(list)
        }
    }

    fun removeElement(preset: CommutePreset) {
        list.remove(preset)
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