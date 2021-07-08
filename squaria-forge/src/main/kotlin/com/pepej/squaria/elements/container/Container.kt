package com.pepej.squaria.elements.container

import com.pepej.squaria.elements.Element2D

interface Container {

    val width: Float
    val height: Float

    fun getElement(id: String?): Element2D?
}