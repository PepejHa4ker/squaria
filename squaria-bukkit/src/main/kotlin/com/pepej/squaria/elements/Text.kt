package com.pepej.squaria.elements

import com.pepej.papi.text.Text.colorize
import com.pepej.squaria.utils.ByteMap

open class Text(id: String, vararg lines: String) : Element<Text>(id, "Text") {
    var text: Array<out String>
    var orientation = 2
    var width = -1
    var shadow = true
    var background = -1
    var hoverBackground = -1


    override fun write(map: ByteMap) {
        super.write(map)
        map["text"] = text
        if (width != -1) {
            map["width"] = width
        }
        if (orientation != 2) {
            map["or"] = orientation
        }
        if (!shadow) {
            map["shadow"] = false
        }
        if (background != -1) {
            map["background"] = background
        }
        if (hoverBackground != -1) {
            map["hoverBackground"] = hoverBackground
        }
    }

    override val type = "Text"

    companion object {
        const val LEFT = 1
        const val CENTER = 2
        const val RIGHT = 3
    }

    init {
        text = lines.map { colorize(it) }.toTypedArray()
    }
}