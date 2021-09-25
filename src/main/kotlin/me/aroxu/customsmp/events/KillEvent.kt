package me.aroxu.customsmp.events

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent


class KillEvent: Listener {
    @EventHandler
    fun onPlayerDeathByPlayer(event: PlayerDeathEvent) {
        if (event.entity.killer is Player) {
            val target = event.entity
            val killer = target.killer!!

            killer.maxHealth = killer.maxHealth + 1
        }
    }
}