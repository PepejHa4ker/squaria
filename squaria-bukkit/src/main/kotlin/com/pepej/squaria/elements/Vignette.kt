package com.pepej.squaria.elements

import com.pepej.squaria.utils.Animation2D
import com.pepej.squaria.utils.Attachment
import com.pepej.squaria.utils.OnClick
import com.pepej.squaria.utils.Position


class Vignette(id: String) : Element<Vignette>(id, "Vignette") {

    override var attach: Attachment? = null
        set(value) {
            field = value
            throw IllegalStateException("Vignette can not have attachment")
        }

    override var anim: Animation2D? = null
        set(value) {
            field = null
            throw IllegalStateException("Vignette can not have attachment")
        }

    override var click: OnClick? = null
        set(value) {
            field = null
            throw IllegalStateException("Vignette can not have attachment")
        }
    override var hoverable: Boolean = false
        set(value) {
            field = false
            throw IllegalStateException("Vignette can not have attachment")
        }

    override var x: Int = 0
        set(value) {
            field = 0
            throw IllegalStateException("Vignette can not have attachment")
        }
    override var y: Int = 0
        set(value) {
            field = 0
            throw IllegalStateException("Vignette can not have attachment")
        }
    override var pos: Position = Position.CENTER
        set(value) {
            field = Position.CENTER
            throw IllegalStateException("Vignette can not have attachment")
        }

    override var scaleX: Float = 0.0F
        set(value) {
            field = 0.0F
            throw IllegalStateException("Vignette can not have attachment")
        }
    override var scaleY: Float = 0.0F
        set(value) {
            field = 0.0F
            throw IllegalStateException("Vignette can not have attachment")
        }

    override fun setScale(scale: Float) {
        throw IllegalStateException("Vignette can not have scale")
    }


    override val type = "Vignette"
}
