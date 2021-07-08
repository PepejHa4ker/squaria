package com.pepej.squaria.utils

import com.pepej.squaria.Squaria

class ColorValue(var render: Int, easing: Function, animDuration: Int) {
    var animDuration: Int
    var easing: Function
    var animStartTime: Long = 0
    var prev = 0
    var orig: Int

    constructor(value: Int, anim: Animation2D) : this(value, anim.editEasing, anim.editDuration)

    fun renderValue(time: Long): Int {
        if (animStartTime != 0L) {
            val diff = (time - animStartTime).toInt()
            if (diff < animDuration) {
                render = lerp(easing(diff.toFloat() / animDuration.toFloat()), orig, prev)
            } else {
                animStartTime = 0L
                render = orig
            }
        }
        return render
    }

    fun set(value: Int) {
        animStartTime = 0L
        render = value
        orig = render
        prev = orig
    }

    fun startAnimation(newValue: Int, duration: Int, easing: Function) {
        this.startAnimation(newValue, duration, easing, Squaria.time)
    }

    fun startAnimation(newValue: Int, duration: Int, easing: Function, time: Long) {
        animDuration = duration
        this.easing = easing
        orig = newValue
        prev = render
        animStartTime = time
    }

    companion object {
        fun lerp(progress: Float, finish: Int, start: Int): Int {
            var a0 = start shr 24 and 255
            var r0 = start shr 16 and 255
            var g0 = start shr 8 and 255
            var b0 = start and 255
            a0 = (a0.toFloat() + ((finish shr 24 and 255) - a0).toFloat() * progress).toInt()
            r0 = (r0.toFloat() + ((finish shr 16 and 255) - r0).toFloat() * progress).toInt()
            g0 = (g0.toFloat() + ((finish shr 8 and 255) - g0).toFloat() * progress).toInt()
            b0 = (b0.toFloat() + ((finish and 255) - b0).toFloat() * progress).toInt()
            if (a0 < 5) {
                a0 = 5
            }
            return a0 shl 24 or (r0 shl 16) or (g0 shl 8) or b0
        }
    }

    init {
        orig = render
        this.animDuration = animDuration
        this.easing = easing
    }
}
