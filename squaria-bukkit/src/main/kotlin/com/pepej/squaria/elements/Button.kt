package com.pepej.squaria.elements

import com.pepej.squaria.serialization.ByteMap

class Button(
    id: String,
    width: Float,
    height: Float,
    val text: String,
    val hoverColor: Int = -1,
    val textColor: Int = -1,

) : Rectangle(id, width, height) {

    override val type = "Button"

    override fun write(map: ByteMap) {
        super.write(map)
        map["width"] = width
        map["height"] = height
        map["text"] = text
        if (hoverColor != -1) {
            map["hoverColor"] = hoverColor
        }
        if (textColor != -1) {
            map["textColor"] = textColor
        }
        if (!hoverable) {
            map["hoverable"] = false
        } else {
            map.remove("hoverable")
        }
    }

}