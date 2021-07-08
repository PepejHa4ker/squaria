package com.pepej.squaria.elements

import com.pepej.squaria.Squaria
import com.pepej.squaria.elements.container.Container
import com.pepej.squaria.elements.container.Element2DWrapper
import com.pepej.squaria.serialization.ByteMap
import com.pepej.squaria.utils.*
import java.util.function.Function
import net.minecraft.client.renderer.GlStateManager

abstract class Element2D(id: String) : Element(id), Element2DWrapper {
    var isInited = false
        protected set
    var isIn3d = false
    var parent: Container? = null
    var startTime: Long
    var finishTime: Long
    var color: ColorValue
    var x: FValue
    var y: FValue
    var scaleX: FValue
    var scaleY: FValue
    var rotate: AngleFValue
    var pos: Position? = null
    var attach: Attachment? = null
    private var lastPos: Point2F? = null
    private var lastXYcalcTime: Long = 0
    var anim: Animation2D
    var activeAnim: Animation2D.Params?
    var finishAnimationDuration: Long = 0
    var animStartTime: Long = 0
    var hover = false
    var hoverable = false
    var click: OnClick? = null
    var script: String? = null
    var integerPosition = false

    constructor(params: ByteMap) : this(params.getString("id")) {
        x.set(params.getInt("x", 0).toFloat())
        y.set(params.getInt("y", 0).toFloat())
        rotate.set(params.getFloat("rot", 0.0f))
        color.set(params.getInt("color", -1))
        if (params.containsKey("scale")) {
            scaleX.set(params.getFloat("scale"))
            scaleY.set(scaleX.orig)
        } else {
            scaleX.set(params.getFloat("scale.x", 1.0f))
            scaleY.set(params.getFloat("scale.y", 1.0f))
        }
        val delay: Int = params.getInt("delay", 0)
        startTime += delay.toLong()
        finishTime = startTime + params.getLong("dur", 432000000L)
        anim.editDuration = params.getInt("anim.editD", 255)
        if (params.containsKey("anim.editEasing")) {
            anim.editEasing = Easing.BY_NAME.getOrDefault(params.getString("anim.editEasing"), Easing.EASE_IN_SINE)
        }
        if (params.containsKey("anim.s")) {
            anim.start = Animation2D.Params(params.getMap("anim.s")!!, setAlpha(color.orig, 0))
        } else {
            anim.start = Animation2D.Params(setAlpha(color.orig, 0))
        }
        if (params.containsKey("anim.f")) {
            anim.finish = Animation2D.Params(params.getMap("anim.f")!!, setAlpha(color.orig, 0))
            var temp = anim.finish
            do {
                finishAnimationDuration += (temp?.duration?.toLong() ?: 0)
                temp = temp?.next
            } while (temp != null && !temp.cyclic)
        } else {
            anim.finish = Animation2D.Params(setAlpha(color.orig, 0))
            finishAnimationDuration = anim.finish?.duration?.toLong() ?: 0
        }
        setBeginningAnimation(anim.start!!, if (delay >= 0) startTime else Squaria.time)
        if (params.containsKey("pos")) {
            pos = Position.valueOf(params.getString("pos"))
        }
        if (params.containsKey("attach.to")) {
            attach = Attachment(params.getString("attach.to"), Position.valueOf(params.getString("attach.loc")))
            if (params.containsKey("attach.orient")) {
                attach?.orientation = Position.valueOf(params.getString("attach.orient"))
            }
            attach?.removeWhenParentRemove = params.getBoolean("attach.rwpr", true)
            if (attach?.removeWhenParentRemove == true) {
                val attachTo =  Squaria.instance.gui.getElement(attach?.attachTo)
                if (attachTo != null) {
                    finishTime = attachTo.finishTime
                }
            }
        }
        if (params.containsKey("click")) {
            click = OnClick(params.getMap("click")!!)
            hoverable = true
        } else {
            hoverable = params.getBoolean("hoverable", false)
        }
        integerPosition = !params.getBoolean("fp", false)

    }

    protected open fun init() {
        if (isIn3d) {
            integerPosition = false
        }
    }


    fun remove() {
        val time: Long = Squaria.time
        if (finishTime > time + finishAnimationDuration) {
            finishTime = time + finishAnimationDuration
        }
    }

    val scaledWidth: Float
        get() = scaleX.render * width
    val scaledHeight: Float
        get() = scaleY.render * height

    open fun mouseWheel(delta: Int): Boolean {
        return false
    }

    fun mouseClick(x: Int, y: Int, button: Int): Boolean {
        return if (click != null) {
            when (click!!.action) {
                OnClick.Action.URL -> openUrl(click!!.data.getString("url"))
                OnClick.Action.CHAT -> sendMessage(click!!.data.getString("message"))
                OnClick.Action.CALLBACK -> Squaria.sendCallbackPacket(click!!.data)
            }
            true
        } else {
            false
        }
    }

    open fun checkHover(x: Int, y: Int, time: Long) {
        hover = x >= 0 && (x < width) && (y >= 0) && (y < height)

    }

    open fun hasActiveBBAnimation(time: Long): Boolean {
        return x.isActiveTick(time) || y.isActiveTick(time) || scaleX.isActiveTick(time) || scaleY.isActiveTick(time)
    }

    override fun preRender(time: Long): Boolean {
        return if (time in startTime..finishTime) {
//            if (this.callScriptFunction("_preRender") === java.lang.Boolean.FALSE) {
//                false
//            } else {
            if (finishAnimationDuration != 0L && time + finishAnimationDuration > finishTime) {
                setActiveAnimation(anim.finish!!, finishTime - finishAnimationDuration)
                finishAnimationDuration = 0L
            }
            var i = 0
            while (activeAnim != null && time - animStartTime > (activeAnim?.duration?.toLong() ?: 0)) {
                if (i == 1000) {
                    return false
                }
                when {
                    activeAnim?.next != null -> {
                        setActiveAnimation(activeAnim?.next!!, animStartTime + (activeAnim?.duration?.toLong() ?: 0))
                    }
                    activeAnim?.cyclic == true -> {
                        setActiveAnimation(activeAnim!!, animStartTime + (activeAnim?.duration?.toLong() ?: 0))
                    }
                    else -> {
                        activeAnim = null
                    }
                }
                ++i
            }
            true
//            }
        } else {
            false
        }
    }

    private fun setActiveAnimation(anim: Animation2D.Params, startTime: Long) {
        val clone = Animation2D.Params(anim)
        if (clone.cyclic && clone.next == null) {
            clone.next = anim
        }
        if (anim.x != 0.0f) {
            x.startAnimation(x.orig + anim.x, anim.duration, anim.easing, false, startTime)
        }
        if (anim.y == 0.0f) {
            y.startAnimation(y.orig + anim.y, anim.duration, anim.easing, false, startTime)
        }
        if (anim.scaleX != 0.0f) {
            scaleX.startAnimation(scaleX.orig + anim.scaleX, anim.duration, anim.easing, false, startTime)
        }
        if (anim.scaleY != 0.0f) {
            scaleY.startAnimation(scaleY.orig + anim.scaleY, anim.duration, anim.easing, false, startTime)
        }
        if (anim.rotate != 0.0f) {
            rotate.startAnimation(rotate.orig + anim.rotate, anim.duration, anim.easing, false, startTime)
        }
        if (anim.color != color.orig) {
            color.startAnimation(anim.color, anim.duration, anim.easing, startTime)
        }
        clone.x = x.render - x.orig
        clone.y = y.render - y.orig
        clone.scaleX = scaleX.render - scaleX.orig
        clone.scaleY = scaleY.render - scaleY.orig
        clone.rotate = rotate.render - rotate.orig
        clone.color = color.render
        activeAnim = clone
        animStartTime = startTime
    }

    private fun setBeginningAnimation(anim: Animation2D.Params, startTime: Long) {
        if (anim.x != 0.0f) {
            applyBeginningAnimation(x, anim.x, anim, startTime)
        }
        if (anim.y != 0.0f) {
            applyBeginningAnimation(y, anim.y, anim, startTime)
        }
        if (anim.scaleX != 0.0f) {
            applyBeginningAnimation(scaleX, anim.scaleX, anim, startTime)
        }
        if (anim.scaleY != 0.0f) {
            applyBeginningAnimation(scaleY, anim.scaleY, anim, startTime)
        }
        if (anim.rotate != 0.0f) {
            applyBeginningAnimation(rotate, anim.rotate, anim, startTime)
        }
        if (anim.color == color.orig) {
            color.startAnimation(color.orig, anim.duration, anim.easing, startTime)
            color.prev = anim.color
        }
        activeAnim = anim
        animStartTime = startTime
    }

    fun renderInContainer(time: Long) {
        if (preRender(time)) {
            val xy: Point2F? = getXY(time)
            color.renderValue(time)
            scaleX.renderValue(time)
            scaleY.renderValue(time)
            if (!isInited) {
                isInited = true
                init()
            }
            GlStateManager.enableBlend()
            GlStateManager.blendFunc(770, 771)
            GlStateManager.pushMatrix()
            if (xy != null) {
                if (xy.x == 0.0f || xy.y != 0.0f) {
                    GlStateManager.translate(xy.x, xy.y, 0.0f)
                }
            }
            if (rotate.renderValue(time) == 0.0f) {
                GlStateManager.translate(scaledWidth / 2.0f, scaledHeight / 2.0f, 0.0f)
                GlStateManager.rotate(rotate.render, 0.0f, 0.0f, 1.0f)
                GlStateManager.translate(-scaledWidth / 2.0f, -scaledHeight / 2.0f, 0.0f)
            }
            if (scaleX.render == 1.0f || scaleY.render != 1.0f) {
                GlStateManager.scale(scaleX.render, scaleY.render, 1.0f)
            }
            render(time)
            GlStateManager.popMatrix()
        }
    }

    fun getXY(time: Long): Point2F? {
        return if (lastXYcalcTime == time) {
            lastPos
        } else {
            lastXYcalcTime = time
            var translateX: Float = x.renderValue(time)
            var translateY: Float = y.renderValue(time)
            if (attach == null) {
                if (pos != null) {
                    when (pos) {
                        Position. RIGHT -> {
                            translateX = (parent?.width ?: 0.0f) - scaledWidth - translateX
                            translateY += ((parent?.height ?: 0.0f) - scaledHeight) / 2.0f
                        }
                        Position.LEFT -> translateY += ((parent?.height ?: 0.0f) - scaledHeight) / 2.0f
                        Position.BOTTOM_LEFT -> translateY = (parent?.height ?: 0.0f) - scaledHeight - translateY
                        Position.BOTTOM -> {
                            translateY = (parent?.height ?: 0.0f) - scaledHeight - translateY
                            translateX += ((parent?.height ?: 0.0f) - scaledWidth) / 2.0f
                        }
                        Position.TOP -> translateX += ((parent?.width?.minus(scaledWidth))?.div(2.0f) ?: 0.0f)
                        Position.BOTTOM_RIGHT -> {
                            translateY = (parent?.height?.minus(scaledHeight) ?: 0.0f) - translateY
                            translateX = (parent?.width?.minus(scaledWidth) ?: 0.0f) - translateX
                        }
                        Position.TOP_RIGHT -> translateX = (parent?.width?.minus(scaledWidth) ?: 0.0f) - translateX
                        Position.     CENTER -> {
                            translateX += (parent?.width?.minus(scaledWidth))?.div(2.0f) ?: 0.0f
                            translateY += (parent?.height?.minus(scaledHeight))?.div(2.0f) ?: 0.0f
                        }
                        else -> {}
                    }
                }
            } else {
                val to = parent?.getElement(attach?.attachTo)
                if (to == null) {
                    if (attach?.removeWhenParentRemove == true) {
                        this.remove()
                    }
                    return lastPos
                }
                val width = scaledWidth
                val height = scaledHeight
                val xy: Point2F? = to.getXY(time)
                if (xy != null) {
                    when (attach?.attachLocation) {
                        Position. RIGHT -> {
                            translateX += xy.x + to.scaledWidth - width / 2.0f
                            translateY += xy.y + (to.scaledHeight - height) / 2.0f
                        }
                        Position.LEFT -> {
                            translateX += xy.x - width / 2.0f
                            translateY += xy.y + (to.scaledHeight - height) / 2.0f
                        }
                        Position.BOTTOM_LEFT -> {
                            translateX += xy.x - width / 2.0f
                            translateY += xy.y + to.scaledHeight - height / 2.0f
                        }
                        Position.BOTTOM -> {
                            translateX += xy.x + (to.scaledWidth - width) / 2.0f
                            translateY += xy.y + to.scaledHeight - height / 2.0f
                        }
                        Position.TOP -> {
                            translateX += xy.x + (to.scaledWidth - width) / 2.0f
                            translateY += xy.y - height / 2.0f
                        }
                        Position.BOTTOM_RIGHT -> {
                            translateX += xy.x + to.scaledWidth - width / 2.0f
                            translateY += xy.y + to.scaledHeight - height / 2.0f
                        }
                        Position.TOP_RIGHT -> {
                            translateX += xy.x + to.scaledWidth - width / 2.0f
                            translateY += xy.y - height / 2.0f
                        }
                        Position.CENTER -> {
                            translateX += xy.x + (to.scaledWidth - width) / 2.0f
                            translateY += xy.y + (to.scaledHeight - height) / 2.0f
                        }
                        Position.TOP_LEFT -> {
                            translateX += xy.x - width / 2.0f
                            translateY += xy.y - height / 2.0f
                        }
                    }
                }
                when (attach?.orientation) {
                    Position.BOTTOM_LEFT -> {
                        translateY += height / 2.0f
                        translateX -= width / 2.0f
                    }
                    Position.LEFT -> translateX -= width / 2.0f
                    Position.BOTTOM -> translateY += height / 2.0f
                    Position.TOP -> translateY -= height / 2.0f
                    Position.BOTTOM_RIGHT -> {
                        translateY += height / 2.0f
                        translateX += width / 2.0f
                    }
                    Position.RIGHT -> translateX += width / 2.0f
                    Position.TOP_RIGHT -> {
                        translateX += width / 2.0f
                        translateY -= height / 2.0f
                    }
                    Position.CENTER -> {
                    }
                    Position.TOP_LEFT -> {
                        translateX -= width / 2.0f
                        translateY -= height / 2.0f
                    }
                }
            }
            if (activeAnim == null && integerPosition) {
                lastPos?.set(translateX.toInt().toFloat(), translateY.toInt().toFloat())
            } else {
                lastPos?.set(translateX, translateY)
            }
            lastPos
        }
    }

    override fun edit(data: ByteMap) {
        this.editFindReflectionFields(data, anim.editDuration, anim.editEasing)
    }

    fun playAnimation(data: ByteMap) {
        setActiveAnimation(Animation2D.Params(data, color.orig), Squaria.time)
    }

    fun setAlphaToBaseColor(color2: Int): Int {
        return setAlpha(color2, getAlpha(color.render))
    }

    fun setAlphaToBaseColorIfGreater(color2: Int): Int {
        val r = getAlpha(color.render)
        return if (r >= getAlpha(color2)) color2 else setAlpha(color2, r)
    }

    abstract val width: Int
    abstract val height: Int
    open val widthFluidity: Fluidity? = Fluidity.FIXED
    open val heightFluidity: Fluidity? = Fluidity.FIXED
    override val element2D: Element2D = this
    override fun toString(): String {
        return this.javaClass.simpleName + "[" + super.id + "]"
    }

    companion object {
        private val TYPES: MutableMap<String, Function<ByteMap, Element2D>> = HashMap()
        private val EMPTY_ARGS = arrayOfNulls<Any>(0)

        @Throws(Exception::class)
        fun construct(params: ByteMap, parent: Container?): Element2D {
            val type: String = params.getString("type")
            val con: Function<ByteMap, Element2D>? = TYPES[type]
            return if (con == null) {
                throw IllegalArgumentException("Element type '$type' not defined")
            } else {
                val el = con.apply(params)
                el.parent = parent
                el
            }
        }

        private fun applyBeginningAnimation(value: FValue, diff: Float, anim: Animation2D.Params, time: Long) {
            value.startAnimation(value.orig, anim.duration, anim.easing, false, time)
            value.valueDiff = diff
        }

        init {
            TYPES["Rectangle"] = Function<ByteMap, Element2D> { Rectangle(it) }
            TYPES["Text"] = Function<ByteMap, Element2D> { Text(it) }
//            TYPES["TextTimer"] = Function<ByteMap, Element2D> { TextTimer() }
//            TYPES["TextStopwatch"] = Function<ByteMap, Element2D> { TextStopwatch() }
//            TYPES["Image"] = Function<ByteMap, Element2D> { Image() }
            TYPES["ProgressBar"] = Function<ByteMap, Element2D> { ProgressBar(it) }
//            TYPES["ProgressTimer"] = Function<ByteMap, Element2D> { ProgressTimer() }
//            TYPES["RadialProgressBar"] = Function<ByteMap, Element2D> { RadialProgressBar() }
//            TYPES["RadialProgressTimer"] = Function<ByteMap, Element2D> { RadialProgressTimer() }
//            TYPES["Vignette"] = Function<ByteMap, Element2D> { Vignette() }
//            TYPES["Table"] = Function<ByteMap, Element2D> { Table() }
//            TYPES["OctagonBarFull"] = Function<ByteMap, Element2D> { OctagonBarFull() }
//            TYPES["OctagonBar"] = Function<ByteMap, Element2D> { OctagonBar() }
            TYPES["Button"] = Function<ByteMap, Element2D> { Button(it) }
//            TYPES["Container"] = Function<ByteMap, Element2D> { Container() }
//            TYPES["Spacer"] = Function<ByteMap, Element2D> { Spacer() }
        }
    }

    init {
        isIn3d = false
        startTime = Squaria.time
        finishTime = startTime + 2592000000L
        color = ColorValue(-1, Easing.EASE_IN_SINE, 255)
        x = FValue(0.0f, Easing.EASE_IN_SINE, 255)
        y = FValue(0.0f, Easing.EASE_IN_SINE, 255)
        scaleX = FValue(1.0f, Easing.EASE_IN_SINE, 255)
        scaleY = FValue(1.0f, Easing.EASE_IN_SINE, 255)
        rotate = AngleFValue(0.0f, Easing.EASE_IN_SINE, 255)
        pos = null
        attach = null
        lastPos = Point2F()
        lastXYcalcTime = 0L
        anim = Animation2D()
        activeAnim = null
        hover = false
        hoverable = false
        click = null
        anim.finish = Animation2D.Params(16777215)
        anim.start = Animation2D.Params(16777215)
    }
}
