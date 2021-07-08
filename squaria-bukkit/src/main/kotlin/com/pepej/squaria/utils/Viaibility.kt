package com.pepej.squaria.utils

interface Visibility {
    fun write(map: ByteMap)

    object AlwaysNotTab : Visibility {
        override fun write(map: ByteMap) {
            map["type"] = "alwaysnottab"
        }
    }

    object Always : Visibility {
        override fun write(map: ByteMap) {
            map["type"] = "always"
        }
    }

    object IngameNotTab : Visibility {
        override fun write(map: ByteMap) {
            map["type"] = "nottab"
        }
    }

    object IngameNotChat : Visibility {
        override fun write(map: ByteMap) {
            map["type"] = "notchat"
        }
    }

    object IngameNotF3 : Visibility {
        override fun write(map: ByteMap) {
            map["type"] = "notf3"
        }
    }

    object Ingame : Visibility {
        override fun write(map: ByteMap) {
            map["type"] = "ingame"
        }
    }

    class Screen(vararg val screens: String) : Visibility {
        override fun write(map: ByteMap) {
            map["type"] = "screen"
            map["class"] = screens
        }
    }

    class Inventory(vararg val titles: String) : Visibility {
        override fun write(map: ByteMap) {
            map["type"] = "inventory"
            map["title"] = titles
        }
    }
}
