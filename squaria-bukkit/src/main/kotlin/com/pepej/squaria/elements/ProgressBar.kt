package com.pepej.squaria.elements

import com.pepej.squaria.serialization.ByteMap

open class ProgressBar(
    id: String,
    width: Float,
    height: Float,
    var progress: Float,
    var barColor: Int = -1,
    var borderColor: Int = -1,
) : Rectangle(id, width, height) {

    override val type = "ProgressBar"


    override fun write(map: ByteMap) {
        super.write(map)
        map["barColor"] = barColor
        if (progress != -99.0f) {
            map["progress"] = progress
        }
        if (borderColor != -1) {
            map["border"] = borderColor
        }
    }
}