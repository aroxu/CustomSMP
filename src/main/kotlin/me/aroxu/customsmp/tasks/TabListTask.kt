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
            plugin.server.onlinePlayers.forEach { player ->
                if (CustomSMPPlugin.isPlayerDataReady[player.uniqueId]!! && player.gameMode == GameMode.SURVIVAL) {
                    val isTargetPlayerInWar = CustomSMPPlugin.isInWar[player.uniqueId]
                    val team = if (player.scoreboard.teams.size < 1) {
                        text(" ")
                    } else {
                        text(" [").append(player.scoreboard.teams.first().displayName()).append(text("] "))

                    }
                    player.playerListName(
                        text(
                            "${player.name}"
                        ).append(team).append(
                            text(
                                "| ${
                                    (BetterMaxHealth.getMaxHealth(
                                        player
                                    ) - 20).toInt()
                                } Kills"
                            )
                        )
                    )
                }
            }
        }, 0, 5)
    }
}
