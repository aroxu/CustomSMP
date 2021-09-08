package me.aroxu.customsmp

import io.github.monun.kommand.kommand
import me.aroxu.customsmp.CustomSMPCommand.register
import org.bukkit.plugin.java.JavaPlugin

/**
 * @author aroxu
 */

class CustomSMPPlugin : JavaPlugin() {
    override fun onEnable() {
        kommand {
            register("customsmp") {
                register(this)
            }
        }
    }
}
