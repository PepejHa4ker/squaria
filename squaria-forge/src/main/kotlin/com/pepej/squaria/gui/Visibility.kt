package com.pepej.squaria.gui

import com.pepej.squaria.Squaria.Companion.LOG
import com.pepej.squaria.serialization.ByteMap
import com.pepej.squaria.utils.Reflect
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.*
import net.minecraft.client.gui.inventory.*
import net.minecraft.inventory.IInventory
import net.minecraft.world.BossInfo
import java.util.*

class Visibility {
    val selectors: Array<Selector>
    var layer: GuiRenderLayer

    constructor(maps: Array<ByteMap>) {
        layer = GuiRenderLayer.HUD
        val selectorsList: MutableList<Selector> = ArrayList(maps.size)
        for (map in maps) {
            var selector: Selector? = null
            val type = map.getString("type")
            when (type) {
                "always" -> selector = AlwaysSelector()
                "ingame" -> selector = IngameSelector()
                "ehover" -> selector = ElementHoverSelector(map.getString("id"))
                "f3" -> selector = F3Selector()
                "ee" -> selector = ElementExistsSelector(map.getString("id"))
                "fps" -> selector = FpsSelector()
                "gui" -> {
                    val type = map.getString("name", "").uppercase(Locale.US)
                    try {
                        selector = GuiSelector(GuiSelector.Type.valueOf(type))
                    } catch (var13: Exception) {
                        LOG.warn("GuiSelector.Type with name $type not found")
                    }
                }
                "chat" -> selector = ChatSelector()
                "inv" -> selector = InventorySelector(map.getString("name"))
                "tab" -> selector = TabSelector()
                "hover" -> selector = HoverSelector()
                "layer" -> {
                    try {
                        layer = GuiRenderLayer.valueOf(map.getString("layer").uppercase(Locale.US))
                    } catch (e: Exception) {
                        LOG.warn("GuiRenderLayer with name ${map.getString("layer").uppercase(Locale.US)} not found")
                    }
                }
            }

            if (selector != null) {
                selector.show = map.getBoolean("show")
                selectorsList.add(selector)
            }
        }
        selectors = selectorsList.toTypedArray()
    }

    constructor(layer: GuiRenderLayer, vararg selectors: Selector) {
        this.layer = GuiRenderLayer.HUD
        this.layer = layer
        this.selectors = selectors as Array<Selector>
    }

    companion object {
        val DEFAULT: Visibility = Visibility(GuiRenderLayer.HUD, IngameSelector().setShow(true), ChatSelector().setShow(true))

    }

    class ElementHoverSelector(private val id: String) : Selector() {
        override fun acceptable(mc: Minecraft, wrapper: GuiElementWrapper): Boolean {
            val element = wrapper.element.parent?.getElement(id)
            return element != null && element.hover
        }
    }

    class HoverSelector : Selector() {
        override fun acceptable(mc: Minecraft, wrapper: GuiElementWrapper): Boolean {
            return wrapper.element.hover
        }
    }

    class ElementExistsSelector(private val id: String) : Selector() {
        override fun acceptable(mc: Minecraft, wrapper: GuiElementWrapper): Boolean {
            return wrapper.element.parent?.getElement(id) != null
        }
    }

//    class BossBarSelector : Selector() {
//        override fun acceptable(mc: Minecraft, wrapper: GuiElementWrapper): Boolean {
//            return BossInfoClient
//        }
//    }

    class FpsSelector : Selector() {
        override fun acceptable(mc: Minecraft, wrapper: GuiElementWrapper): Boolean {
            return!mc.gameSettings.showDebugInfo
        }
    }

    class TabSelector : Selector() {
        override fun acceptable(mc: Minecraft, wrapper: GuiElementWrapper): Boolean {
            return mc.ingameGUI.tabList != null
        }
    }

    class F3Selector : Selector() {
        override fun acceptable(mc: Minecraft, wrapper: GuiElementWrapper): Boolean {
            return mc.gameSettings.showDebugInfo
        }
    }

    class ChatSelector : Selector() {
        override fun acceptable(mc: Minecraft, wrapper: GuiElementWrapper): Boolean {
            return mc.currentScreen is GuiChat
        }
    }

    class InventorySelector(private val name: String) : Selector() {
        override fun acceptable(mc: Minecraft, wrapper: GuiElementWrapper): Boolean {
            return if (mc.currentScreen is GuiChest) {
             val chest = mc.currentScreen as GuiChest
               val field = Reflect.findField(chest.javaClass, "lowerChestInventory") ?: return false
                field.isAccessible = true
                val iInventory = field.get(chest) as IInventory
                iInventory.name.equals(name)
            } else {
                false
            }
        }
    }

    class GuiSelector(private val type: Type) : Selector() {
        override fun acceptable(mc: Minecraft, wrapper: GuiElementWrapper): Boolean {
            return mc.currentScreen != null && mc.currentScreen?.javaClass == type.clazz
        }

        enum class Type(var clazz: Class<out GuiScreen>) {
            ENCHANTMENT(GuiEnchantment::class.java), HOPPER(GuiHopper::class.java), MERCHANT(GuiMerchant::class.java), ANVIL(
                GuiRepair::class.java),
            BEACON(GuiBeacon::class.java), BREWING_STAND(GuiBrewingStand::class.java), CHEST(
                GuiChest::class.java),
            CRAFTING(GuiCrafting::class.java), DISPENSER(GuiDispenser::class.java), FURNACE(
                GuiFurnace::class.java),
            HORSE(GuiScreenHorseInventory::class.java), BOOK(GuiScreenBook::class.java), COMMAND_BLOCK(
                GuiCommandBlock::class.java),
            SIGN(GuiEditSign::class.java), INVENTORY(GuiInventory::class.java);
        }
    }

    class IngameSelector : Selector() {
        override fun acceptable(mc: Minecraft, wrapper: GuiElementWrapper): Boolean {
            return mc.currentScreen == null
        }
    }

    class AlwaysSelector : Selector() {
        override fun acceptable(mc: Minecraft, wrapper: GuiElementWrapper): Boolean {
            return true
        }
    }

    abstract class Selector(var show: Boolean = false) {
        abstract fun acceptable(mc: Minecraft, wrapper: GuiElementWrapper): Boolean
        fun setShow(value: Boolean): Selector {
            show = value
            return this
        }
    }
}