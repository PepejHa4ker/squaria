package com.pepej.squaria.utils

import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats

fun drawRect(x: Double, y: Double, width: Double, height: Double, color: Int) {
    val a = (color shr 24 and 255).toFloat() / 255.0f
    val r = (color shr 16 and 255).toFloat() / 255.0f
    val g = (color shr 8 and 255).toFloat() / 255.0f
    val b = (color and 255).toFloat() / 255.0f
    GlStateManager.color(r, g, b, a)
    GlStateManager.disableTexture2D()
    val tessellator = Tessellator.getInstance()
    val bufferBuilder = tessellator.buffer
    bufferBuilder.begin(7, DefaultVertexFormats.POSITION)
    bufferBuilder.pos(x, y + height, 0.0).endVertex()
    bufferBuilder.pos(x + width, y + height, 0.0).endVertex()
    bufferBuilder.pos(x + width, y, 0.0).endVertex()
    bufferBuilder.pos(x, y, 0.0).endVertex()
    tessellator.draw()
    GlStateManager.enableTexture2D()
}