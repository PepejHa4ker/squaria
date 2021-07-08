package com.pepej.squaria.elements

import com.pepej.squaria.Squaria.Companion.LOG
import com.pepej.squaria.Squaria.Companion.mc
import com.pepej.squaria.utils.ByteMap
import com.pepej.squaria.utils.Fluidity
import com.pepej.squaria.utils.drawRect
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager


class Text(params: ByteMap) : Element2D(params) {
    var align: Align
    var iWidth = params.getInt("width", -1)
    private var tWidthFluidity = Fluidity.byValue(iWidth)
    override var widthFluidity = if (tWidthFluidity === Fluidity.MATCH_PARENT) Fluidity.MATCH_PARENT else Fluidity.FIXED

    var wordWrap: WordWrap
    override val width: Float
        get() {
            var width = iWidth.toFloat()
            if (widthFluidity === Fluidity.MATCH_PARENT) {
                width = (super.parent?.width ?: 0.0F) / super.scaleX.render
            } else if (background != -1) {
                width += 4.0f
            }
            return width
        }
    override val height
        get() = (font.FONT_HEIGHT * text.size).toFloat()
    var text = params.getStringArray("text")
    var font = mc.fontRenderer
    var hasShadow = params.getBoolean("shadow", true)
    var background = params.getInt("background", -1)
    var hoverBackground = 0
    override fun init() {
        super.init()
        update()
    }

    override fun reload() {
        super.reload()
        update()
    }

    fun update() {
        var w: Int
        if (widthFluidity === Fluidity.WRAP_CONTENT) {
            if (text.isEmpty()) {
                iWidth = 0
            } else {
                iWidth = font.getStringWidth(text[0])
                for (i in 1 until text.size) {
                    w = font.getStringWidth(text[i])
                    if (w > width) {
                        iWidth = w
                    }
                }
            }
        }
    }

    override fun render(time: Long) {
        GlStateManager.enableTexture2D()
        val bgColor = if (super.hover && hoverBackground != -1) hoverBackground else background
        var w = width
        if (bgColor != -1) {
            drawRect(0.0, 0.0, w.toDouble(), height.toDouble(), setAlphaToBaseColorIfGreater(bgColor))
            GlStateManager.translate(2.0f, 0.0f, 0.0f)
            w -= 4.0f
        }
        var i = 0
        when (align) {
            Align.CENTER -> {
                for (str in text) {
                    drawString(str, (w.toInt() - font.getStringWidth(str)) / 2, font.FONT_HEIGHT * i++,
                        super.color.render)
                }
                return
            }
            Align.LEFT -> {
                for (str in text) {
                    drawString(str, 0, font.FONT_HEIGHT * i++, super.color.render)
                }
                return
            }
            Align.RIGHT -> {
                for (str in text) {
                    drawString(str, w.toInt() - font.getStringWidth(str), font.FONT_HEIGHT * i++, super.color.render)
                }
            }
        }
    }

    private fun drawString(text: String?, x: Int, y: Int, color: Int) {
        if (hasShadow) {
            GlStateManager.pushMatrix()
            font.drawStringWithShadow(text, x.toFloat(), y.toFloat(), color)
            GlStateManager.popMatrix()

        } else {
            GlStateManager.pushMatrix()
            font.drawString(text, x, y, color)
            GlStateManager.popMatrix()
        }
    }

    override fun edit(data: ByteMap) {
        super.edit(data)
        if (data.containsKey(".width")) {
            widthFluidity = Fluidity.byValue(iWidth)
        }
        update()
    }


    enum class WordWrap {
        NO_WRAP, NORMAL, BREAK_WORD
    }

    enum class Align {
        LEFT, CENTER, RIGHT
    }

    init {
        font = Minecraft.getMinecraft().fontRenderer
        text = params.getStringArray("text")
        align = Align.valueOf(params.getString("align", "CENTER"))
        wordWrap = WordWrap.valueOf(params.getString("wordWrap", "NO_WRAP"))
        if (super.hoverable) {
            hoverBackground = params.getInt("hoverBackground", -1)
        }
        if (params.containsKey("or")) {
            align = Align.values()[params.getInt("or") - 1]
        }
    }
}