package com.pepej.squaria.service

import com.pepej.papi.events.Events
import com.pepej.papi.terminable.TerminableConsumer
import com.pepej.papi.terminable.module.TerminableModule
import com.pepej.squaria.Squaria.Companion.instance
import com.pepej.squaria.elements.Button
import com.pepej.squaria.utils.ByteMap
import com.pepej.squaria.utils.IntColor
import org.bukkit.entity.Player
import org.bukkit.event.player.*

object PacketService : TerminableModule {


    override fun setup(consumer: TerminableConsumer) {
        Events.subscribe(AsyncPlayerChatEvent::class.java)
            .handler {
                val button = Button("test.btn", 60F, 10F, "Some test button", IntColor.M_GREY_100, IntColor.DEEP_SKY_BLUE)
                button.x = 80
                button.y = 30
                button.hoverable = true
                instance.add(button, it.player)
            }
            .bindWith(consumer)
        Events.subscribe(PlayerJoinEvent::class.java)
            .handler {
            }
            .bindWith(consumer)
        Events.merge(PlayerEvent::class.java, PlayerQuitEvent::class.java, PlayerKickEvent::class.java)
            .handler {
            }
            .bindWith(consumer)
    }


    fun openUrl(url: String, vararg players: Player) {
        for (player in players) {
            sendPacket(buildOpenUrl(url), player)
        }
    }

    fun openUrl(url: String, players: Collection<Player>) {
        for (player in players) {
            sendPacket(buildOpenUrl(url), player)
        }
    }

    private fun buildOpenUrl(url: String): ByteArray {
        val map = ByteMap()
        map["%"] = "url"
        map["url"] = url
        return map.toByteArray()
    }

    fun sendPacket(bytes: ByteArray, players: Collection<Player>) {
        players.forEach {
            sendPacket(bytes, it)
        }
    }

    fun sendPacket(bytes: ByteArray, vararg players: Player) {
        players.forEach {
            sendPacket(bytes, it)
        }
    }

    fun sendPacket(bytes: ByteArray, player: Player) {
        player.sendPluginMessage(instance, "Squaria", bytes)
    }
}