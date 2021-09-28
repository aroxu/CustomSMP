package me.aroxu.customsmp.events

import me.aroxu.customsmp.handler.PlayerDataHandler.handleData
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerLoginEvent

class LoginEvent : Listener {
    @EventHandler
    fun onPlayerLogin(event: PlayerLoginEvent) {
        val target = event.player
        handleData(target)
    }
}
