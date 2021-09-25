package me.aroxu.customsmp

import io.github.monun.kommand.kommand
import me.aroxu.customsmp.CustomSMPCommand.register
import me.aroxu.customsmp.events.EventInitializer
import org.bukkit.plugin.java.JavaPlugin

/**
 * @author aroxu
 */

class CustomSMPPlugin : JavaPlugin() {
    companion object {
        lateinit var plugin: JavaPlugin
            private set
    }

    override fun onEnable() {
        plugin = this
        EventInitializer.initEvent(plugin, server)

        kommand {
            register("customsmp") {
                register(this)
            }
        }
    }
}
