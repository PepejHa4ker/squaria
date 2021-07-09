package com.pepej.squaria

import com.pepej.squaria.service.SquariaMessenger
import com.pepej.papi.ap.Plugin
import com.pepej.papi.ap.PluginDependency
import com.pepej.papi.plugin.PapiJavaPlugin
import com.pepej.squaria.elements.Element
import com.pepej.squaria.serialization.ByteMap
import com.pepej.squaria.service.PacketService
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.messaging.PluginMessageListener

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
        Bukkit.getMessenger().registerIncomingPluginChannel(this, "Squaria", SquariaMessenger)
        bindModule(PacketService)
    }

}