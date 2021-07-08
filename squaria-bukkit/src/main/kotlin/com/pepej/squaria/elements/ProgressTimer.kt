package com.pepej.squaria.elements

import com.pepej.squaria.serialization.ByteMap

class ProgressTimer(
    id: String,
    width: Float,
    height: Float,
    var isReverse: Boolean = false
) : ProgressBar(id, width, height, -99.0f) {

    override val type = "ProgressTimer"

    override fun write(map: ByteMap) {
        super.write(map)
        if (isReverse) {
            map["reverse"] = true
        }
    }
}