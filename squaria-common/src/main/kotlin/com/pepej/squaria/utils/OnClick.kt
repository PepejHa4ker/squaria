package com.pepej.squaria.utils

class OnClick(var action: Action, var data: ByteMap) {
    constructor(map: ByteMap) : this(Action.valueOf(map.getString("action")), map.getMap("data", ByteMap()))


    enum class Action {
        URL, CHAT, CALLBACK
    }

}
