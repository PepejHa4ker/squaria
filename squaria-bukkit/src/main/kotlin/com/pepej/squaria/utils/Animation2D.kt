package com.pepej.squaria.utils

class Animation2D(
    var start: Params? = null,
    var finish: Params? = null,
) {
    fun setStart(params: Params?): Animation2D {
        start = params
        return this
    }

    fun setFinish(params: Params?): Animation2D {
        finish = params
        return this
    }

    fun setBoth(params: Params?): Animation2D {
        start = params
        finish = start
        return this
    }

    class Params {
        private var x = 0
        private var y = 0
        private var scaleX = 0.0f
        private var scaleY = 0.0f
        private var rotation = 0.0f
        fun setX(x: Int): Params {
            this.x = x
            return this
        }

        fun setY(y: Int): Params {
            this.y = y
            return this
        }

        fun setScale(scale: Float): Params {
            scaleY = scale
            scaleX = scaleY
            return this
        }

        fun setScaleX(scale: Float): Params {
            scaleX = scale
            return this
        }

        fun setScaleY(scale: Float): Params {
            scaleY = scale
            return this
        }

        fun setRotation(angle: Float): Params {
            rotation = 360.0f
            return this
        }

        fun serialize(): ByteMap {
            val map = ByteMap()
            if (x != 0) {
                map["x"] = x
            }
            if (y != 0) {
                map["y"] = y
            }
            if (scaleX != 0.0f || scaleY != 0.0f) {
                if (scaleX == scaleY) {
                    map["scale"] = scaleX
                } else {
                    if (scaleX != 0.0f) {
                        map["scale.x"] = scaleX
                    }
                    if (scaleY != 0.0f) {
                        map["scale.y"] = scaleY
                    }
                }
            }
            if (rotation != 0.0f) {
                map["rot"] = rotation
            }
            return map
        }
    }
}