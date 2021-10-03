package me.aroxu.customsmp

import io.github.monun.kommand.kommand
import me.aroxu.customsmp.CustomSMPCommand.register
import me.aroxu.customsmp.database.DataManager
import me.aroxu.customsmp.events.EventInitializer
import me.aroxu.customsmp.handler.DataHandler.handlePlayerData
import me.aroxu.customsmp.handler.DataHandler.handleTeamsData
import me.aroxu.customsmp.tasks.TabListPlayerLifeInfoTask
import me.aroxu.customsmp.tasks.TabListPlayerTeamAndKillInfoTask
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
        var survivalLife:HashMap<UUID, Int> = HashMap()
        var warLife:HashMap<UUID, Int> = HashMap()
        var isInWar:HashMap<UUID, Boolean> = HashMap()
        var isInTeam:HashMap<UUID, Boolean> = HashMap()
        var playerTeam:HashMap<UUID, UUID> = HashMap()
        var isPlayerDataReady:HashMap<UUID, Boolean> = HashMap()
        var teamsName:HashMap<UUID, String> = HashMap()
        var teamsMember: HashMap<UUID, List<UUID>> = HashMap()
        var teamsUuid: List<UUID> = emptyList()
    }

    override fun onEnable() {
        plugin = this
        // Initialize Plugin Events
        EventInitializer.initEvent(plugin, server)

        // Initialize Plugin Database
        val dbFile = File(plugin.dataFolder, "csmptw.db")
        dbFile.absoluteFile.parentFile.mkdirs()
        DataManager.initDatabaseWithSqlite(dbFile, !dbFile.exists())

        // Initialize Plugin Command Handler
        kommand {
            register("smp") {
                register(this)
            }
        }

        // For reload event
        server.onlinePlayers.forEach {
            handlePlayerData(it)
        }
        handleTeamsData()

        TabListPlayerLifeInfoTask.registerRepeatingTabListPlayerLifeInfoTask()
        TabListPlayerTeamAndKillInfoTask.registerRepeatingTabListPlayerTeamAndKillInfoTask()
    }
}
