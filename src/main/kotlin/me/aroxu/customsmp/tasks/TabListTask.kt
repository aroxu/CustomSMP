package me.aroxu.customsmp.tasks

import me.aroxu.customsmp.CustomSMPPlugin
import me.aroxu.customsmp.CustomSMPPlugin.Companion.plugin
import me.aroxu.customsmp.utils.BetterMaxHealth
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.text
import org.bukkit.GameMode

object TabListTask {
    fun registerRepeatingTabListTask() {
        // Repeating Task for TabBar, run every 5 ticks. (4 times per seconds)
        plugin.server.scheduler.scheduleSyncRepeatingTask(plugin, {
            plugin.server.onlinePlayers.forEach {
                if (CustomSMPPlugin.isPlayerDataReady[it.uniqueId]!! && it.gameMode == GameMode.SURVIVAL) {
//                    val isTargetPlayerInWar = CustomSMPPlugin.isInWar[it.uniqueId]
                    val team = if (it.scoreboard.teams.size < 1) {
                        text(" ")
                    } else {
                        text(" [").append(it.scoreboard.teams.first().displayName()).append(text("] "))

                    }
                    it.playerListName(
                        text(
                            it.name
                        ).append(team).append(
                            text(
                                "| ${
                                    (BetterMaxHealth.getMaxHealth(
                                        it
                                    ) - 20).toInt()
                                } Kills"
                            )
                        )
                    )
                }
            }
        }, 0, 1)
    }
}
