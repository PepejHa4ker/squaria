package com.pepej.squaria.utils

import com.pepej.squaria.Squaria

open class FValue(var render: Float, easing: Function, animDuration: Int) {
    var animDuration: Int
    var easing: Function
    var animStartTime: Long = 0
    var valueDiff = 0f
    var orig: Float

    constructor(value: Float, anim: Animation2D) : this(value, anim.editEasing, anim.editDuration) {}

    fun isActiveTick(time: Long): Boolean {
        val active = animStartTime != 0L
        renderValue(time)
        return active
    }

    fun renderValue(time: Long): Float {
        if (animStartTime != 0L) {
            val diff = (time - animStartTime).toInt()
            if (diff < animDuration) {
                render = animate(easing(diff.toFloat() / animDuration.toFloat()), orig, valueDiff)
            } else {
                animStartTime = 0L
                render = orig
            }
        }
        return render
    }

    fun set(`val`: Float) {
        animStartTime = 0L
        valueDiff = 0.0f
        render = `val`
        orig = render
    }

    fun startAnimation(newValue: Float, duration: Int, easing: Function, smart: Boolean) {
        this.startAnimation(newValue, duration, easing, smart, Squaria.time)
    }

    open fun startAnimation(newValue: Float, duration: Int, easing: Function, smart: Boolean, time: Long) {
        renderValue(time)
        animDuration = duration
        this.easing = easing
        orig = newValue
        valueDiff = render - orig
        animStartTime = time
    }

    companion object {
        private fun animate(progress: Float, finish: Float, diff: Float): Float {
            return finish + diff * (1.0f - progress)
        }
    }

    init {
        orig = render
        this.animDuration = animDuration
        this.easing = easing
    }
}
