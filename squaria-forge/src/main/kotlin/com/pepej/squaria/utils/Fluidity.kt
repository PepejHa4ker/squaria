package com.pepej.squaria.utils

enum class Fluidity {
    FIXED, WRAP_CONTENT, MATCH_PARENT;

    companion object {
        fun byValue(size: Int): Fluidity {
            return if (size == -1) {
                WRAP_CONTENT
            } else {
                if (size == -2) MATCH_PARENT else FIXED
            }
        }
    }
}