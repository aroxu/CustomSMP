package me.aroxu.customsmp.events

import me.aroxu.customsmp.CustomSMPPlugin.Companion.survivalLife
import me.aroxu.customsmp.database.DataManager
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent


class KillEvent: Listener {
    @EventHandler
    fun onPlayerDeath(event: PlayerDeathEvent) {
        val target = event.entity
        if (event.entity.killer is Player) {
            val killer = target.killer!!

            killer.maxHealth = killer.maxHealth + 1
        }
        survivalLife[target.uniqueId] = survivalLife[target.uniqueId]!!.minus(1)
        DataManager.setSurvivalLifeWithUuid(target.uniqueId.toString(), survivalLife[target.uniqueId]!!)
    }
}
