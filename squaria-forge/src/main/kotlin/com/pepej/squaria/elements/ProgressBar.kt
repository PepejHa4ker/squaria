package com.pepej.squaria.elements

import com.pepej.squaria.gui.SquariaGui
import com.pepej.squaria.utils.ByteMap
import com.pepej.squaria.utils.FValue
import com.pepej.squaria.utils.drawRect
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import org.lwjgl.opengl.GL11

class ProgressBar(params: ByteMap) : Rectangle(params) {
    var progress = FValue(params.getFloat("progress", 0.5f), super.anim)
    var barColor = params.getInt("barColor")
    var borderColor = params.getInt("border", -1)
    override fun render(time: Long) {
        val barWidth: Float = width * progress.renderValue(time)
        GlStateManager.disableTexture2D()
        drawRect(0.0, 0.0, barWidth.toDouble(), height.toDouble(), super.color.render)
        drawRect(barWidth.toDouble(), 0.0, (width - barWidth).toDouble(), height.toDouble(),
            setAlphaToBaseColorIfGreater(
                barColor))
        if (borderColor != -1) {
            GL11.glLineWidth(0.5f * SquariaGui.scaledResolution.scaleFactor.toFloat())
            GlStateManager.disableTexture2D()
            setColor(setAlphaToBaseColorIfGreater(borderColor))
            val tessellator = Tessellator.getInstance()
            val builderBuffer = tessellator.buffer
            builderBuffer.begin(2, DefaultVertexFormats.POSITION)
            builderBuffer.pos(-0.25, -0.25, 0.0).endVertex()
            builderBuffer.pos(width.toDouble() + 0.25, -0.25, 0.0).endVertex()
            builderBuffer.pos(width.toDouble() + 0.25, height.toDouble() + 0.25, 0.0).endVertex()
            builderBuffer.pos(-0.25, height.toDouble() + 0.25, 0.0).endVertex()
            tessellator.draw()
            GlStateManager.enableTexture2D()
        }
    }

    companion object {
        private const val BORDER_WIDTH = 0.5
        private const val BORDER_WIDTH_D2 = 0.25
    }
}