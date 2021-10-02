package me.aroxu.customsmp.events

import me.aroxu.customsmp.CustomSMPPlugin.Companion.plugin
import me.aroxu.customsmp.CustomSMPPlugin.Companion.survivalLife
import me.aroxu.customsmp.database.DataManager
import me.aroxu.customsmp.utils.BetterMaxHealth
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.sound.Sound.sound
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent


class KillEvent : Listener {
    @EventHandler
    fun onPlayerDeath(event: PlayerDeathEvent) {
        val target = event.entity
        if (event.entity.killer is Player) {
            val killer = target.killer!!

            BetterMaxHealth.setMaxHealth(killer, BetterMaxHealth.getMaxHealth(killer) + 1)
        }
        survivalLife[target.uniqueId] = survivalLife[target.uniqueId]!!.minus(1)
        DataManager.setSurvivalLifeWithUuid(target.uniqueId, survivalLife[target.uniqueId]!!)
        if (survivalLife[target.uniqueId]!! <= 0) {
            target.gameMode = GameMode.SPECTATOR
            plugin.server.onlinePlayers.forEach {
                run {
                    if (it.uniqueId == target.uniqueId) {
                        it.sendMessage(
                            text("당신의 생존 목숨이 전부 소진되어 노예 상태가 되었습니다.")
                                .color(TextColor.color(0xFF0000)).decorate(TextDecoration.BOLD)
                        )
                    } else {
                        if (it.isOp) {
                            it.playSound(
                                sound(
                                    Key.key("block.note_block.pling"),
                                    Sound.Source.AMBIENT,
                                    10.0f,
                                    2.0f
                                )
                            )
                        }
                        it.sendMessage(
                            text("플레이어 ${target.name} 생존 목숨이 전부 소진되어 노예 상태가 되었습니다.")
                                .color(TextColor.color(0xFF0000)).decorate(TextDecoration.BOLD)
                        )
                    }
                }
            }
        }
    }
}
