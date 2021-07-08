package com.pepej.squaria.utils

import com.pepej.squaria.serialization.ByteMap

class AngleValueVector3f(prefix: String, params: ByteMap, anim: Animation3D, def: Float = 0.0F) {
    val x: AngleFValue
    val y: AngleFValue
    val z: AngleFValue

    val isAnimationActive: Boolean
        get() = x.animStartTime + y.animStartTime + z.animStartTime == 0L

    init {
        if (params.containsKey(prefix)) {
            x = AngleFValue(params.getFloat(prefix), anim.editEasing, anim.editDuration)
            y = AngleFValue(x.orig, anim.editEasing, anim.editDuration)
            z = AngleFValue(x.orig, anim.editEasing, anim.editDuration)
        } else {
            x = AngleFValue(params.getFloat("$prefix.x", def), anim.editEasing, anim.editDuration)
            y = AngleFValue(params.getFloat("$prefix.y", def), anim.editEasing, anim.editDuration)
            z = AngleFValue(params.getFloat("$prefix.z", def), anim.editEasing, anim.editDuration)
        }
    }
}