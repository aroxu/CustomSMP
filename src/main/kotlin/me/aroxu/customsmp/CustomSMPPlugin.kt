package me.aroxu.customsmp

import io.github.monun.kommand.kommand
import me.aroxu.customsmp.CustomSMPCommand.register
import me.aroxu.customsmp.database.DataManager
import me.aroxu.customsmp.events.EventInitializer
import me.aroxu.customsmp.handler.PlayerDataHandler.handleData
import me.aroxu.customsmp.utils.ColorByLife
import me.aroxu.customsmp.utils.ColorByLife.WHITE
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.GameMode
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.util.*
import kotlin.collections.HashMap

/**
 * @author aroxu
 */

class CustomSMPPlugin : JavaPlugin() {
    companion object {
        lateinit var plugin: JavaPlugin
            private set
        var survivalLife = HashMap<UUID, Int>()
        var warLife = HashMap<UUID, Int>()
        var isInWar = HashMap<UUID, Boolean>()
        var isPlayerDataReady = HashMap<UUID, Boolean>()
    }

    override fun onEnable() {
        plugin = this
        // Initialize Plugin Events
        EventInitializer.initEvent(plugin, server)

        // Initialize Plugin Database
        val dbFile = File(plugin.dataFolder, "csmptw.db")
        dbFile.absoluteFile.parentFile.mkdirs()
        val isNewDbFile = !dbFile.exists()
        DataManager.initDatabaseWithSqlite(dbFile, isNewDbFile)

        // Initialize Plugin Command Handler
        kommand {
            register("smp") {
                register(this)
            }
        }

        // For reload event
        server.onlinePlayers.forEach { player ->
            run {
                handleData(player)
            }
        }

        // Repeating Task for ActionBar, run every 5 ticks. (4 times per seconds)
        server.scheduler.scheduleSyncRepeatingTask(plugin, {
            server.onlinePlayers.forEach { player ->
                if (isPlayerDataReady[player.uniqueId]!! && player.gameMode == GameMode.SURVIVAL) {
                    val isTargetPlayerInWar = isInWar[player.uniqueId]
                    val targetPlayerSurvivalLife = survivalLife[player.uniqueId]
                    val targetPlayerSurvivalLifeActionBarText = "현재 남은 생존 목숨 : ${targetPlayerSurvivalLife!!}"

                    var actionBarTextComponent =
                        text(targetPlayerSurvivalLifeActionBarText).decorate(TextDecoration.BOLD)
                            .color(ColorByLife.getSurvivalColorByLife(targetPlayerSurvivalLife))
                    if (isTargetPlayerInWar!!) {
                        actionBarTextComponent.append(text(" | ").color(WHITE))
                        val targetPlayerWarLife = warLife[player.uniqueId]
                        val targetPlayerWarLifeActionBarText = "현재 남은 전쟁 목숨 : ${targetPlayerWarLife!!}"
                        actionBarTextComponent =
                            text(targetPlayerSurvivalLifeActionBarText).decorate(TextDecoration.BOLD)
                                .color(ColorByLife.getSurvivalColorByLife(targetPlayerSurvivalLife))
                                .append(
                                    text(" | ").decorate(TextDecoration.BOLD).color(
                                        WHITE
                                    )
                                ).append(
                                    text(targetPlayerWarLifeActionBarText).decorate(TextDecoration.BOLD)
                                        .color(ColorByLife.getWarColorByLife(targetPlayerWarLife))
                                )
                    }
                    player.sendActionBar(actionBarTextComponent)
                }
            }
        }, 0, 5)
    }
}
