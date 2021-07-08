package com.pepej.squaria.elements

import com.pepej.squaria.Squaria
import com.pepej.squaria.Squaria.Companion.LOG
import com.pepej.squaria.serialization.ByteMap
import com.pepej.squaria.utils.*
import com.pepej.squaria.utils.Function
import net.minecraft.client.renderer.GlStateManager
import java.lang.reflect.Field

abstract class Element(val id: String) {

    protected open fun editFindReflectionFields(data: ByteMap, duration: Int, easing: Function) {
        for ((key, value) in data) {
            if (value == null) continue
            if (key.startsWith(".")) {
                editReflectAnimation(Reflect.findField(this.javaClass, key.substring(1)),
                    value, false, false, duration, easing)
            } else if (key.startsWith("#")) {
                if (key.startsWith("#%")) {
                    editReflectAnimation(Reflect.findField(this.javaClass, key.substring(2)),
                        value, true, true, duration, easing)
                } else {
                    editReflectAnimation(Reflect.findField(this.javaClass, key.substring(1)),
                        value, true, false, duration, easing)
                }
            }
        }
    }


    protected open fun editReflectAnimation(
        field: Field?,
        value: Any,
        animate: Boolean,
        smart: Boolean,
        duration: Int,
        easing: Function?,
    ) {
        if (field == null) {
            LOG.warn("Unable to find field")
        } else {
            val type = field.type
            try {
                if (type == ColorValue::class.java) {
                    val colorValue: ColorValue = field[this] as ColorValue
                    if (animate) {
                        if (easing != null) {
                            colorValue.startAnimation((value as Number).toInt(), duration, easing)
                        }
                    } else {
                        colorValue.set((value as Number).toInt())
                    }
                } else if (FValue::class.java.isAssignableFrom(type)) {
                    val `val`: FValue = field[this] as FValue
                    val newValue = (value as Number).toFloat()
                    if (animate) {
                        if (easing != null) {
                            `val`.startAnimation(newValue, duration, easing, smart)
                        }
                    } else {
                        `val`.set(newValue)
                    }
                } else if (type != ValueVector3f::class.java && type != AngleValueVector3f::class.java) {
                    field[this] = value
                } else {
                    val z: Any
                    val x: Any
                    val y: Any
                    if (type == ValueVector3f::class.java) {
                        val vec: ValueVector3f = field[this] as ValueVector3f
                        x = vec.x
                        y = vec.y
                        z = vec.z
                    } else {
                        val vec: AngleValueVector3f = field[this] as AngleValueVector3f
                        x = vec.x
                        y = vec.y
                        z = vec.z
                    }
                    val data = value as ByteMap
                    if (animate) {
                        val time: Long = Squaria.time
                        if (easing != null) {
                            if (data.containsKey("x")) {
                                (x as FValue).startAnimation(data.getFloat("x"), duration, easing, smart, time)
                            }
                            if (data.containsKey("y")) {
                                (y as FValue).startAnimation(data.getFloat("y"), duration, easing, smart, time)
                            }
                            if (data.containsKey("z")) {
                                (z as FValue).startAnimation(data.getFloat("z"), duration, easing, smart, time)
                            }
                        }
                    } else {
                        if (data.containsKey("x")) {
                            (x as FValue).set(data.getFloat("x"))
                        }
                        if (data.containsKey("y")) {
                            (y as FValue).set(data.getFloat("y"))
                        }
                        if (data.containsKey("z")) {
                            (z as FValue).set(data.getFloat("z"))
                        }
                    }
                }
            } catch (var14: Exception) {
                val v2 = value.javaClass.name + "(" + value + ")"
                LOG.warn("Unable to edit field '$type ${field.name} with give value $v2'")
            }
        }
    }

    abstract fun edit(data: ByteMap)

    protected open fun setColor(color: Int) {
        val a = (color shr 24 and 255).toFloat() / 255.0f
        val r = (color shr 16 and 255).toFloat() / 255.0f
        val g = (color shr 8 and 255).toFloat() / 255.0f
        val b = (color and 255).toFloat() / 255.0f
        GlStateManager.color(r, g, b, a)
    }



    protected open fun getAlpha(color: Int): Int {
        return color shr 24 and 255
    }

    protected open fun setAlpha(color: Int, alpha: Int): Int {
        return (color and 16777215) + (alpha shl 24)
    }

    open fun preRender(time: Long): Boolean {
        return true
    }

    abstract fun render(var1: Long)

    open fun dispose() {}

    open fun reload() {}
}