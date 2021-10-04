package me.aroxu.customsmp.events

import org.bukkit.Server
import org.bukkit.plugin.java.JavaPlugin

object EventInitializer {
    fun initEvent(plugin: JavaPlugin, server: Server) {
        server.pluginManager.registerEvents(KillEvent(), plugin)
        server.pluginManager.registerEvents(LoginEvent(), plugin)
        server.pluginManager.registerEvents(AttackEvent(), plugin)
        server.pluginManager.registerEvents(PlayerMoveEvent(), plugin)
    }
}
