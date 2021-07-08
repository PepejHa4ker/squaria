package com.pepej.squaria.utils

class Animation3D {
    var start: Params? = null
    var finish: Params? = null
    var editDuration = 255
    var editEasing: Function = Easing.EASE_IN_SINE

    class Params(map: ByteMap) {
        var x: Float = map.getFloat("x", 0.0f)
        var y: Float = map.getFloat("y", 0.0f)
        var z: Float = map.getFloat("z", 0.0f)
        var scaleX = 0f
        var scaleY = 0f
        var scaleZ = 0f
        var angleX: Float
        var angleY: Float
        var angleZ: Float

        init {
            if (map.containsKey("scale")) {
                scaleZ = map.getFloat("scale")
                scaleY = scaleZ
                scaleX = scaleY
            } else {
                scaleX = map.getFloat("scale.x", 0.0f)
                scaleY = map.getFloat("scale.y", 0.0f)
                scaleZ = map.getFloat("scale.z", 0.0f)
            }
            angleX = map.getFloat("angle.x", 0.0f)
            angleY = map.getFloat("angle.y", 0.0f)
            angleZ = map.getFloat("angle.z", 0.0f)
        }
    }
}