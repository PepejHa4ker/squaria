package com.pepej.squaria

import com.pepej.squaria.gui.SquariaGui
import com.pepej.squaria.serialization.ByteMap
import io.netty.buffer.Unpooled
import net.minecraft.client.Minecraft
import net.minecraft.network.PacketBuffer
import net.minecraft.network.play.client.CPacketCustomPayload
import net.minecraft.network.play.server.SPacketCustomPayload
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.network.NetworkRegistry
import org.apache.logging.log4j.LogManager

@Mod(modid = "squaria", name = "Squaria", version = "1.0.0")
class Squaria {

    val gui: SquariaGui = SquariaGui(mc)

    companion object {

        @Mod.Instance
        lateinit var instance: Squaria

        val LOG = LogManager.getLogger("Squaria")
        val mc = Minecraft.getMinecraft()
        var time = System.currentTimeMillis()

        fun sendCallbackPacket(data: ByteMap) {
            val map = ByteMap()
            map["%"] = "callback"
            map["data"] = data
            sendPacket(map)
        }

        fun sendPacket(map: ByteMap) {
            if (mc.world != null) {
                mc.player.connection.sendPacket(SPacketCustomPayload("Squaria", PacketBuffer(Unpooled.wrappedBuffer(map.toByteArray()))))
            }
        }

    }


    @Mod.EventHandler
    fun preinit(evt: FMLPreInitializationEvent) {
    }

    @Mod.EventHandler
    fun init(evt: FMLInitializationEvent) {
        val channel = NetworkRegistry.INSTANCE.newEventDrivenChannel("Squaria")
        MinecraftForge.EVENT_BUS.register(EventListener)
        channel.register(EventListener)
    }

    @Mod.EventHandler
    fun postinit(evt: FMLPostInitializationEvent) {
    }
}