package me.aroxu.customsmp.events

import me.aroxu.customsmp.CustomSMPPlugin
import org.bukkit.Bukkit
import org.bukkit.entity.Arrow
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent

class AttackEvent : Listener {
    @EventHandler
    fun onEntityDamageByEntity(event: EntityDamageByEntityEvent) {
        val target = event.entity
        val damager = event.damager
        if (target is Player  && damager is Player || (event.damager as Arrow).shooter is Player) {
            if (CustomSMPPlugin.isInTeam[target.uniqueId]!! && CustomSMPPlugin.isInTeam[damager.uniqueId]!!) {
                if (CustomSMPPlugin.playerTeam[target.uniqueId]!! == CustomSMPPlugin.playerTeam[damager.uniqueId]!!) {
                    event.isCancelled = true
                }
            }
            else {
                val task = Bukkit.getServer().scheduler.runTaskTimer(CustomSMPPlugin.plugin,
                    Runnable{
                        (target as Player).isGliding = false
                        if(damager is Arrow) (damager.shooter as Player).isGliding = false
                        else (damager as Player).isGliding = false
                },0L,0L)

                Bukkit.getServer().scheduler.runTaskLater(CustomSMPPlugin.plugin,
                    Runnable{
                        task.cancel()
                }, 100L)

            }
        }
    }
}
