package com.glados.navtag.core

//import com.github.kittinunf.fuel.core.ResponseDeserializable
//import com.google.gson.Gson
//import com.google.gson.annotations.SerializedName

//data class SafetyLightPreset(
//    @SerializedName("ip") val ip: String,
//    @SerializedName("MAC") val mac: String,
//    @SerializedName("nom") val name: String
//)

/**
 * Type qui représente la liste noire
 *
 * @property users liste d'utilisateurs bloqués
 */
//data class SafetyLightPresets(@SerializedName("bloques") val users: Array<SafetyLightPreset>) {
//    class Deserializer : ResponseDeserializable<SafetyLightPresets> {
//        override fun deserialize(content: String): SafetyLightPresets? = Gson().fromJson(content, BlockedUsers::class.java)
//    }
//
//    override fun equals(other: Any?): Boolean {
//        if (this === other) return true
//        if (javaClass != other?.javaClass) return false
//
//        other as BlockedUsers
//
//        if (!users.contentEquals(other.users)) return false
//
//        return true
//    }
//
//    override fun hashCode(): Int {
//        return users.contentHashCode()
//    }
//}