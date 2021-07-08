package com.pepej.squaria

import com.pepej.squaria.Squaria.Companion.LOG
import com.pepej.squaria.Squaria.Companion.mc
import com.pepej.squaria.gui.GuiElementWrapper
import com.pepej.squaria.gui.GuiRenderLayer
import com.pepej.squaria.utils.ByteMap
import io.netty.buffer.Unpooled
import net.minecraft.network.PacketBuffer
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.PlayerEvent
import net.minecraftforge.fml.common.network.FMLNetworkEvent
import org.lwjgl.input.Mouse
import java.io.ByteArrayInputStream
import java.io.DataInputStream

object EventListener {
    @SubscribeEvent
    fun onPacketEvent(event: FMLNetworkEvent.ClientCustomPacketEvent) {
        val channel = event.packet.channel()
        if (channel != "Squaria") return
        val buf = event.packet.payload()


        val length = buf.readableBytes()
//        val amount = buf.readVarInt()
        LOG.debug("Received packet length $length")
//        for (i in 0 until amount) {
            val bytes = ByteArray(length)
            buf.readBytes(bytes)
            val map = ByteMap(bytes)
            LOG.debug("Received $map")

//        }

        handleAction(map)
    }

    @SubscribeEvent
    fun onDisconnect(event: PlayerEvent.PlayerLoggedOutEvent) {
        Squaria.instance.gui.clearNow()

    }

    @SubscribeEvent
    fun onRender(event: RenderGameOverlayEvent.Post) {
        Squaria.instance.gui.render(GuiRenderLayer.HUD)
    }


    private fun handleAction(map: ByteMap) {
        val action = map.getString("%")
        when (action) {

            "add" -> {
                Squaria.instance.gui.addElement(GuiElementWrapper(map, Squaria.instance.gui))
            }
        }

    }
}
