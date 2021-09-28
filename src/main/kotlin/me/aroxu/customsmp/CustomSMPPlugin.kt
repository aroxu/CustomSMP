package me.aroxu.customsmp

import io.github.monun.kommand.kommand
import me.aroxu.customsmp.CustomSMPCommand.register
import me.aroxu.customsmp.database.DataManager
import me.aroxu.customsmp.events.EventInitializer
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
        lateinit var survivalLife: HashMap<UUID, Int>
            private set
        lateinit var warLife: HashMap<UUID, Int>
            private set
    }

    override fun onEnable() {
        plugin = this
        EventInitializer.initEvent(plugin, server)

        val dbFile = File(plugin.dataFolder, "csmptw.db")
        dbFile.absoluteFile.parentFile.mkdirs()
        val isNewDbFile = !dbFile.exists()
        DataManager.initDatabaseWithSqlite(dbFile, isNewDbFile)

        DataManager.getSurvivalLifeWithUuid("762dea11-9c45-4b18-95fc-a86aab3b39ee")
        DataManager.setSurvivalLifeWithUuid("762dea11-9c45-4b18-95fc-a86aab3b39ee", 20)
        DataManager.getSurvivalLifeWithUuid("762dea11-9c45-4b18-95fc-a86aab3b39ee")
        DataManager.setSurvivalLifeWithUuid("762dea11-9c45-4b18-95fc-a86aab3b39ee", 20)

        kommand {
            register("customsmp") {
                register(this)
            }
        }
    }
}
