package com.pepej.squaria.elements

import com.pepej.squaria.utils.ByteMap

class Image(
    id: String,
    width: Float,
    height: Float,
    val image: String,
    var data: ByteArray?
) : Rectangle(id, width, height) {

    override val type = "Image"

    constructor(id: String, size: Float, image: String) : this(id, size, size, image, null)


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