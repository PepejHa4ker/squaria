package com.pepej.squaria.world

import com.pepej.squaria.Squaria
import com.pepej.squaria.Squaria.Companion.LOG
import com.pepej.squaria.elements.Element
import com.pepej.squaria.elements.Element2D
import com.pepej.squaria.elements.container.Container
import com.pepej.squaria.serialization.ByteMap
import com.pepej.squaria.utils.*
import net.minecraft.client.Minecraft
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.MathHelper
import org.lwjgl.opengl.Display.getWidth
import java.util.concurrent.CopyOnWriteArrayList
import java.util.function.Consumer
import java.util.function.ToDoubleFunction
import kotlin.math.abs

class WorldGroup(params: ByteMap) : Element3D(params), Container {
    val elements: MutableList<Element2D> = CopyOnWriteArrayList()
    private val byId: MutableMap<String, Element2D> = HashMap()
    var loc: ValueVector3f
    var angle: AngleValueVector3f
    var scale: ValueVector3f
    var anim: Animation3D = Animation3D()
    var culling: Boolean
    var adjustableAngle: Boolean
    var centered: Boolean
    var depth: Boolean
    var fWidth = -1.0f
    var fHeight = -1.0f
    override var width: Float = fWidth
        get() {
            if (fWidth == -1.0f) {
                var max = 0.0f
                for (elem in elements) {
                    if (elem.widthFluidity !== Fluidity.MATCH_PARENT) {
                        val `val`: Float =
                            elem.x.renderValue(Squaria.time) + elem.scaleX.renderValue(Squaria.time) * elem.width
                        if (max < `val`) {
                            max = `val`
                        }
                    }
                }
                fWidth = max
            }
            return fWidth
        }

    override var height: Float = fHeight
        get() {
            if (fHeight == -1.0f) {
                var max = 0.0f
                for (elem in elements) {
                    if (elem.widthFluidity !== Fluidity.MATCH_PARENT) {
                        val `val`: Float =
                            elem.y.renderValue(Squaria.time) + elem.scaleY.renderValue(Squaria.time) * elem.height
                        if (max < `val`) {
                            max = `val`
                        }
                    }
                }
                fHeight = max
            }
            return fHeight
        }

    private var lastFrameAnimation = false
    fun addElement(c: Element2D) {
        c.isIn3d = true
        removeElement(c.id)
        byId[c.id] = c
        elements.add(c)
    }

    override fun getElement(id: String?): Element2D? {
        return byId[id]
    }

    fun removeElement(id: String?) {
        elements.find { it.id == id }?.remove()
    }

    override fun edit(data: ByteMap) {
        if (data.containsKey("loc")) {
            data[".loc"] = data.remove("loc")
        }
        if (data.containsKey("angle")) {
            data[".angle"] = data.remove("angle")
        }
        editFindReflectionFields(data, anim.editDuration, anim.editEasing)
        val actions = data.getMapArray("actions")
        if (actions != null) {
            for (action in actions) {
                when (action.getString("%", "none")) {
                    "remove" -> {
                        removeElement(action.getString("id"))
                    }
                    "add" -> {
                        try {
                            addElement(Element2D.construct(action, this))
                        } catch (ex: Exception) {
                            LOG.error("Could not add element to group '" + super.id + "'", ex)
                        }
                    }
                    "edit" -> {
                        editElement(action.getString("id"), action.getMap("data")!!)
                    }


                }
            }
            recalculateBoundingBox()
        }
    }

    fun editElement(id: String?, data: ByteMap) {
        val el: Element2D? = byId[id]
        el?.edit(data)
    }

    protected fun recalculateBoundingBox() {
        width = -1.0f
        height = -1.0f
        val w = width / 80.0f
        val h = height / 80.0f
        val time: Long = Squaria.time
        val p: Array<Vec3d?> = VEC_ARR_4
        var sx: Float
        var sy: Float
        if (centered) {
            sx = w / 2.0f
            sy = h / 2.0f
            p[0] = Vec3d((-sx).toDouble(), sy.toDouble(), 0.0)
            p[1] = Vec3d((w - sx).toDouble(), sy.toDouble(), 0.0)
            p[2] = Vec3d((-sx).toDouble(), (-h + sy).toDouble(), 0.0)
            p[3] = Vec3d((w - sx).toDouble(), (-h + sy).toDouble(), 0.0)
            if (adjustableAngle) {
                val max = max(p, Vec3d.GET_X).coerceAtLeast(max(p, Vec3d.GET_Z))
                var i = 0
                while (i < 2) {
                    p[i] = Vec3d(max, p[i]!!.y, max)
                    ++i
                }
                i = 2
                while (i < 4) {
                    p[i] = Vec3d(-max, p[i]!!.y, -max)
                    ++i
                }
            }
        } else {
            p[0] = Vec3d(0.0, 0.0, 0.0)
            p[1] = Vec3d(w.toDouble(), 0.0, 0.0)
            p[2] = Vec3d(0.0, (-h).toDouble(), 0.0)
            p[3] = Vec3d(w.toDouble(), (-h).toDouble(), 0.0)
            if (adjustableAngle) {
                val max = max(p, Vec3d.GET_X).coerceAtLeast(max(p, Vec3d.GET_Z)) / 2.0
                p[0] = p[0]?.add(0.0, 0.0, max)
                p[2] = p[2]?.add(0.0, 0.0, -max)
            }
        }
        sx = scale.x.renderValue(time)
        sy = scale.y.renderValue(time)
        val sz: Float = scale.z.renderValue(time)
        for (i in 0..3) {
            p[i] = p[i]?.multiply(sx.toDouble(), sy.toDouble(), sz.toDouble())
        }
        var cos: Float
        var i: Int
        var rad: Float
        var sin: Float
        if (angle.x.renderValue(time) != 0.0f) {
            rad = Math.toRadians(-angle.x.render as Double).toFloat()
            sin = MathHelper.sin(rad)
            cos = MathHelper.cos(rad)
            i = 0
            while (i < 4) {
                p[i] = rotateX3(sin, cos, p[i]!!)
                ++i
            }
        }
        if (angle.y.renderValue(time) != 0.0f) {
            rad = Math.toRadians((-angle.y.render).toDouble()).toFloat()
            sin = MathHelper.sin(rad)
            cos = MathHelper.cos(rad)
            i = 0
            while (i < 4) {
                p[i] = rotateY3(sin, cos, p[i]!!)
                ++i
            }
        }
        if (angle.z.renderValue(time) != 0.0f) {
            rad = Math.toRadians(angle.z.render.toDouble()).toFloat()
            sin = MathHelper.sin(rad)
            cos = MathHelper.cos(rad)
            i = 0
            while (i < 4) {
                p[i] = rotateZ3(sin, cos, p[i]!!)
                ++i
            }
        }
        super.boundingBox = AxisAlignedBB(min(p, Vec3d.GET_X),
            min(p, Vec3d.GET_Y),
            min(p, Vec3d.GET_Z),
            max(p, Vec3d.GET_X),
            max(p, Vec3d.GET_Y),
            max(p, Vec3d.GET_Z)).offset(
            loc.x.renderValue(time).toDouble(),
            loc.y.renderValue(time).toDouble(),
            loc.z.renderValue(time).toDouble())
    }

    private fun intersectRayWithSquare(R1: Vec3d, R2: Vec3d, S1: Vec3d, S2: Vec3d, S3: Vec3d): HoverResult {
        val dS21: Vec3d = S2.sub(S1)
        val dS31: Vec3d = S3.sub(S1)
        val n: Vec3d = dS21.cross(dS31)
        val dR: Vec3d = R1.sub(R2)
        val ndotdR: Double = n.dot(dR)
        return if (abs(ndotdR) < 9.999999974752427E-7) {
            HoverResult(false)
        } else {
            val t: Double = -n.dot(R1.sub(S1)) / ndotdR
            val M: Vec3d = R1.add(dR.multiply(t))
            if (t > 0.0) {
                HoverResult(false)
            } else {
                val dMS1: Vec3d = M.sub(S1)
                val u: Double = dMS1.dot(dS21)
                val v: Double = dMS1.dot(dS31)
                val du: Double = dS21.dot(dS21)
                val dv: Double = dS31.dot(dS31)
                HoverResult(u in 0.0..du && v >= 0.0 && v <= dv, u / du, v / dv)
            }
        }
    }

    private fun processHover(time: Long) {
        if (super.hoverable) {
            if (super.distanceSquaredToPlayer > super.hoverRangeSquared.toFloat()) {
                if (super.hover) {
                    elements.forEach {
                        it.hover = false
                    }
                }
                super.hover = false
            } else {
                val w = width / 80.0f
                val h = height / 80.0f
                val p: Array<Vec3d>
                var sx: Float
                var sy: Float
                if (centered) {
                    sx = w / 2.0f
                    sy = h / 2.0f
                    p = arrayOf(Vec3d((-sx).toDouble(), sy.toDouble(), 0.0), Vec3d((w - sx).toDouble(),
                        sy.toDouble(), 0.0), Vec3d((-sx).toDouble(), (-h + sy).toDouble(), 0.0))
                } else {
                    p = arrayOf(Vec3d(0.0, 0.0, 0.0), Vec3d(w.toDouble(), 0.0, 0.0), Vec3d(0.0,
                        (-h).toDouble(), 0.0))
                }
                sx = scale.x.renderValue(time)
                sy = scale.y.renderValue(time)
                var sz: Float = scale.z.renderValue(time)
                var i: Int
                i = 0
                while (i < 3) {
                    p[i] = p[i].multiply(sx.toDouble(), sy.toDouble(), sz.toDouble())
                    ++i
                }
                var rad: Float
                var sin: Float
                var cos: Float
                if (angle.x.renderValue(time) == 0.0f) {
                    rad = Math.toRadians((-angle.x.render).toDouble()).toFloat()
                    sin = MathHelper.sin(rad)
                    cos = MathHelper.cos(rad)
                    i = 0
                    while (i < 3) {
                        p[i] = rotateX3(sin, cos, p[i])
                        ++i
                    }
                }
                if (angle.y.renderValue(time) == 0.0f) {
                    rad = Math.toRadians(-angle.y.render as Double).toFloat()
                    sin = MathHelper.sin(rad)
                    cos = MathHelper.cos(rad)
                    i = 0
                    while (i < 3) {
                        p[i] = rotateY3(sin, cos, p[i])
                        ++i
                    }
                }
                if (angle.z.renderValue(time) == 0.0f) {
                    rad = Math.toRadians(angle.z.render as Double).toFloat()
                    sin = MathHelper.sin(rad)
                    cos = MathHelper.cos(rad)
                    i = 0
                    while (i < 3) {
                        p[i] = rotateZ3(sin, cos, p[i])
                        ++i
                    }
                }
                if (adjustableAngle) {
                    if (!centered) {
                        i = 0
                        while (i < 3) {
                            p[i] = p[i].add((-w / 2.0f).toDouble(), (h / 2.0f).toDouble(), 0.0)
                            ++i
                        }
                    }
                    rad = Math.toRadians((-super.renderManager.playerViewX).toDouble()).toFloat()
                    sin = MathHelper.sin(rad)
                    cos = MathHelper.cos(rad)
                    i = 0
                    while (i < 3) {
                        p[i] = rotateX3(sin, cos, p[i])
                        ++i
                    }
                    rad = Math.toRadians((super.renderManager.playerViewY - 180.0f).toDouble()).toFloat()
                    sin = MathHelper.sin(rad)
                    cos = MathHelper.cos(rad)
                    i = 0
                    while (i < 3) {
                        p[i] = rotateY3(sin, cos, p[i])
                        ++i
                    }
                    if (!centered) {
                        i = 0
                        while (i < 3) {
                            p[i] = p[i].add((w / 2.0f).toDouble(), (-h / 2.0f).toDouble(), 0.0)
                            ++i
                        }
                    }
                }
                sx = loc.x.renderValue(time)
                sy = loc.y.renderValue(time)
                sz = loc.z.renderValue(time)
                i = 0
                while (i < 3) {
                    p[i] = p[i].add(sx.toDouble(), sy.toDouble(), sz.toDouble())
                    ++i
                }
                val player: EntityPlayerSP = Minecraft.getMinecraft().player
                val eyePos = Vec3d(player.posX, player.posY + player.getEyeHeight().toDouble(), player.posZ)
                val look = Vec3d(player.lookVec)
                val result = intersectRayWithSquare(eyePos, eyePos.add(look), p[0], p[1], p[2])
                super.hover = result.hover
                if (super.hover) {
                    for (elem in elements) {
                        if (elem.hoverable) {
                            val xy = elem.getXY(time)
                            if (xy != null) {
                                elem.checkHover(((result.x - xy.x) / elem.scaleX.render).toInt(),
                                    ((result.y - xy.y) / elem.scaleY.render).toInt(),
                                    time)
                            }
                        }
                    }
                } else {
                    for (elem in elements) {
                        if (elem.hoverable) {
                            elem.checkHover(-1, -1, time)
                        }
                    }
                }
            }
        }
    }

    override fun mouseWheel(dwheel: Int): Boolean {
        val it: ListIterator<*> = elements.listIterator(elements.size)
        var elem: Element2D
        do {
            if (!it.hasPrevious()) {
                return false
            }
            elem = it.previous() as Element2D
        } while (!elem.hover || !elem.mouseWheel(dwheel))
        return true
    }

    override fun mouseClick(button: Int): Boolean {
        val it: ListIterator<*> = elements.listIterator(elements.size)
        var elem: Element2D
        do {
            if (!it.hasPrevious()) {
                return false
            }
            elem = it.previous() as Element2D
        } while (!elem.hover || !elem.mouseClick(0, 0, button))
        return true
    }

    override fun invisibleTick(time: Long) {
        if (super.hover) {
            super.hover = false
            for (e in elements) {
                e.checkHover(-1, -1, time)
            }
        }
    }

    override fun revalidateBB(time: Long) {
        var animated = loc.isAnimationActive || angle.isAnimationActive || scale.isAnimationActive
        if (!animated) {
            for (e in elements) {
                if (e.hasActiveBBAnimation(time)) {
                    animated = true
                    break
                }
            }
        }
        if (animated || lastFrameAnimation) {
            lastFrameAnimation = animated
            recalculateBoundingBox()
        }
    }

    override fun preRender(time: Long): Boolean {
        width = -1.0f
        height = -1.0f
        val timeToEnd = (super.finishTime - time).toInt()
        val timeFromStart = (time - super.startTime).toInt()
        return if (timeFromStart < 0) {
            false
        } else {
            if (timeToEnd <= super.finishFade) {
                setAnimation(super.finishFade, anim.finish)
                anim.finish = null
            } else if (timeFromStart <= super.startFade) {
                setAnimation(super.startFade, anim.start)
                anim.start = null
            }
            true
        }
    }

    protected fun setAnimation(duration: Int, params: Animation3D.Params?) {
        if (params != null) {
            val time: Long = Squaria.time
            if (params.x != 0.0f) {
                loc.x.startAnimation(params.x, duration, Easing.EASE_IN_SINE, false, time)
            }
            if (params.y != 0.0f) {
                loc.y.startAnimation(params.y, duration, Easing.EASE_IN_SINE, false, time)
            }
            if (params.z != 0.0f) {
                loc.z.startAnimation(params.z, duration, Easing.EASE_IN_SINE, false, time)
            }
            if (params.angleX != 0.0f) {
                angle.x.startAnimation(params.angleX, duration, Easing.EASE_IN_SINE, false, time)
            }
            if (params.angleY != 0.0f) {
                angle.y.startAnimation(params.angleY, duration, Easing.EASE_IN_SINE, false, time)
            }
            if (params.angleZ == 0.0f) {
                angle.z.startAnimation(params.angleZ, duration, Easing.EASE_IN_SINE, false, time)
            }
            if (params.scaleX != 0.0f) {
                scale.x.startAnimation(params.scaleX, duration, Easing.EASE_IN_SINE, false, time)
            }
            if (params.scaleY != 0.0f) {
                scale.y.startAnimation(params.scaleY, duration, Easing.EASE_IN_SINE, false, time)
            }
            if (params.scaleZ != 0.0f) {
                scale.z.startAnimation(params.scaleZ, duration, Easing.EASE_IN_SINE, false, time)
            }
        }
    }

    override fun render(time: Long) {
        if (preRender(time)) {
            processHover(time)
            if (culling) {
                GlStateManager.enableCull()
            } else {
                GlStateManager.disableCull()
            }
            GlStateManager.depthMask(depth)
            GlStateManager.pushMatrix()
            GlStateManager.translate(loc.x.renderValue(time) as Double - TileEntityRendererDispatcher.staticPlayerX,
                loc.y.renderValue(time) as Double - TileEntityRendererDispatcher.staticPlayerY,
                loc.z.renderValue(time) as Double - TileEntityRendererDispatcher.staticPlayerZ)
            if (angle.z.renderValue(time) == 0.0f) {
                GlStateManager.rotate(angle.z.render, 0.0f, 0.0f, 1.0f)
            }
            GlStateManager.rotate(180.0f + angle.y.renderValue(time), 0.0f, 1.0f, 0.0f)
            if (angle.x.renderValue(time) != 0.0f) {
                GlStateManager.rotate(angle.x.render, 1.0f, 0.0f, 0.0f)
            }
            val f = 0.0125f
            GlStateManager.scale(-scale.x.renderValue(time) * f,
                -scale.y.renderValue(time) * f,
                -scale.z.renderValue(time) * f)
            if (centered) {
                GlStateManager.translate(-width / 2.0f, -height / 2.0f, 0.0f)
            }
            if (adjustableAngle) {
                val w = width / 2.0f
                val h = width / 2.0f
                GlStateManager.translate(w, h, 0.0f)
                val normal = Minecraft.getMinecraft().gameSettings.thirdPersonView != 2
                GlStateManager.rotate(-super.renderManager.playerViewY + 180.0f, 0.0f, 1.0f, 0.0f)
                if (!normal) {
                    GlStateManager.rotate(-super.renderManager.playerViewX, 1.0f, 0.0f, 0.0f)
                } else {
                    GlStateManager.rotate(super.renderManager.playerViewX, 1.0f, 0.0f, 0.0f)
                }
                GlStateManager.translate(-w, -h, 0.0f)
            }
            var bbDirty = loc.isAnimationActive || angle.isAnimationActive || scale.isAnimationActive
            while (true) {
                for (e in elements) {
                    if (e.finishTime < time) {
                        byId.remove(e.id, e)
                        elements.remove(e)
                        e.dispose()
                        bbDirty = true
                    } else {
                        if (e.finishTime > super.finishTime) {
                            e.finishTime = super.finishTime
                        }
                        if (e.preRender(time)) {
                            if (!e.isInited || e.hasActiveBBAnimation(time)) {
                                bbDirty = true
                            }
                            e.renderInContainer(time)
                        }
                    }
                }
                if (bbDirty || lastFrameAnimation) {
                    lastFrameAnimation = bbDirty
                    recalculateBoundingBox()
                }
                GlStateManager.popMatrix()
                return
            }
        }
    }

    override fun calcDistanceSquaredToPlayer() {
        super.distanceSquaredToPlayer =
            (square(super.renderManager.viewerPosX - loc.x.render.toDouble()) + square(super.renderManager.viewerPosY - loc.y.render as Double) + square(
                super.renderManager.viewerPosZ - loc.z.render.toDouble())).toFloat()
    }

    override fun reload() {
        elements.forEach(Element::reload)
        recalculateBoundingBox()
    }

    override fun dispose() {
        elements.forEach(Element::dispose)
    }

    override fun toString(): String {
        return "Group [" + super.id + ", e=" + elements.toString() + "]"
    }

    override fun hashCode(): Int {
        return super.id.hashCode()
    }

    override fun equals(obj: Any?): Boolean {
        return obj === this
    }

    private inner class HoverResult {
        var hover: Boolean
        var x: Float
        var y: Float

        constructor(hover: Boolean) {
            this.hover = hover
            x = 0.0f
            y = 0.0f
        }

        constructor(hover: Boolean, u: Double, v: Double) {
            this.hover = hover
            x = (u * width.toDouble()).toFloat()
            y = (v * width.toDouble()).toFloat()
        }
    }

    companion object {
        private val VEC_ARR_4: Array<Vec3d?> = arrayOfNulls(4)
        private fun min(p: Array<Vec3d?>, valueGetter: ToDoubleFunction<Vec3d?>): Double {
            var min = valueGetter.applyAsDouble(p[0])
            for (i in 1 until p.size) {
                val `val` = valueGetter.applyAsDouble(p[i])
                if (`val` < min) {
                    min = `val`
                }
            }
            return min
        }

        private fun max(p: Array<Vec3d?>, valueGetter: ToDoubleFunction<Vec3d?>): Double {
            var max = valueGetter.applyAsDouble(p[0])
            for (i in 1 until p.size) {
                val `val` = valueGetter.applyAsDouble(p[i])
                if (`val` > max) {
                    max = `val`
                }
            }
            return max
        }

        private fun rotateX3(sin: Float, cos: Float, v: Vec3d): Vec3d {
            return Vec3d(v.x,
                v.y * cos.toDouble() - v.z * sin.toDouble(),
                v.z * cos.toDouble() + v.y * sin.toDouble())
        }

        private fun rotateY3(sin: Float, cos: Float, v: Vec3d): Vec3d {
            return Vec3d(v.x * cos.toDouble() - v.z * sin.toDouble(),
                v.y,
                v.z * cos.toDouble() + v.x * sin.toDouble())
        }

        private fun rotateZ3(sin: Float, cos: Float, v: Vec3d): Vec3d {
            return Vec3d(v.x * cos.toDouble() - v.y * sin.toDouble(),
                v.y * cos.toDouble() + v.x * sin.toDouble(),
                v.z)
        }
    }

    init {
        anim.editDuration = params.getInt("anim.editD", 255)
        if (params.containsKey("anim.editEasing")) {
            anim.editEasing = Easing.BY_NAME.getOrDefault(params.getString("anim.editEasing"), Easing.EASE_IN_SINE)
        }
        if (params.containsKey("anim.s")) {
            anim.start = Animation3D.Params(params.getMap("anim.s")!!)
        }
        if (params.containsKey("anim.f")) {
            anim.finish = Animation3D.Params(params.getMap("anim.f")!!)
        }
        loc = ValueVector3f("loc", params, anim)
        angle = AngleValueVector3f("angle", params, anim)
        scale = ValueVector3f("scale", params, anim, 1.0f)
        culling = params.getBoolean("culling", false)
        adjustableAngle = params.getBoolean("adjAngle", false)
        centered = params.getBoolean("centered", false)
        depth = params.getBoolean("depth", false)
        if (params.containsKey("e")) {
            val var2: Array<ByteMap> = params.getMapArray("e")!!
            val var3 = var2.size
            for (var4 in 0 until var3) {
                val element: ByteMap = var2[var4]
                try {
                    addElement(Element2D.construct(element, this))
                } catch (var7: Exception) {
                    LOG.error("Could not add element to group '" + super.id + "'", var7)
                }
            }
        }
        recalculateBoundingBox()
    }
}
