package com.pepej.squaria.elements

import com.pepej.squaria.serialization.ByteMap


open class Rectangle(id: String, val width: Float, val height: Float) : Element<Rectangle>(id, "Rectangle") {

    constructor(id: String, size: Float) : this(id, size, size)

    override fun write(map: ByteMap) {
        super.write(map)
        if (width == height) {
            map["size"] = width
        } else {
            map["width"] = width
            map["height"] = height
        }
    }


}