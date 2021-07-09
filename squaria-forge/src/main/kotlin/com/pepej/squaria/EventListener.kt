package com.pepej.squaria

import com.pepej.squaria.Squaria.Companion.LOG
import com.pepej.squaria.Squaria.Companion.mc
import com.pepej.squaria.gui.GuiElementWrapper
import com.pepej.squaria.gui.GuiRenderLayer
import com.pepej.squaria.serialization.ByteMap
import com.pepej.squaria.world.Element3D
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.InputEvent
import net.minecraftforge.fml.common.gameevent.PlayerEvent
import net.minecraftforge.fml.common.network.FMLNetworkEvent
import org.lwjgl.input.Mouse


object EventListener {



    @SubscribeEvent
    fun onPacketEvent(event: FMLNetworkEvent.ClientCustomPacketEvent) {
        val channel = event.packet.channel()
        if (channel != "Squaria") return
        val buf = event.packet.payload()


        val length = buf.readableBytes()
        LOG.debug("Received packet length $length")
            val bytes = ByteArray(length)
            buf.readBytes(bytes)
            val map = ByteMap(bytes)
            LOG.debug("Received $map")

        handleAction(map)
    }

    @SubscribeEvent
    fun onDisconnect(event: PlayerEvent.PlayerLoggedOutEvent) {
        Squaria.instance.gui.clearNow()

    }

    @SubscribeEvent
    fun onRender(event: RenderGameOverlayEvent.Pre) {
        for (guiRenderLayer in GuiRenderLayer.REVERSED_RENDER_ORDER) {
            Squaria.instance.gui.render(guiRenderLayer)

        }
    }


    private fun handleAction(map: ByteMap) {
        when (map.getString("%")) {

            "add" -> {
                Squaria.instance.gui.addElement(GuiElementWrapper(map, Squaria.instance.gui))
            }

            "edit" -> {
                val element = Squaria.instance.gui.getElement(map.getString("id"))
                element?.edit(map.getMap("data")!!)
            }
            "remove:id" -> {
                Squaria.instance.gui.removeElement(map.getString("id"))
            }

            "remove:all" -> {
                Squaria.instance.gui.clear()
            }
        }

    }
}





























