package com.pepej.squaria.utils

import com.pepej.squaria.serialization.ByteMap
import kotlin.math.*


typealias Function = (Float) -> Float

object Easing {
    val BY_NAME: MutableMap<String, Function> = HashMap()
    val EASE_IN_SINE: Function = { easeInSine(it) }
    fun getOrDefault(params: ByteMap, key: String, def: Function): Function {
        val name = params.getString(key)
        return BY_NAME.getOrDefault(name, def)
    }

    fun linear(t: Float): Float {
        return t
    }

    fun easeInSine(t: Float): Float {
        return sin(t * 1.5707964f)
    }

    fun easeOutSine(t: Float): Float {
        return 1.0f + sin((t - 1.0f) * 1.5707964f)
    }

    fun easeInOutSine(t: Float): Float {
        return 0.5f * (1.0f + sin(3.1415927f * (t - 0.5f)))
    }

    fun easeInQuad(t: Float): Float {
        return t * t
    }

    fun easeOutQuad(t: Float): Float {
        return t * (2.0f - t)
    }

    fun easeInOutQuad(t: Float): Float {
        return if (t < 0.5F) 2.0f * t * t else -1.0f + (4.0f - 2.0f * t) * t
    }

    fun easeInCubic(t: Float): Float {
        return t * t * t
    }

    fun easeOutCubic(t: Float): Float {
        var t = t
        return --t * t * t + 1.0f
    }

    fun easeInOutCubic(t: Float): Float {
        return if (t < 0.5F) 4.0f * t * t * t else (t - 1.0f) * (2.0f * t - 2.0f) * (2.0f * t - 2.0f) + 1.0f
    }

    fun easeInQuart(t: Float): Float {
        return t * t * t * t
    }

    fun easeOutQuart(t: Float): Float {
        var t = t
        return 1.0f - --t * t * t * t
    }

    fun easeInOutQuart(t: Float): Float {
        var t = t
        return if (t < 0.5F) 8.0f * t * t * t * t else 1.0f - 8.0f * --t * t * t * t
    }

    fun easeInQuint(t: Float): Float {
        return t * t * t * t * t
    }

    fun easeOutQuint(t: Float): Float {
        var t = t
        return 1.0f + --t * t * t * t * t
    }

    fun easeInOutQuint(t: Float): Float {
        var t = t
        return if (t < 0.5F) 16.0f * t * t * t * t * t else 1.0f + 16.0f * --t * t * t * t * t
    }

    fun easeInExpo(t: Float): Float {
        return (2.0.pow((8.0f * t).toDouble()).toFloat() - 1.0f) / 255.0f
    }

    fun easeOutExpo(t: Float): Float {
        return 1.0f - 2.0.pow((-8.0f * t).toDouble()).toFloat()
    }

    fun easeInOutExpo(t: Float): Float {
        return if (t < 0.5f) (2.0.pow((16.0f * t).toDouble())
            .toFloat() - 1.0f) / 510.0f else 1.0f - 0.5f * 2.0.pow((-16.0f * (t - 0.5f)).toDouble()).toFloat()
    }

    fun easeInCirc(t: Float): Float {
        return 1.0f - sqrt(1.0f - t)
    }

    fun easeOutCirc(t: Float): Float {
        return sqrt(t)
    }

    fun easeInOutCirc(t: Float): Float {
        return if (t < 0.5f) (1.0f - sqrt(1.0f - 2.0f * t)) * 0.5f else (1.0f + sqrt(
            2.0f * t - 1.0f)) * 0.5f
    }

    fun easeInBack(t: Float): Float {
        return t * t * (2.70158f * t - 1.70158f)
    }

    fun easeOutBack(t: Float): Float {
        var t = t
        return 1.0f + --t * t * (2.70158f * t + 1.70158f)
    }

    fun easeInOutBack(t: Float): Float {
        var t = t
        return if (t < 0.5f) t * t * (7.0f * t - 2.5f) * 2.0f else 1.0f + --t * t * 2.0f * (7.0f * t + 2.5f)
    }

    fun easeInElastic(t: Float): Float {
        val t2 = t * t
        return t2 * t2 * sin(t * 3.1415927f * 4.5f)
    }

    fun easeOutElastic(t: Float): Float {
        val t2 = (t - 1.0f) * (t - 1.0f)
        return 1.0f - t2 * t2 * cos(t * 3.1415927f * 4.5f)
    }

    fun easeInOutElastic(t: Float): Float {
        val t2: Float
        return when {
            t < 0.45f -> {
                t2 = t * t
                8.0f * t2 * t2 * sin(t * 3.1415927f * 9.0f)
            }
            t < 0.55f -> {
                0.5f + 0.75f * sin(t * 3.1415927f * 4.0f)
            }
            else -> {
                t2 = (t - 1.0f) * (t - 1.0f)
                1.0f - 8.0f * t2 * t2 * sin(t * 3.1415927f * 9.0f)
            }
        }
    }

    fun easeInBounce(t: Float): Float {
        return 2.0.pow((6.0f * (t - 1.0f)).toDouble())
            .toFloat() * abs(sin(t * 3.1415927f * 3.5f))
    }

    fun easeOutBounce(t: Float): Float {
        return 1.0f - 2.0.pow((-6.0f * t).toDouble())
            .toFloat() * abs(cos(t * 3.1415927f * 3.5f))
    }

    fun easeInOutBounce(t: Float): Float {
        return if (t.toDouble() < 0.5) 8.0f * 2.0.pow((8.0f * (t - 1.0f)).toDouble()).toFloat() * abs(
            sin(t * 3.1415927f * 7.0f)) else 1.0f - 8.0f * 2.0.pow((-8.0f * t).toDouble()).toFloat() * abs(sin(t * 3.1415927f * 7.0f))
    }



    init {
        BY_NAME["linear"] = { linear(it) }
        BY_NAME["easeInSine"] = { easeInSine(it) }
        BY_NAME["easeOutSine"] = { easeOutSine(it) }
        BY_NAME["easeInOutSine"] = { easeInOutSine(it) }
        BY_NAME["easeInQuad"] = { easeInQuad(it) }
        BY_NAME["easeOutQuad"] = { easeOutQuad(it) }
        BY_NAME["easeInOutQuad"] = { easeInOutQuad(it) }
        BY_NAME["easeInCubic"] = { easeInCubic(it) }
        BY_NAME["easeOutCubic"] = { easeOutCubic(it) }
        BY_NAME["easeInOutCubic"] = { easeInOutCubic(it) }
        BY_NAME["easeInQuart"] = { easeInQuart(it) }
        BY_NAME["easeOutQuart"] = { easeOutQuart(it) }
        BY_NAME["easeInOutQuart"] = { easeInOutQuart(it) }
        BY_NAME["easeInQuint"] = { easeInQuint(it) }
        BY_NAME["easeOutQuint"] = { easeOutQuint(it) }
        BY_NAME["easeInOutQuint"] = { easeInOutQuint(it) }
        BY_NAME["easeInExpo"] = { easeInExpo(it) }
        BY_NAME["easeOutExpo"] = { easeOutExpo(it) }
        BY_NAME["easeInOutExpo"] = { easeInOutExpo(it) }
        BY_NAME["easeInCirc"] = { easeInCirc(it) }
        BY_NAME["easeOutCirc"] = { easeOutCirc(it) }
        BY_NAME["easeInOutCirc"] = { easeInOutCirc(it) }
        BY_NAME["easeInBack"] = { easeInBack(it) }
        BY_NAME["easeOutBack"] = { easeOutBack(it) }
        BY_NAME["easeInOutBack"] = { easeInOutBack(it) }
        BY_NAME["easeInElastic"] = { easeInElastic(it) }
        BY_NAME["easeOutElastic"] = { easeOutElastic(it) }
        BY_NAME["easeInOutElastic"] = { easeInOutElastic(it) }
        BY_NAME["easeInBounce"] = { easeInBounce(it) }
        BY_NAME["easeOutBounce"] = { easeOutBounce(it) }
        BY_NAME["easeInOutBounce"] = { easeInOutBounce(it) }
    }
}