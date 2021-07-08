package com.pepej.squaria.elements

import com.pepej.squaria.Squaria
import com.pepej.squaria.utils.ByteMap
import com.pepej.squaria.utils.Easing
import com.pepej.squaria.utils.Fluidity
import com.pepej.squaria.utils.drawRect
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.FontRenderer

class Button(params: ByteMap) : Rectangle(params) {
    var textColor: Int
    var text: String
    var hoverColor: Int
    var font: FontRenderer
    var textWidth: Int
    private var hoverAnimationProgress: Float
    private var hoverAnimationFinish: Long
    private var lastHoverState: Boolean
    override val width: Float
        get() = if (super.widthFluidity === Fluidity.WRAP_CONTENT) {
            (textWidth + 5).toFloat()
        } else {
            if (super.widthFluidity === Fluidity.MATCH_PARENT){
                (super.parent?.width ?: 0.0F) / super.scaleX.render
            } else {
                super.fWidth.renderValue(Squaria.time)
            }
        }
    override var widthFluidity: Fluidity?
        get() = super.widthFluidity
        set(widthFluidity) {
            super.widthFluidity = widthFluidity
        }
    override val height: Float
        get() {
            return if (super.heightFluidity === Fluidity.WRAP_CONTENT) {
                (font.FONT_HEIGHT + 2).toFloat()
            } else {
                if (super.heightFluidity === Fluidity.MATCH_PARENT) {
                        (super.parent?.height ?: 0.0F) / super.scaleY.render
                } else {
                    super.fHeight.renderValue(Squaria.time)
                }
            }
        }
    override var heightFluidity: Fluidity?
        get() = super.heightFluidity
        set(heightFluidity) {
            super.heightFluidity = heightFluidity
        }

    override fun render(time: Long) {
        if (lastHoverState != super.hover) {
            lastHoverState = super.hover
            hoverAnimationFinish = if (super.hover) {
                (300.0f * (1.0f - hoverAnimationProgress)).toLong() + time
            } else {
                (300.0f * hoverAnimationProgress).toLong() + time
            }
        }
        val factor: Float
        if (hoverAnimationFinish > time) {
            hoverAnimationProgress = if (super.hover) {
                1.0f - 0.0f.coerceAtLeast((hoverAnimationFinish - time).toFloat() / 300.0f)
            } else {
                1.0f.coerceAtMost((hoverAnimationFinish - time).toFloat() / 300.0f)
            }
            factor = Easing.easeInOutQuad(hoverAnimationProgress)
        } else {
            hoverAnimationProgress = if (super.hover) 1.0f else 0.0f
            factor = hoverAnimationProgress
        }
        val w = width
        val h = height
        val delimiter = h * (0.9f - 0.8f * factor)
        drawRect(0.0, 0.0, w.toDouble(), delimiter.toDouble(), super.color.render)
        drawRect(0.0, delimiter.toDouble(), w.toDouble(), (h - delimiter).toDouble(),
            setAlphaToBaseColor(
                hoverColor))
        this.font.drawStringWithShadow(this.text,
            (w - this.textWidth.toFloat()) / 2.0f,
            (h - this.font.FONT_HEIGHT.toFloat()) / 2.0f,
            this.setAlphaToBaseColor(this.textColor))
    }

    companion object {
        private fun alterBrightness(color: Int, delta: Int): Int {
            var r = color shr 16 and 255
            var g = color shr 8 and 255
            var b = color and 255
            r = Math.min(255, r + delta)
            g = Math.min(255, g + delta)
            b = Math.min(255, b + delta)
            return (color and -16777216) + (r shl 16) + (g shl 8) + b
        }

    }

    init {
        this.font = Minecraft.getMinecraft().fontRenderer
        this.hoverAnimationProgress = 0.0f
        this.hoverAnimationFinish = 0L
        this.lastHoverState = false
        super.hoverable = params.getBoolean("hoverable", true)
        this.text = params.getString("text")
        this.hoverColor = params.getInt("hoverColor", alterBrightness(super.color.orig, -20))
        this.textColor = params.getInt("textColor", -1)
        this.textWidth = this.font.getStringWidth(this.text)
    }
}