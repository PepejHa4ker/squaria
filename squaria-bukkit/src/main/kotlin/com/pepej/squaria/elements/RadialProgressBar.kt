package com.pepej.squaria.elements

import com.pepej.squaria.serialization.ByteMap

open class RadialProgressBar(id: String, var size: Int, var progress: Float) : Element<RadialProgressBar>(id, "RadialProgressBar") {

    override fun write(map: ByteMap) {
        super.write(map)
        map["size"] = size
        if (progress != -99.0f) {
            map["progress"] = progress
        }
    }
}