package me.aroxu.customsmp.tasks

import me.aroxu.customsmp.CustomSMPPlugin
import me.aroxu.customsmp.CustomSMPPlugin.Companion.plugin
import me.aroxu.customsmp.utils.ColorByLife
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.GameMode

object TabListPlayerLifeInfoTask {
    // Repeating Task for ActionBar, run every 1 ticks. (20 times per seconds)
    fun registerRepeatingTabListPlayerLifeInfoTask() {
        plugin.server.scheduler.scheduleSyncRepeatingTask(plugin, {
            plugin.server.onlinePlayers.forEach {
                if (CustomSMPPlugin.isPlayerDataReady[it.uniqueId]!!) {
                    val isTargetPlayerInWar = CustomSMPPlugin.isInWar[it.uniqueId]
                    val targetPlayerSurvivalLife = CustomSMPPlugin.survivalLife[it.uniqueId]
                    val targetPlayerSurvivalLifeActionBarText = "현재 남은 생존 목숨 : ${targetPlayerSurvivalLife!!}"

                    var actionBarTextComponent =
                        Component.text(targetPlayerSurvivalLifeActionBarText).decorate(TextDecoration.BOLD)
                            .color(ColorByLife.getSurvivalColorByLife(targetPlayerSurvivalLife))
                    if (isTargetPlayerInWar!!) {
                        val targetPlayerWarLife = CustomSMPPlugin.warLife[it.uniqueId]
                        val targetPlayerWarLifeActionBarText = "현재 남은 전쟁 목숨 : ${targetPlayerWarLife!!}"
                        actionBarTextComponent =
                            Component.text(targetPlayerWarLifeActionBarText).decorate(TextDecoration.BOLD)
                                .color(ColorByLife.getWarColorByLife(targetPlayerWarLife))
                    }
                    it.sendPlayerListFooter(actionBarTextComponent)
                }
            }
        }, 0, 1)
    }
}
