package com.pepej.squaria.world

import com.pepej.squaria.Squaria
import com.pepej.squaria.elements.Element
import com.pepej.squaria.serialization.ByteMap
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.culling.Frustum
import net.minecraft.util.math.AxisAlignedBB

abstract class Element3D(params: ByteMap) : Element(params.getString("id")) {
    var distanceSquaredToPlayer = 0f
    var renderDistanceSquared = square(params.getInt("rndrDist", 64)).toFloat()
    var startTime = Squaria.time
    var finishTime = startTime + params.getLong("dur", 432000000L)
    var finishFade = 0
    var startFade = 0
    var boundingBox: AxisAlignedBB? = null
    var hover = false
    var hoverRangeSquared = 0
    var hoverable = params.getBoolean("hoverable", false)
    var frustumDisabled = !params.getBoolean("fe", true)
    protected val renderManager = Minecraft.getMinecraft().renderManager
    val isInViewRange: Boolean
        get() = distanceSquaredToPlayer < renderDistanceSquared

    fun isVisible(frustum: Frustum): Boolean {
        return frustumDisabled || frustum.isBoundingBoxInFrustum(boundingBox)
    }

    open fun invisibleTick(time: Long) {}
    open fun revalidateBB(time: Long) {}
    fun remove() {
        if (finishTime > Squaria.time + finishFade.toLong()) {
            finishTime = Squaria.time + finishFade.toLong()
        }
    }

    abstract fun calcDistanceSquaredToPlayer()
    open fun mouseClick(button: Int): Boolean {
        return false
    }

    open fun mouseWheel(dwheel: Int): Boolean {
        return false
    }

    companion object {
        protected fun square(num: Int): Int {
            return num * num
        }

        protected fun square(num: Float): Float {
            return num * num
        }

        @JvmStatic
        protected fun square(num: Double): Double {
            return num * num
        }
    }

    init {
        if (params.containsKey("fade")) {
            startFade = params.getInt("fade")
            finishFade = startFade
        } else {
            startFade = params.getInt("fade.s", 255)
            finishFade = params.getInt("fade.f", 255)
        }
        if (hoverable) {
            hoverRangeSquared = square(params.getInt("hr", 10))
        }
    }
}
