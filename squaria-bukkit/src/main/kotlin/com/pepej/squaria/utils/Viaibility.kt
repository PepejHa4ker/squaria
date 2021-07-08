package com.pepej.squaria.utils

import java.util.function.Consumer

class Visibility {
    private val list: MutableList<ByteMap?>
    private var layer: Layer
    private var baked: Array<ByteMap?>? = null
    fun always(show: Boolean): Visibility {
        return add(show) { it["type"] = "always" }
    }

    fun ingame(show: Boolean): Visibility {
        return add(show) { it["type"] = "ingame" }
    }

    fun gui(show: Boolean, clazz: String): Visibility {
        return add(show) {
            it["type"] = "gui"
            it["class"] = clazz
        }
    }

    fun inventory(show: Boolean, name: String): Visibility {
        return add(show) { map: ByteMap ->
            map["type"] = "inv"
            map["name"] = name
        }
    }

    fun chat(show: Boolean): Visibility {
        return add(show) { map: ByteMap -> map["type"] = "chat" }
    }

    fun f3(show: Boolean): Visibility {
        return add(show) { map: ByteMap -> map["type"] = "f3" }
    }

    fun fps(show: Boolean): Visibility {
        return add(show) { map: ByteMap -> map["type"] = "fps" }
    }

    fun tab(show: Boolean): Visibility {
        return add(show) { map: ByteMap -> map["type"] = "tab" }
    }

    fun bossbar(show: Boolean): Visibility {
        return add(show) { map: ByteMap -> map["type"] = "bossbar" }
    }

    fun layer(layer: Layer): Visibility {
        this.layer = layer
        baked = null
        return this
    }

    private fun add(show: Boolean, writer: Consumer<ByteMap>): Visibility {
        val map = ByteMap()
        map["show"] = show
        writer.accept(map)
        list.add(map)
        baked = null
        return this
    }

    val serialized: Array<ByteMap?>?
        get() {
            if (baked == null) {
                if (layer == Layer.HUD) {
                    baked = list.toTypedArray()
                } else {
                    val arr = arrayOfNulls<ByteMap>(list.size + 1)
                    (list as java.util.Collection<*>).toArray(arr)
                    arr[arr.size - 1] = layer.serialized
                    baked = arr
                }
            }
            return baked
        }

    companion object {
        val DEFAULT = Visibility().ingame(true).chat(true)
        val ALWAYS = Visibility().always(true)
        val ALWAYS_SCREEN: Visibility
        val ALWAYS_EXCEPT_TAB: Visibility
        val ALWAYS_SCREEN_EXCEPT_TAB: Visibility
        val INGAME_EXCEPT_TAB: Visibility

        init {
            ALWAYS_SCREEN = Visibility().always(true).layer(Layer.SCREEN)
            ALWAYS_EXCEPT_TAB = Visibility().always(true).tab(false)
            ALWAYS_SCREEN_EXCEPT_TAB = Visibility().always(true).tab(false).layer(Layer.SCREEN)
            INGAME_EXCEPT_TAB = Visibility().ingame(true).chat(true).tab(false)
        }
    }

    enum class Layer {
        HUD, SCREEN;

        val serialized = ByteMap()

        init {
            serialized["type"] = "layer"
            serialized["layer"] = name
        }
    }

    init {
        layer = Layer.HUD
        list = ArrayList()
    }
}