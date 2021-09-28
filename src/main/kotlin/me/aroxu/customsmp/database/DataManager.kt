package me.aroxu.customsmp.database

import me.aroxu.customsmp.CustomSMPPlugin
import me.aroxu.customsmp.CustomSMPPlugin.Companion.plugin
import me.aroxu.customsmp.database.objects.IsInWar
import me.aroxu.customsmp.database.objects.SurvivalLife
import me.aroxu.customsmp.database.objects.WarLife
import me.aroxu.customsmp.utils.BoolConvert
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File
import java.util.*
import kotlin.NoSuchElementException

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
                        "life"	INTEGER NOT NULL,
                        PRIMARY KEY("player")
                    );
                    """
                )
                exec(
                    """
                    CREATE TABLE "WarLife" (
                        "player"	TEXT NOT NULL UNIQUE,
                        "life"	INTEGER NOT NULL,
                        PRIMARY KEY("player")
                    );
                    """
                )
                exec(
                    """
                    CREATE TABLE "IsInWar" (
                        "player"    TEXT NOT NULL UNIQUE,
                        "isinwar"    INTEGER NOT NULL,
                        PRIMARY KEY("player")
                    );
                    """
                )
            }
        }
    }

    // Returns Player's Survival Life. If there is no data with target player, returns -1
    fun getSurvivalLifeWithUuid(targetPlayer: String): Int {
        var result = -1
        transaction {
            try {
                SurvivalLife.select {
                    SurvivalLife.player eq targetPlayer
                }.single().also { result = it[SurvivalLife.life] }
            } catch (e: NoSuchElementException) {
                plugin.logger.warning("[DataBase] 해당 유저를 찾을 수 없습니다.")
            }
        }
        return result
    }

    fun setSurvivalLifeWithUuid(targetPlayer: String, targetLife: Int) {
        println(targetPlayer)
        println(targetLife)
        transaction {
            try {
                SurvivalLife.select {
                    SurvivalLife.player eq targetPlayer
                }.single()
                SurvivalLife.update({ SurvivalLife.player eq targetPlayer }) {
                    it[life] = targetLife
                }
            } catch (e: NoSuchElementException) {
                SurvivalLife.insert {
                    it[player] = targetPlayer
                    it[life] = targetLife
                }
            }
        }
        CustomSMPPlugin.survivalLife[UUID.fromString(targetPlayer)] = targetLife
    }


    // Returns Player's War Life. If there is no data with target player, returns -1
    fun getWarLifeWithUuid(targetPlayer: String): Int {
        var result = -1
        transaction {
            try {
                WarLife.select {
                    WarLife.player eq targetPlayer
                }.single().also { result = it[WarLife.life] }
            } catch (e: NoSuchElementException) {
                plugin.logger.warning("[DataBase] 해당 유저를 찾을 수 없습니다.")
            }
        }
        return result
    }

    fun setWarLifeWithUuid(targetPlayer: String, targetLife: Int) {
        transaction {
            try {
                WarLife.select {
                    WarLife.player eq targetPlayer
                }.single()
                WarLife.update({WarLife.player eq targetPlayer}) {
                    it[life] = targetLife
                }
            } catch (e: NoSuchElementException) {
                WarLife.insert {
                    it[player] = targetPlayer
                    it[life] = targetLife
                }
            }
        }
        CustomSMPPlugin.warLife[UUID.fromString(targetPlayer)] = targetLife
    }


    // Checks is target Player is in War. If there is no data with target player, returns false
    fun getIsInWarWithUuid(targetPlayer: String): Boolean {
        var result = false
        try {
            transaction {
                IsInWar.select {
                    IsInWar.player eq targetPlayer
                }.single().also { result = BoolConvert.IntToBool(it[IsInWar.isInWar]) }
            }
        } catch (e: NoSuchElementException) {
            plugin.logger.warning("[DataBase] 해당 유저를 찾을 수 없습니다.")
        }
        return result
    }

    fun setIsInWarWithUuid(targetPlayer: String, targetIsInWar: Boolean) {
        transaction {
            try {
                IsInWar.select {
                    IsInWar.player eq targetPlayer
                }.single()
                IsInWar.update({IsInWar.player eq targetPlayer}) {
                    it[isInWar] = BoolConvert.BoolToInt(targetIsInWar)
                }
            } catch (e: NoSuchElementException) {
                IsInWar.insert {
                    it[player] = targetPlayer
                    it[isInWar] = BoolConvert.BoolToInt(targetIsInWar)
                }
            }
        }
        CustomSMPPlugin.isInWar[UUID.fromString(targetPlayer)] = targetIsInWar
    }
}
