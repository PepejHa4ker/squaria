package com.pepej.squaria.api.event

import com.pepej.squaria.serialization.ByteMap
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class SquariaCallbackEvent(val data: ByteMap) : Event() {

    override fun getHandlers(): HandlerList {
        return handlerList
    }

    companion object {
        val handlerList = HandlerList()
    }

}
