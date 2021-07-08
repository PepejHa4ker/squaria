package com.pepej.squaria.elements

class TextStopwatch(id: String, vararg lines: String) : Text(id, *lines) {
    override val type  = "TextStopwatch"
}
