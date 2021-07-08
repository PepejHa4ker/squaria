package com.pepej.squaria.utils

class Attachment(
    var attachTo: String,
    var attachLocation: Position
) {

    var orientation: Position
    var removeWhenParentRemove = true

    init {
        orientation = attachLocation
    }
}