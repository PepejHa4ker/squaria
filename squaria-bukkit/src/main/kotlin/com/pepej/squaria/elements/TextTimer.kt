package com.pepej.squaria.elements

import com.pepej.squaria.serialization.ByteMap


class TextTimer(id: String, vararg text: String) : Text(id, *text) {
    var timerDuration = -1L

    override fun write(map: ByteMap) {
        super.write(map)
        if (timerDuration != -1L) {
            map["millis"] = timerDuration
        }
    }

    override val type = "TextTimer"
}
