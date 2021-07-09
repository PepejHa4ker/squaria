package com.pepej.squaria.service

import com.pepej.papi.events.Events
import com.pepej.papi.terminable.TerminableConsumer
import com.pepej.papi.terminable.module.TerminableModule
import com.pepej.squaria.Squaria.Companion.instance
import com.pepej.squaria.elements.ProgressBar
import com.pepej.squaria.elements.Text
import com.pepej.squaria.serialization.ByteMap
import com.pepej.squaria.utils.IntColor
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.*

object PacketService : TerminableModule {


    override fun setup(consumer: TerminableConsumer) {
        Events.subscribe(EntityDamageEvent::class.java)
            .filter { it.entity is Player }
            .handler {
                val player = it.entity as Player
                val map = ByteMap()
                map["progress"] = ((player.health - it.damage) / player.maxHealth).toFloat()
               SquariaElementService.editElementByID("squaria.progressbar.test", map, player)
            }
            .bindWith(consumer)
        Events.subscribe(PlayerJoinEvent::class.java)
            .handler {
                val healthProgressBar = ProgressBar("squaria.progressbar.test", 200f, 5f, (it.player.health / it.player.maxHealth).toFloat())
                healthProgressBar.color = IntColor.LIGHT_RED
                healthProgressBar.setOffset(300, 400)
                SquariaElementService.addElement(healthProgressBar, it.player)
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

    fun sendPacket(bytes: ByteArray, players: Array<out Player>) {
        players.forEach {
            sendPacket(bytes, it)
        }
    }

    fun sendPacket(bytes: ByteArray, player: Player) {
        player.sendPluginMessage(instance, "Squaria", bytes)
    }
}