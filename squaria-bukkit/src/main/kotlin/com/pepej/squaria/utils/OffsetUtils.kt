package com.pepej.squaria.utils

import com.pepej.squaria.elements.Element

object OffsetUtils {
    fun <T : Element<T>> doubleChest(elem: T, slot: Int): T {
        return if (slot in 0..53) {
            elem.pos = Position.CENTER
            elem.setOffset(slot % 9 * 18 - 72, slot / 9 * 18 - 85)
            elem
        } else {
            throw IllegalArgumentException("Slot must be between 0 and 53")
        }
    }

    fun <T : Element<T>> chest(elem: T, slot: Int): T {
        return if (slot in 0..25) {
            elem.pos = Position.CENTER
            elem.setOffset(slot % 9 * 18 - 72, slot / 9 * 18 - 58)
            elem
        } else {
            throw IllegalArgumentException("Slot must be between 0 and 26")
        }
    }

    fun <T : Element<T>> inv9(elem: T, slot: Int): T {
        return if (slot in 0..8) {
            elem.pos = Position.CENTER
            elem.setOffset(slot * 18 - 72, -40)
            elem
        } else {
            throw IllegalArgumentException("Slot must be between 0 and 8")
        }
    }

    fun <T : Element<T>> inv45(elem: T, slot: Int): T {
        return if (slot in 0..44) {
            elem.pos = Position.CENTER
            elem.setOffset(slot % 9 * 18 - 72, slot / 9 * 18 - 76)
            elem
        } else {
            throw IllegalArgumentException("Slot must be between 0 and 44")
        }
    }

    fun <T : Element<T>> hotbar(elem: T, slot: Int): T {
        return if (slot in 0..8) {
            elem.pos = Position.BOTTOM
            elem.setOffset(slot * 20 - 80, 3)
            elem
        } else {
            throw IllegalArgumentException("Slot must be between 0 and 8")
        }
    }
}