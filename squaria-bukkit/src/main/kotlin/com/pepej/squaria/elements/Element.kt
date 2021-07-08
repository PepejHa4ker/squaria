package com.pepej.squaria.elements

import com.pepej.squaria.serialization.ByteMap
import com.pepej.squaria.utils.*

abstract class Element<T : Element<T>> protected constructor(
    val id: String,
    open val type: String,
    var color: Int = -1,
    var duration: Long = -1L,
    open var pos: Position = Position.CENTER,
    open var attach: Attachment? = null,
    var visibility: Visibility? = null,
    open var anim: Animation2D? = null,
    open var scaleX: Float = 1.0F,
    open var scaleY: Float = 1.0F,
    open var x: Int = 0,
    open var y: Int = 0,
    var rotation: Float = 0.0F,
    var delay: Int = 0,
    var fadeStart: Int = 255,
    var fadeFinish: Int = 255,
    open var hoverable: Boolean = false
) {


    open var click: OnClick? = null
        set(value) {
            hoverable = true
            field = value
        }

    open fun setScale(scale: Float) {
        scaleX = scale
        scaleY = scale
    }

    fun setOffset(x: Int, y: Int) {
        this.x = x
        this.y = y

    }

    fun setFade(fade: Int) {
        fadeStart = fade
        fadeFinish = fadeStart
    }


    open fun write(map: ByteMap) {
        map["type"] = type
        if (id != null) {
            map["id"] = id
        }
        if (x != 0) {
            map["x"] = x
        }
        if (y != 0) {
            map["y"] = y
        }
        if (duration > 0L) {
            map["dur"] = duration
        }
        if (delay != 0) {
            map["delay"] = delay
        }
        if (color != -1) {
            map["color"] = color
        }
        if (rotation != 0.0f) {
            map["rot"] = rotation
        }
        if (hoverable && click == null) {
            map["hv"] = true
        }
        if (scaleX != 1.0f || scaleY != 1.0f) {
            if (scaleX == scaleY) {
                map["scale"] = scaleX
            } else {
                if (scaleX != 1.0f) {
                    map["scale.x"] = scaleX
                }
                if (scaleY != 1.0f) {
                    map["scale.y"] = scaleY
                }
            }
        }
        if (fadeStart != 255 || fadeFinish != 255) {
            if (fadeStart == fadeFinish) {
                map["fade"] = fadeStart
            } else {
                if (fadeStart != 255) {
                    map["fade.s"] = fadeStart
                }
                if (fadeFinish != 255) {
                    map["fade.f"] = fadeFinish
                }
            }
        }
        if (click != null) {
            val c = ByteMap()
            c["action"] = click?.action?.name
            c["data"] = click?.data
            map["click"] = c
        }
        if (visibility != null) {
            map["vis"] = visibility?.serialized
        }
        if (attach != null) {
            map["attach.to"] = attach?.attachTo
            map["attach.loc"] = attach?.attachLocation?.name
            if (attach?.attachLocation !== attach?.orientation) {
                map["attach.orient"] = attach?.orientation?.name
            }
            if (attach?.removeWhenParentRemove != true) {
                map["attach.rwpr"] = false
            }
        } else if (pos !== Position.CENTER) {
            map["pos"] = pos.name
        }
        if (anim != null) {
            if (anim?.start != null) {
                map["anim.s"] = anim?.start?.serialize()
            }
            if (anim?.finish != null) {
                map["anim.f"] = anim?.finish?.serialize()
            }
        }
    }
}


