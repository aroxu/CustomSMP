package me.aroxu.customsmp

import io.github.monun.kommand.kommand
import me.aroxu.customsmp.CustomSMPCommand.register
import me.aroxu.customsmp.database.DataManager
import me.aroxu.customsmp.events.EventInitializer
import me.aroxu.customsmp.handler.DataHandler.handlePlayerData
import me.aroxu.customsmp.handler.DataHandler.handleTeamsData
import me.aroxu.customsmp.tasks.ActionBarTask
import me.aroxu.customsmp.tasks.TabListTask
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
        val isNewDbFile = !dbFile.exists()
        DataManager.initDatabaseWithSqlite(dbFile, isNewDbFile)

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

        ActionBarTask.registerRepeatingActionBarTask()
        TabListTask.registerRepeatingTabListTask()
    }
}
