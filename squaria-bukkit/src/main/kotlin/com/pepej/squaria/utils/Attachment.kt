package com.pepej.squaria.utils

class Attachment(var attachTo: String, var attachLocation: Position) {
    var orientation = attachLocation
    var removeWhenParentRemove = true
}