package com.pepej.squaria.utils

import com.pepej.squaria.serialization.ByteMap

class OnClick(var action: Action, var data: ByteMap) {
    constructor(map: ByteMap) : this(Action.valueOf(map.getString("action")), map.getMap("data", ByteMap()))


    enum class Action {
        URL, CHAT, CALLBACK
    }

}
