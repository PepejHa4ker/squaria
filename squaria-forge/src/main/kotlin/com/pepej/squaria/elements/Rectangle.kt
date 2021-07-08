package com.pepej.squaria.elements

import com.pepej.squaria.Squaria
import com.pepej.squaria.serialization.ByteMap
import com.pepej.squaria.utils.FValue
import com.pepej.squaria.utils.Fluidity
import com.pepej.squaria.utils.drawRect
import org.lwjgl.opengl.Display.getWidth

open class Rectangle : Element2D {
    override val width: Int
        get() {
            return if (widthFluidity === Fluidity.MATCH_PARENT) {
                ((super.parent?.width ?: 0.0F) / super.scaleX.renderValue(Squaria.time)).toInt()
            } else {
                fWidth.renderValue(Squaria.time).toInt()
            }
        }
    override var widthFluidity: Fluidity? = null
    override var heightFluidity: Fluidity? = null
    override val height: Int
        get() {
            return if (heightFluidity === Fluidity.MATCH_PARENT) {
                ((super.parent?.height ?: 0.0F) / super.scaleY.renderValue(Squaria.time)).toInt()
            } else {
                fHeight.renderValue(Squaria.time).toInt()
            }
        }

    var fWidth: FValue
    var fHeight: FValue

    constructor(params: ByteMap) : this(params, 1)
    constructor(params: ByteMap, defaultSize: Int) : super(params) {
        if (params.containsKey("size")) {
            fWidth = FValue(params.getFloat("size", defaultSize.toFloat()), super.anim)
            fHeight = FValue(fWidth.orig, super.anim)
        } else {
            fWidth = FValue(params.getFloat("width", defaultSize.toFloat()), super.anim)
            fHeight = FValue(params.getFloat("height", defaultSize.toFloat()), super.anim)
        }
        widthFluidity = Fluidity.byValue(fHeight.orig.toInt())
        heightFluidity = Fluidity.byValue(fHeight.orig.toInt())
    }

    constructor(id: String, width: Float, height: Float) : super(id) {
        this.fWidth = FValue(width, super.anim)
        this.fHeight = FValue(height, super.anim)
    }

    override fun hasActiveBBAnimation(time: Long): Boolean {
        return super.hasActiveBBAnimation(time) || fWidth.isActiveTick(time) || fHeight.isActiveTick(time)
    }


    override fun render(time: Long) {
        drawRect(0.0, 0.0, getWidth().toDouble(), height.toDouble(), super.color.render)
    }
}