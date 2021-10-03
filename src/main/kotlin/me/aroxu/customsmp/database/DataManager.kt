package me.aroxu.customsmp.database

import me.aroxu.customsmp.CustomSMPPlugin
import me.aroxu.customsmp.CustomSMPPlugin.Companion.plugin
import me.aroxu.customsmp.database.objects.*
import me.aroxu.customsmp.database.objects.TeamsUuid.uuid
import me.aroxu.customsmp.utils.BoolConvert
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File
import java.util.*
import kotlin.NoSuchElementException

object DataManager {
    private lateinit var db: Database
    fun initDatabaseWithSqlite(dbPath: File, isNewFile: Boolean) {
        db = Database.connect("jdbc:sqlite:${dbPath.absolutePath}", "org.sqlite.JDBC")
        println("Database connected with: ${dbPath.absolutePath}")
        transaction {
            addLogger(StdOutSqlLogger)
            if (isNewFile) {
                println("New Database Detected! Initializing...")
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
                exec(
                    """
                    CREATE TABLE "TeamsName" (
                        "uuid"    TEXT NOT NULL UNIQUE,
                        "name"    TEXT NOT NULL UNIQUE,
                        PRIMARY KEY("uuid")
                    );
                    """
                )
                exec(
                    """
                    CREATE TABLE "TeamsUuid" (
                        "uuid"    TEXT NOT NULL UNIQUE,
                        PRIMARY KEY("uuid")
                    );
                    """
                )
                exec(
                    """
                    CREATE TABLE "TeamsMember" (
                        "uuid"    TEXT NOT NULL UNIQUE,
                        "members"    TEXT NOT NULL,
                        PRIMARY KEY("uuid")
                    );
                    """
                )
            }
        }
    }

    // Returns Player's Survival Life. If there is no data with target player, returns -1
    fun getSurvivalLifeWithUuid(targetPlayerUuid: UUID): Int {
        var result = -1
        transaction {
            try {
                SurvivalLife.select {
                    SurvivalLife.player eq targetPlayerUuid.toString()
                }.single().also { result = it[SurvivalLife.life] }
            } catch (e: NoSuchElementException) {
                plugin.logger.warning("[DataBase] 해당 조건에 만족하는 데이터가 없습니다.")
            }
        }
        return result
    }

    fun setSurvivalLifeWithUuid(targetPlayerUuid: UUID, targetPlayerLife: Int) {
        transaction {
            try {
                SurvivalLife.select {
                    SurvivalLife.player eq targetPlayerUuid.toString()
                }.single()
                SurvivalLife.update({ SurvivalLife.player eq targetPlayerUuid.toString() }) {
                    it[life] = targetPlayerLife
                }
            } catch (e: NoSuchElementException) {
                SurvivalLife.insert {
                    it[player] = targetPlayerUuid.toString()
                    it[life] = targetPlayerLife
                }
            }
        }
        CustomSMPPlugin.survivalLife[targetPlayerUuid] = targetPlayerLife
    }


    // Returns Player's War Life. If there is no data with target player, returns -1
    fun getWarLifeWithUuid(targetPlayerUuid: UUID): Int {
        var result = -1
        transaction {
            try {
                WarLife.select {
                    WarLife.player eq targetPlayerUuid.toString()
                }.single().also { result = it[WarLife.life] }
            } catch (e: NoSuchElementException) {
                plugin.logger.warning("[DataBase] 해당 조건에 만족하는 데이터가 없습니다.")
            }
        }
        return result
    }

    fun setWarLifeWithUuid(targetPlayerUuid: UUID, targetLife: Int) {
        transaction {
            try {
                WarLife.select {
                    WarLife.player eq targetPlayerUuid.toString()
                }.single()
                WarLife.update({WarLife.player eq targetPlayerUuid.toString()}) {
                    it[life] = targetLife
                }
            } catch (e: NoSuchElementException) {
                WarLife.insert {
                    it[player] = targetPlayerUuid.toString()
                    it[life] = targetLife
                }
            }
        }
        CustomSMPPlugin.warLife[targetPlayerUuid] = targetLife
    }


    // Checks is target Player is in War. If there is no data with target player, returns false
    fun getIsInWarWithUuid(targetPlayerUuid: UUID): Boolean {
        var result = false
        try {
            transaction {
                IsInWar.select {
                    IsInWar.player eq targetPlayerUuid.toString()
                }.single().also { result = BoolConvert.intToBool(it[IsInWar.isInWar]) }
            }
        } catch (e: NoSuchElementException) {
            plugin.logger.warning("[DataBase] 해당 조건에 만족하는 데이터가 없습니다.")
        }
        return result
    }

    fun setIsInWarWithUuid(targetPlayerUuid: UUID, isTargetPlayerInWar: Boolean) {
        transaction {
            try {
                IsInWar.select {
                    IsInWar.player eq targetPlayerUuid.toString()
                }.single()
                IsInWar.update({IsInWar.player eq targetPlayerUuid.toString()}) {
                    it[isInWar] = BoolConvert.boolToInt(isTargetPlayerInWar)
                }
            } catch (e: NoSuchElementException) {
                IsInWar.insert {
                    it[player] = targetPlayerUuid.toString()
                    it[isInWar] = BoolConvert.boolToInt(isTargetPlayerInWar)
                }
            }
        }
        CustomSMPPlugin.isInWar[targetPlayerUuid] = isTargetPlayerInWar
    }

    // Get Team name with Team's UUID. returns String
    fun getTeamNameWithUuid(targetTeamUuid: UUID): String {
        var result = ""
        try {
            transaction {
                TeamsName.select {
                    TeamsName.uuid eq targetTeamUuid.toString()
                }.single().also { result = it[TeamsName.name] }
            }
        } catch (e: NoSuchElementException) {
            plugin.logger.warning("[DataBase] 해당 조건에 만족하는 데이터가 없습니다.")
        }
        return result
    }

    fun setTeamNameWithUuid(targetTeamUuid: UUID, targetTeamName: String) {
        transaction {
            try {
                TeamsName.select {
                    TeamsName.uuid eq targetTeamUuid.toString()
                }.single()
                TeamsName.update({TeamsName.uuid eq targetTeamUuid.toString()}) {
                    it[name] = targetTeamName
                }
            } catch (e: NoSuchElementException) {
                TeamsName.insert {
                    it[uuid] = targetTeamUuid.toString()
                    it[name] = targetTeamName
                }
            }
        }
        CustomSMPPlugin.teamsName[targetTeamUuid] = targetTeamName
    }

    // Get Team members with Team's UUID. returns List<UUID>
    fun getTeamMembersWithUuid(targetTeamUuid: UUID): List<UUID> {
        val result = emptyList<UUID>()
        try {
            transaction {
                TeamsMember.select {
                    TeamsMember.uuid eq targetTeamUuid.toString()
                }.single().also { query ->
                    query[TeamsMember.members].split(", ").forEach {
                    result.plus(UUID.fromString(it))
                } }
            }
        } catch (e: NoSuchElementException) {
            plugin.logger.warning("[DataBase] 해당 조건에 만족하는 데이터가 없습니다.")
        }
        return result
    }

    fun setTeamMembersWithUuid(targetTeamUuid: UUID, targetTeamMembers: List<UUID>) {
        transaction {
            try {
                TeamsMember.select {
                    TeamsName.uuid eq targetTeamUuid.toString()
                }.single()
                TeamsMember.update({TeamsMember.uuid eq targetTeamUuid.toString()}) {
                    it[members] = targetTeamMembers.joinToString(", ")
                }
            } catch (e: NoSuchElementException) {
                TeamsMember.insert {
                    it[uuid] = targetTeamUuid.toString()
                    it[members] = targetTeamMembers.joinToString(", ")
                }
            }
        }
        CustomSMPPlugin.teamsMember[targetTeamUuid] = targetTeamMembers
    }

    // Get List of all team's UUID. returns List<UUID>
    fun getAllTeamUuids(): List<UUID> {
        val result = emptyList<UUID>()
        try {
            transaction {
                TeamsUuid.selectAll().also { queries ->
                    run {
                        queries.forEach { query ->
                            run {
                                println(query[uuid])
                                result.plus(query[uuid])
                            }
                        }
                    }
                }
            }
        } catch (e: NoSuchElementException) {
            plugin.logger.warning("[DataBase] 해당 조건에 만족하는 데이터가 없습니다.")
        }
        return result
    }

    fun addToTeamUuids(targetTeamUuid: UUID) {
        transaction {
            TeamsMember.insert {
                it[uuid] = targetTeamUuid.toString()
            }
        }
        CustomSMPPlugin.teamsUuid.plus(targetTeamUuid)
    }

    fun removeFromTeamUuids(targetTeamUuid: UUID) {
        transaction {
            TeamsName.deleteWhere {
                uuid eq targetTeamUuid.toString()
            }
            TeamsMember.deleteWhere {
                uuid eq targetTeamUuid.toString()
            }
            TeamsUuid.deleteWhere {
                uuid eq targetTeamUuid.toString()
            }
        }
        CustomSMPPlugin.teamsName.remove(targetTeamUuid)
        CustomSMPPlugin.teamsMember.remove(targetTeamUuid)
        CustomSMPPlugin.teamsUuid.minus(targetTeamUuid)
    }
}
