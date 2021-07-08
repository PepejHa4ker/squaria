package com.pepej.squaria.world

import com.pepej.squaria.elements.Element
import com.pepej.squaria.utils.Animation3D
import com.pepej.squaria.utils.ByteMap
import java.util.*

open class WorldGroup(var id: String) {
    var x = 0.0f
    var y = 0.0f
    var z = 0.0f
    var scaleX = 1.0f
    var scaleY = 1.0f
    var scaleZ = 1.0f
    var angleX = 0.0f
    var angleY = 0.0f
    var angleZ = 0.0f
    var duration = -1L
    var fadeStart = 255
    var fadeFinish = 255
    var culling = false
    var renderDistance = 64
    var adjustableAngle = false
    var centered = false
    var hoverable = false
    var hoverRange = 10
    var animation: Animation3D = Animation3D()
    var elements: MutableList<Element<*>> = LinkedList()

    fun clear() {
        elements.clear()
    }

    fun add(element: Element<*>) {
        elements.add(element)
    }

    fun add(vararg elements: Element<*>) {
        this.elements.addAll(elements.toList())
    }

    fun setLocation(x: Float, y: Float, z: Float) {
        this.x = x
        this.y = y
        this.z = z
    }

    fun setScale(scale: Float) {
        this.setScale(scale, scale, scale)
    }

    fun setScale(x: Float, y: Float, z: Float) {
        scaleX = x
        scaleY = y
        scaleZ = z
    }

    fun setRotation(x: Float, y: Float, z: Float) {
        angleX = x
        angleY = y
        angleZ = z
    }

    fun setFade(fade: Int) {
        fadeStart = fade
        fadeFinish = fadeStart
    }

    open fun write(map: ByteMap) {
        if (id != null) {
            map["id"] = id
        }
        if (duration > 0L) {
            map["dur"] = duration
        }
        if (culling) {
            map["culling"] = true
        }
        if (adjustableAngle) {
            map["adjAngle"] = true
        }
        if (renderDistance != 64) {
            map["rndrDist"] = renderDistance
        }
        if (centered) {
            map["centered"] = true
        }
        if (hoverable) {
            map["hv"] = true
            if (hoverRange != 10) {
                map["hr"] = hoverRange
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
        if (x != 0.0f) {
            map["loc.x"] = x
        }
        if (y != 0.0f) {
            map["loc.y"] = y
        }
        if (z != 0.0f) {
            map["loc.z"] = z
        }
        if (angleX != 0.0f) {
            map["angle.x"] = angleX
        }
        if (angleY != 0.0f) {
            map["angle.y"] = angleY
        }
        if (angleZ != 0.0f) {
            map["angle.z"] = angleZ
        }
        if (scaleX == scaleY && scaleX == scaleZ && scaleX != 1.0f) {
            map["scale"] = scaleX
        } else {
            if (scaleX != 1.0f) {
                map["scale.x"] = scaleX
            }
            if (scaleY != 1.0f) {
                map["scale.y"] = scaleY
            }
            if (scaleZ != 1.0f) {
                map["scale.z"] = scaleZ
            }
        }
        val e: Array<ByteMap?> = arrayOfNulls(elements.size)
        for (i in e.indices) {
            (elements[i] as Element<*>?)?.write(ByteMap().also { e[i] = it })
        }
        map["e"] = e
        if (animation.start != null) {
            map["anim.s"] = animation.start?.serialize()
        }
        if (animation.finish != null) {
            map["anim.f"] = animation.finish?.serialize()
        }
    }
}
