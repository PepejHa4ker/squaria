package com.pepej.squaria.utils

data class Attachment(var attachTo: String, val attachLocation: Position) {
    var orientation: Position = attachLocation
    var removeWhenParentRemove = false

}

