package com.pepej.squaria.gui

import com.pepej.squaria.Squaria
import com.pepej.squaria.Squaria.Companion.LOG
import com.pepej.squaria.elements.Element2D
import com.pepej.squaria.elements.container.Container
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import org.lwjgl.input.Mouse
import org.lwjgl.opengl.GL11
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList


class SquariaGui(private val mc: Minecraft) : Container {
    val elements: MutableMap<GuiRenderLayer, MutableList<GuiElementWrapper>>
    val byId: MutableMap<String, GuiElementWrapper>
    val persistentElements: MutableList<GuiElementWrapper>
    var visibleElementsCount = 0
    private var visibleElementsCountThisFrame = 0
    fun addElement(c: GuiElementWrapper) {
        removeElement(c.element.id)
        byId[c.element.id] = c
        elements[c.visibility.layer]?.add(c)
    }

    fun addPersistentElement(c: GuiElementWrapper) {
        byId[c.element.id] = c
        persistentElements.add(c)
    }

    override fun getElement(id: String?): Element2D? {
        return byId[id]?.element
    }

    fun removeElement(id: String) {
        for (elements in elements.values) {
            for (element in elements) {
                if (element.element.id == id) {
                    element.element.remove()
                }
            }
        }

    }

    override val width: Float = screenWidth.toFloat()
    override val height: Float = screenHeight.toFloat()

    fun removePersistentElement(id: String) {
        if (byId.remove(id) != null) {
            persistentElements.removeIf {
                it.element.id == id
            }
        }
    }

    fun removeGroup(group: String) {
        for (elements in elements.values) {
            for (element in elements) {
                if (element.element.id.startsWith(group)) {
                    element.element.remove()
                }
            }
        }
    }

    fun clear() {
        for (elements in elements.values) {
            for (element in elements) {
                element.element.remove()
            }
        }
    }

    fun clearNow() {
        for (elements in elements.values) {
            for (element in elements) {
                element.element.dispose()
                byId.remove(element.element.id)
            }
            elements.clear()
        }
    }

    fun render(layer: GuiRenderLayer) {
        if (!mc.gameSettings.hideGUI) {
            if (layer === GuiRenderLayer.HUD) {
                mouseX = Mouse.getX()
                mouseY = Mouse.getY()
                scaledResolution = ScaledResolution(mc)
                screenWidth = scaledResolution!!.scaledWidth
                screenHeight = scaledResolution!!.scaledHeight
                mouseX = mouseX * screenWidth / mc.displayWidth
                mouseY = screenHeight - mouseY * screenHeight / mc.displayHeight - 1
                visibleElementsCount = visibleElementsCountThisFrame
                visibleElementsCountThisFrame = 0
            } else if (layer === GuiRenderLayer.SCREEN && mc.world == null) {
                return
            }
            mc.world.profiler.startSection("SquariaGui")
            GlStateManager.disableRescaleNormal()
            GlStateManager.alphaFunc(516, 0.01f)
            GlStateManager.disableDepth()
            GlStateManager.disableLighting()
            val cullingEnabled = GL11.glIsEnabled(2884)
            if (cullingEnabled) {
                GlStateManager.disableCull()
            }
            Squaria.time = System.currentTimeMillis()
            val time: Long = Squaria.time
            val layerElements = elements[layer] as MutableList<GuiElementWrapper>
            for (element in layerElements) {
                renderElement(element, time)
                if (element.element.finishTime < time) {
                    byId.remove(element.element.id, element)
                    layerElements.remove(element)
                    element.element.dispose()
                }
            }
            if (persistentElements.isNotEmpty()) {
                for (persisElement in persistentElements) {
                    if (persisElement.visibility.layer === layer) {
                        renderElement(persisElement, time)
                        if (persisElement.element.finishTime < time) {
                            byId.remove(persisElement.element.id, persisElement)
                            persistentElements.remove(persisElement)
                            persisElement.element.dispose()
                        }
                    }
                }
            }
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
            GlStateManager.enableRescaleNormal()
            if (layer !== GuiRenderLayer.BEFORE_TOOLTIP) {
                GlStateManager.disableBlend()
                GlStateManager.enableDepth()
            }
            if (cullingEnabled) {
                GlStateManager.enableCull()
            }
            mc.world.profiler.endSection()
        }
    }

    private fun renderElement(e: GuiElementWrapper, time: Long) {
        e.checkHover(mc, time)
        if (e.isVisible(mc)) {
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
            ++visibleElementsCount
            try {
                e.render(time)
            } catch (var5: Exception) {
                LOG.error("GuiElement render error ($e)", var5)
            }
        }
    }

    companion object {
        var mouseX = 0
        var mouseY = 0
        lateinit var scaledResolution: ScaledResolution
        private var screenWidth = 0
        private var screenHeight = 0
    }

    init {
        elements = EnumMap(GuiRenderLayer::class.java)
        val var2 = GuiRenderLayer.values()
        val var3 = var2.size
        for (var4 in 0 until var3) {
            val layer = var2[var4]
            elements[layer] = CopyOnWriteArrayList()
        }
        persistentElements = CopyOnWriteArrayList()
        byId = HashMap()
    }
}