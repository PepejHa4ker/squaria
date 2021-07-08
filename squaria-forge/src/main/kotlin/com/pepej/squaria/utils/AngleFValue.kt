package com.pepej.squaria.utils


class AngleFValue : FValue {
    constructor(value: Float, anim: Animation2D) : super(value, anim)
    constructor(value: Float, easing: Function, animDuration: Int) : super(value, easing, animDuration)

    override fun startAnimation(newValue: Float, duration: Int, easing: Function, smart: Boolean, time: Long) {
        var newValue = newValue
        if (smart) {
            val newRotation = makePositiveRotation(newValue)
            val oldRotation = makePositiveRotation(super.orig)
            var diff = newRotation - oldRotation
            if (Math.abs(newRotation - oldRotation) > 180.0f) {
                if (newRotation > oldRotation) {
                    diff -= 360.0f
                } else {
                    diff += 360.0f
                }
            }
            newValue = super.orig + diff
        }
        super.startAnimation(newValue, duration, easing, smart, time)
    }

    companion object {
        private fun makePositiveRotation(rotation: Float): Float {
            var rotation = rotation
            rotation %= 360.0f
            return if (rotation < 0.0f) rotation + 360.0f else rotation
        }
    }
}