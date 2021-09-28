package me.aroxu.customsmp.database

import me.aroxu.customsmp.database.objects.SurvivalLife
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File
import java.util.*

object DataManager {
    lateinit var db: Database
    fun initDatabaseWithSqlite(dbPath: File, isNewFile: Boolean) {
        db = Database.connect("jdbc:sqlite:${dbPath.absolutePath}", "org.sqlite.JDBC")
        println("Database connected with: ${dbPath.absolutePath}")
        transaction {
            addLogger(StdOutSqlLogger)
            if (isNewFile) {
                exec(
                    """
                    CREATE TABLE "SurvivalLife" (
                        "player"	TEXT NOT NULL UNIQUE,
                        "life"	INT NOT NULL,
                        PRIMARY KEY("player")
                    );
                    """
                )
            }
        }
    }

    fun getSurvivalLifeWithUuid(player: String) {
        transaction {
            val lifeQuery = SurvivalLife.select {
                SurvivalLife.player eq player
            }
            lifeQuery.where
            return@transaction lifeQuery
        }
    }

    fun setSurvivalLifeWithUuid(targetPlayer: String, targetLife: Int) {
        transaction {
            SurvivalLife.insertIgnore {
                it[player] = targetPlayer
                it[life] = targetLife
            }
        }
    }
}