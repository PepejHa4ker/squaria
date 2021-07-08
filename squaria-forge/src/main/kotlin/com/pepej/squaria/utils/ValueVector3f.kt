package com.pepej.squaria.utils

import com.pepej.squaria.serialization.ByteMap


class ValueVector3f {
    val x: FValue
    val y: FValue
    val z: FValue

    constructor(prefix: String, params: ByteMap, anim: Animation3D) : this(prefix, params, anim, 0.0f)
    constructor(prefix: String, params: ByteMap, anim: Animation3D, def: Float) : this(prefix,
        params,
        anim.editDuration,
        anim.editEasing,
        def) {
    }

    constructor(prefix: String, params: ByteMap, editDuration: Int, editEasing: Function?, def: Float) {
        if (params.containsKey(prefix)) {
            x = FValue(params.getFloat(prefix), editEasing!!, editDuration)
            y = FValue(x.orig, editEasing, editDuration)
            z = FValue(x.orig, editEasing, editDuration)
        } else {
            x = FValue(params.getFloat("$prefix.x", def), editEasing!!, editDuration)
            y = FValue(params.getFloat("$prefix.y", def), editEasing, editDuration)
            z = FValue(params.getFloat("$prefix.z", def), editEasing, editDuration)
        }
    }

    constructor(x: Float, y: Float, z: Float, editDuration: Int, editEasing: Function?) {
        this.x = FValue(x, editEasing!!, editDuration)
        this.y = FValue(y, editEasing, editDuration)
        this.z = FValue(z, editEasing, editDuration)
    }

    fun renderValue(time: Long) {
        x.renderValue(time)
        y.renderValue(time)
        z.renderValue(time)
    }

    val isAnimationActive: Boolean
        get() = x.animStartTime + y.animStartTime + z.animStartTime != 0L
}