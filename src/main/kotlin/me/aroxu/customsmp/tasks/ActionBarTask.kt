package me.aroxu.customsmp.tasks

import me.aroxu.customsmp.CustomSMPPlugin
import me.aroxu.customsmp.CustomSMPPlugin.Companion.plugin
import me.aroxu.customsmp.utils.ColorByLife
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.GameMode

object ActionBarTask {
    // Repeating Task for ActionBar, run every 1 ticks. (20 times per seconds)
    fun registerRepeatingActionBarTask() {
        plugin.server.scheduler.scheduleSyncRepeatingTask(plugin, {
            plugin.server.onlinePlayers.forEach {
                if (CustomSMPPlugin.isPlayerDataReady[it.uniqueId]!! && it.gameMode == GameMode.SURVIVAL) {
                    val isTargetPlayerInWar = CustomSMPPlugin.isInWar[it.uniqueId]
                    val targetPlayerSurvivalLife = CustomSMPPlugin.survivalLife[it.uniqueId]
                    val targetPlayerSurvivalLifeActionBarText = "현재 남은 생존 목숨 : ${targetPlayerSurvivalLife!!}"

                    var actionBarTextComponent =
                        Component.text(targetPlayerSurvivalLifeActionBarText).decorate(TextDecoration.BOLD)
                            .color(ColorByLife.getSurvivalColorByLife(targetPlayerSurvivalLife))
                    if (isTargetPlayerInWar!!) {
                        actionBarTextComponent.append(Component.text(" | ").color(ColorByLife.WHITE))
                        val targetPlayerWarLife = CustomSMPPlugin.warLife[it.uniqueId]
                        val targetPlayerWarLifeActionBarText = "현재 남은 전쟁 목숨 : ${targetPlayerWarLife!!}"
                        actionBarTextComponent =
                            Component.text(targetPlayerSurvivalLifeActionBarText).decorate(TextDecoration.BOLD)
                                .color(ColorByLife.getSurvivalColorByLife(targetPlayerSurvivalLife))
                                .append(
                                    Component.text(" | ").decorate(TextDecoration.BOLD).color(
                                        ColorByLife.WHITE
                                    )
                                ).append(
                                    Component.text(targetPlayerWarLifeActionBarText).decorate(TextDecoration.BOLD)
                                        .color(ColorByLife.getWarColorByLife(targetPlayerWarLife))
                                )
                    }
                    it.sendActionBar(actionBarTextComponent)
                }
            }
        }, 0, 1)
    }
}
