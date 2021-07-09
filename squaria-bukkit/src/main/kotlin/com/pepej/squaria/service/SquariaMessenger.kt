package com.pepej.squaria.service

import com.pepej.papi.events.Events
import com.pepej.squaria.api.event.SquariaCallbackEvent
import com.pepej.squaria.serialization.ByteMap
import org.bukkit.entity.Player
import org.bukkit.plugin.messaging.PluginMessageListener

object SquariaMessenger : PluginMessageListener {

    override fun onPluginMessageReceived(channel: String, player: Player, message: ByteArray) {
        if (channel != "Squaria") return
        val data = ByteMap(message)
        when (data.getString("%")) {
            "callback" -> {
                Events.call(SquariaCallbackEvent(player, data))
            }
        }
    }

}