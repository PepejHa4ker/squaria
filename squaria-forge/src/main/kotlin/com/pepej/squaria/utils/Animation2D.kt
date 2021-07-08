package com.pepej.squaria.utils

import com.pepej.squaria.serialization.ByteMap
import java.util.*

class Animation2D {
    var start: Params? = null
    var finish: Params? = null
    var editDuration = 255
    var editEasing: Function

    class Params {
        var x = 0f
        var y = 0f
        var scaleX = 0f
        var scaleY = 0f
        var rotate = 0f
        var easing: Function
        var duration = 255
        var color: Int
        var next: Params? = null
        var cyclic = false

        constructor(color: Int) {
            this.color = color
            easing = Easing.EASE_IN_SINE
        }

        constructor(params: Params) {
            color = params.color
            easing = params.easing
            x = params.x
            y = params.y
            scaleX = params.scaleX
            scaleY = params.scaleY
            rotate = params.rotate
            duration = params.duration
            next = params.next
            cyclic = params.cyclic
        }

        constructor(map: ByteMap, defaultColor: Int) {
            x = map.getInt("x", 0).toFloat()
            y = map.getInt("y", 0).toFloat()
            duration = map.getInt("d", 255)
            color = map.getInt("c", defaultColor)
            rotate = map.getFloat("rot", 0.0f)
            easing = Easing.BY_NAME.getOrDefault(map.getString("easing", "easeInSine"), Easing.EASE_IN_SINE)
            if (map.containsKey("scale")) {
                scaleY = map.getFloat("scale")
                scaleX = scaleY
            } else {
                scaleX = map.getFloat("scale.x", 0.0f)
                scaleY = map.getFloat("scale.y", 0.0f)
            }
            val nextMap = map.getMap("next")
            if (nextMap != null) {
                next = Params(nextMap, color)
            }
            if (map.getBoolean("cyclic", false)) {
                cyclic = true
                if (next != null) {
                    val antiDuplicate: MutableSet<Params?> = Collections.newSetFromMap(IdentityHashMap())
                    antiDuplicate.add(this)
                    antiDuplicate.add(next)
                    var last = next
                    while (last!!.next != null) {
                        last = last.next
                        if (antiDuplicate.contains(last)) {
                            cyclic = false
                            return
                        }
                        antiDuplicate.add(last)
                    }
                    last.next = this
                }
            }
        }
    }

    init {
        editEasing = Easing.EASE_IN_SINE
    }
}