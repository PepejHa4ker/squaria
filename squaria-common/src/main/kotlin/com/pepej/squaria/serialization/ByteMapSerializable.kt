package com.pepej.squaria.serialization

interface ByteMapSerializable<T> {

    fun serialize(): ByteMap

    fun deserialize(map: ByteMap): T

}