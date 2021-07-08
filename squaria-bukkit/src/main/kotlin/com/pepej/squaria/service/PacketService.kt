package com.pepej.squaria.service

import com.pepej.papi.events.Events
import com.pepej.papi.terminable.TerminableConsumer
import com.pepej.papi.terminable.module.TerminableModule
import com.pepej.squaria.Squaria
import com.pepej.squaria.Squaria.Companion.instance
import com.pepej.squaria.elements.ProgressBar
import com.pepej.squaria.elements.Rectangle
import com.pepej.squaria.elements.Text
import com.pepej.squaria.serialization.ByteMap
import com.pepej.squaria.utils.IntColor
import com.pepej.squaria.utils.OnClick
import com.pepej.squaria.utils.Position
import com.pepej.squaria.utils.Visibility
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.*

object PacketService : TerminableModule {


    override fun setup(consumer: TerminableConsumer) {
        Events.subscribe(EntityDamageEvent::class.java)
            .filter { it.entity is Player }
            .handler {
                val player = it.entity as Player
                val progressBar = ProgressBar("pg.test", 100f, 10f, ((player.health - it.damage) / player.maxHealth).toFloat())
                progressBar.color = IntColor.RED
                progressBar.duration = 1500
                progressBar.setOffset(80, 210)
                val text = Text("test.dmg", "Урон: " + (it.damage / 2.0).toString() + "❤")
                text.hoverable  = true
                text.color = IntColor.RED
                text.hoverBackground = IntColor.M_BLUE_GREY_100
                text.setOffset(100, 200)
                text.duration = 1500L
                instance.add(text, player)
                instance.add(progressBar, player)

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