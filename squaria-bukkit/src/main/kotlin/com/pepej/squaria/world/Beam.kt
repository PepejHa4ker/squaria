package com.pepej.squaria.world

import com.pepej.squaria.utils.ByteMap

class Beam(id: String, var color: Int) : WorldGroup(id) {
    override fun write(map: ByteMap) {
        super.write(map)
        map["type"] = "beam"
        map["color"] = color
        map.remove("e")
    }
}
