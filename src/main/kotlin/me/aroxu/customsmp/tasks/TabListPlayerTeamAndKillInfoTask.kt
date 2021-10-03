package me.aroxu.customsmp.tasks

import me.aroxu.customsmp.CustomSMPPlugin
import me.aroxu.customsmp.CustomSMPPlugin.Companion.plugin
import me.aroxu.customsmp.utils.BetterMaxHealth
import net.kyori.adventure.text.Component.text

object TabListPlayerTeamAndKillInfoTask {
    fun registerRepeatingTabListPlayerTeamAndKillInfoTask() {
        // Repeating Task for TabBar, run every 1 ticks. (20 times per seconds)
        plugin.server.scheduler.scheduleSyncRepeatingTask(plugin, {
            plugin.server.onlinePlayers.forEach {
                if (CustomSMPPlugin.isPlayerDataReady[it.uniqueId]!!) {
//                    val isTargetPlayerInWar = CustomSMPPlugin.isInWar[it.uniqueId]
                    val playerTeamName =
                        if (!CustomSMPPlugin.isInTeam[it.uniqueId]!!) {
                            ""
                        } else {
                            " [${
                                CustomSMPPlugin.teamsName[CustomSMPPlugin.teamsMember.filterValues { team ->
                                    team.contains(
                                        it.uniqueId
                                    )
                                }.keys.first()]!!
                            }]"
                        }
                    val team = text(playerTeamName)
                    it.playerListName(
                        text(
                            it.name
                        ).append(team).append(
                            text(
                                " | ${
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
