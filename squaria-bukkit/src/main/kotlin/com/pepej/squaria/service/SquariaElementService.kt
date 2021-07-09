package com.pepej.squaria.service

import com.pepej.squaria.elements.Element
import com.pepej.squaria.serialization.ByteMap
import org.bukkit.entity.Player

object SquariaElementService {

    fun addElement(element: Element<*>, vararg players: Player) {
        val map = ByteMap()
        map["%"] = "add"
        element.write(map)
        for (player in players) {
            PacketService.sendPacket(map.toByteArray(), player)
        }
    }

    fun removeElementByID(id: String, vararg players: Player) {
        val map = ByteMap()
        map["%"] = "remove:id"
        map["id"] = id
        PacketService.sendPacket(map.toByteArray(), players)
    }

    fun removeElement(element: Element<*>, vararg players: Player) {
        removeElementByID(element.id, *players)
    }

    fun removeAllElements(vararg players: Player) {
        val map = ByteMap()
        map["%"] = "remove:all"
        PacketService.sendPacket(map.toByteArray(), players)
    }

    fun editElementByID(id: String, data: ByteMap, vararg players: Player) {
        val map = ByteMap()
        map["%"] = "edit"
        map["id"] = id
        map["data"] = data
        PacketService.sendPacket(map.toByteArray(), players)
    }

    fun editElement(element: Element<*>, data: ByteMap, vararg players: Player) {
        editElementByID(element.id, data, *players)
    }

}