package me.aroxu.customsmp.events

import me.aroxu.customsmp.CustomSMPPlugin
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent

class PlayerMoveEvent : Listener {
    @EventHandler
    fun onPlayerMove(event: PlayerMoveEvent) {
        val target = event.player
        val targetPosition = target.location
        val destination = event.to
        CustomSMPPlugin.regionsName.forEach { region ->
            run {
                val regionPos = CustomSMPPlugin.regionsPos[region]!!
                if (destination.x in regionPos[0]..regionPos[2] && destination.y in regionPos[1]..regionPos[3]) {
                    println("Detected!!!!")
                    event.isCancelled = true
                }
            }
        }
    }
}
