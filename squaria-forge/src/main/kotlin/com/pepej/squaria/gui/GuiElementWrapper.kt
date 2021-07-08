package com.pepej.squaria.gui

import com.pepej.squaria.elements.Element
import com.pepej.squaria.elements.Element2D
import com.pepej.squaria.elements.container.Container
import com.pepej.squaria.elements.container.Element2DWrapper
import com.pepej.squaria.serialization.ByteMap
import com.pepej.squaria.utils.Position
import net.minecraft.client.Minecraft

class GuiElementWrapper : Element, Element2DWrapper {
    var element: Element2D
    var visibility: Visibility
    var visible: Boolean

    constructor(params: ByteMap, parent: Container?) : super(params.getString("id")) {
        visibility = Visibility.DEFAULT
        visible = false
        element = Element2D.construct(params, parent)
        if (element.pos == null) {
            element.pos = Position.CENTER
        }
        if (params.containsKey("vis")) {
            visibility = Visibility(params.getMapArray("vis")!!)
        }
    }

    constructor(element: Element2D, parent: Container?) : this(element, Visibility.DEFAULT, parent)
    constructor(element: Element2D, visibility: Visibility, parent: Container?) : super(element.id) {
        this.element = element
        this.visibility = Visibility.DEFAULT
        visible = false
        element.parent = parent
        this.visibility = visibility
    }

    fun checkHover(mc: Minecraft, time: Long) {
        if (element.hoverable) {
            if (mc.currentScreen != null) {
                val xy = element.getXY(time)
                if (xy != null) {
                    element.checkHover(((SquariaGui.mouseX.toFloat() - xy.x) / element.scaleX.render).toInt(), ((SquariaGui.mouseY.toFloat() - xy.y) / element.scaleY.render).toInt(), time)
                }
            } else {
                element.checkHover(-1, -1, time)
            }
        }
    }

    override fun render(time: Long) {
        element.renderInContainer(time)
    }

    fun isVisible(mc: Minecraft): Boolean {
        var show = false
        for (selector in visibility.selectors) {
            if (selector.acceptable(mc, this)) {
                show = selector.show
            }
        }
        visible = show
        return show
    }

    override fun edit(data: ByteMap) {
        element.edit(data)
    }

    override fun toString(): String {
        return "Gui $element"
    }

    override val element2D: Element2D
        get() = element

    override fun hashCode(): Int {
        return element.hashCode()
    }

    override fun equals(obj: Any?): Boolean {
        return obj === this
    }
}