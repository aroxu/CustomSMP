package me.aroxu.customsmp.events

import me.aroxu.customsmp.CustomSMPPlugin
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent

class AttackEvent : Listener {
    @EventHandler
    fun onPlayerLogin(event: EntityDamageByEntityEvent) {
        val target = event.entity
        val damager = event.damager
        if (target is Player  && damager is Player) {
            if (CustomSMPPlugin.isInTeam[target.uniqueId]!! && CustomSMPPlugin.isInTeam[damager.uniqueId]!!) {
                if (CustomSMPPlugin.playerTeam[target.uniqueId]!! == CustomSMPPlugin.playerTeam[damager.uniqueId]!!) {
                    event.isCancelled = true
                }
            }
        }
    }
}
