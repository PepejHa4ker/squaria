package com.pepej.squaria.gui

enum class GuiRenderLayer {
    HUD, BEFORE_TOOLTIP, SCREEN;

    companion object {
        val REVERSED_RENDER_ORDER = arrayOf(SCREEN, BEFORE_TOOLTIP, HUD)
    }
}