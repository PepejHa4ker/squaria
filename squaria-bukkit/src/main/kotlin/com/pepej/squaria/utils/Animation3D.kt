package com.pepej.squaria.utils

import com.pepej.squaria.serialization.ByteMap

class Animation3D(
    var start: Params? = null,
    var finish: Params? = null
) {
    fun setStart(params: Params?): Animation3D {
        start = params
        return this
    }

    fun setFinish(params: Params?): Animation3D {
        finish = params
        return this
    }

    fun setBoth(params: Params?): Animation3D {
        start = params
        finish = start
        return this
    }

    class Params {
        private var x = 0.0f
        private var y = 0.0f
        private var z = 0.0f
        private var scaleX = 0.0f
        private var scaleY = 0.0f
        private var scaleZ = 0.0f
        private var angleX = 0.0f
        private var angleY = 0.0f
        private var angleZ = 0.0f
        fun setOffset(x: Float, y: Float, z: Float): Params {
            this.x = x
            this.y = y
            this.z = z
            return this
        }

        fun setScale(scale: Float): Params {
            this.setScale(scale, scale, scale)
            return this
        }

        fun setScale(x: Float, y: Float, z: Float): Params {
            scaleX = x
            scaleY = y
            scaleZ = z
            return this
        }

        fun setRotation(x: Float, y: Float, z: Float): Params {
            angleX = x
            angleY = y
            angleZ = z
            return this
        }

        fun serialize(): ByteMap {
            val map = ByteMap()
            if (x != 0.0f) {
                map["x"] = x
            }
            if (y != 0.0f) {
                map["y"] = y
            }
            if (z != 0.0f) {
                map["z"] = z
            }
            if (angleX != 0.0f) {
                map["angle.x"] = angleX
            }
            if (angleY != 0.0f) {
                map["angle.y"] = angleY
            }
            if (angleZ != 0.0f) {
                map["angle.z"] = angleZ
            }
            if (scaleX == scaleY && scaleX == scaleZ && scaleX != 0.0f) {
                map["scale"] = scaleX
            } else {
                if (scaleX != 0.0f) {
                    map["scale.x"] = scaleX
                }
                if (scaleY != 0.0f) {
                    map["scale.y"] = scaleY
                }
                if (scaleZ != 0.0f) {
                    map["scale.z"] = scaleZ
                }
            }
            return map
        }
    }
}