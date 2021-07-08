package com.pepej.squaria.utils

import com.pepej.squaria.Squaria.Companion.LOG
import net.minecraft.client.Minecraft
import net.minecraft.util.math.Vec3d
import java.awt.Desktop
import java.io.IOException
import java.net.URI
import java.util.regex.Pattern

fun Vec3d.multiply(x: Double, y: Double, z: Double) = Vec3d(this.x * x, this.y * y, this.z * z)

private val URL_REGEXP = Pattern.compile("https?://(www\\.)?[-a-zA-Z0-9@:%._+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_+.~#?&/=]*)")

fun openUrl(url: String) {
    if (URL_REGEXP.matcher(url).matches()) {
        try {
            Desktop.getDesktop().browse(URI(url))
        } catch (e: Exception) {
            LOG.error("Couldn't open link ${e.message}. Trying Windows rundll32 feature...")
            e.printStackTrace()
            try {
                Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler $url")
            } catch (var3: IOException) {
                LOG.error("Couldn't open link with rundll32", var3)
            }
        }
    }
}

fun sendMessage(message: String) {
    if (message[0] != '/') {
        Minecraft.getMinecraft().ingameGUI.chatGUI.addToSentMessages(message)
    }
    Minecraft.getMinecraft().player.sendChatMessage(message)
}
