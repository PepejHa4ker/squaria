package com.pepej.squaria

import com.pepej.papi.ap.Plugin
import com.pepej.papi.ap.PluginDependency
import com.pepej.papi.plugin.PapiJavaPlugin
import com.pepej.squaria.elements.Element
import com.pepej.squaria.service.PacketService
import com.pepej.squaria.utils.ByteMap
import org.bukkit.Bukkit
import org.bukkit.entity.Player

@Plugin(name = "squaria", version = "1.0.0", depends = [PluginDependency("papi")])
class Squaria : PapiJavaPlugin() {

    companion object {
        lateinit var instance: Squaria
    }

    override fun onPluginLoad() {

        instance = this
    }

    override fun onPluginEnable() {
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "Squaria")
//        getService(BungeeCord::class.java)?.registerForwardCallbackRaw("Squaria") { data ->
//            val map = ByteMap(data)
//            val type = map.getString("%")
//
//            if (type == "callback") {
//                Events.call(SquariaCallbackEvent(map.getMap("data")))
//            }
//            false
//        }
        bindModule(PacketService)
//    }

    }

    fun add(element: Element<*>, vararg players: Player) {
        val map = ByteMap()
        map["%"] = "add"
        element.write(map)
        for (player in players) {
            PacketService.sendPacket(map.toByteArray(), player)
        }
    }
}