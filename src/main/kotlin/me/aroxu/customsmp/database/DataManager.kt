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
                    CREATE TABLE "IsInTeam" (
                        "player"    TEXT NOT NULL UNIQUE,
                        "isinteam"    INTEGER NOT NULL,
                        PRIMARY KEY("player")
                    );
                    """
                )
                exec(
                    """
                    CREATE TABLE "PlayerTeam" (
                        "player"    TEXT NOT NULL UNIQUE,
                        "team"    TEXT NOT NULL,
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
    fun getSurvivalLifeWithUuid(targetPlayerUuid: UUID): Int? {
        var result: Int? = null
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
    fun getWarLifeWithUuid(targetPlayerUuid: UUID): Int? {
        var result: Int? = null
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


    // Checks is target Player is in War. If there is no data with target player, returns false
    fun getIsInTeamWithUuid(targetPlayerUuid: UUID): Boolean? {
        var result:Boolean? = null
        try {
            transaction {
                IsInTeam.select {
                    IsInTeam.player eq targetPlayerUuid.toString()
                }.single().also { result = BoolConvert.intToBool(it[IsInTeam.isInTeam]) }
            }
        } catch (e: NoSuchElementException) {
            plugin.logger.warning("[DataBase] 해당 조건에 만족하는 데이터가 없습니다.")
        }
        return result
    }

    fun setIsInTeamWithUuid(targetPlayerUuid: UUID, isTargetPlayerInTeam: Boolean) {
        transaction {
            try {
                IsInTeam.select {
                    IsInTeam.player eq targetPlayerUuid.toString()
                }.single()
                IsInTeam.update({IsInTeam.player eq targetPlayerUuid.toString()}) {
                    it[isInTeam] = BoolConvert.boolToInt(isTargetPlayerInTeam)
                }
            } catch (e: NoSuchElementException) {
                IsInTeam.insert {
                    it[player] = targetPlayerUuid.toString()
                    it[isInTeam] = BoolConvert.boolToInt(isTargetPlayerInTeam)
                }
            }
        }
        CustomSMPPlugin.isInTeam[targetPlayerUuid] = isTargetPlayerInTeam
        println(CustomSMPPlugin.isInTeam[targetPlayerUuid])
    }


    // Returns Player's Survival Life. If there is no data with target player, returns -1
    fun getPlayerTeamWithUuid(targetPlayerUuid: UUID): UUID? {
        var result: UUID? = null
        transaction {
            try {
                PlayerTeam.select {
                    PlayerTeam.player eq targetPlayerUuid.toString()
                }.single().also { result = UUID.fromString(it[PlayerTeam.team]) }
            } catch (e: NoSuchElementException) {
                plugin.logger.warning("[DataBase] 해당 조건에 만족하는 데이터가 없습니다.")
            }
        }
        return result
    }

    fun setPlayerTeamWithUuid(targetPlayerUuid: UUID, targetPlayerTeam: UUID) {
        transaction {
            try {
                PlayerTeam.select {
                    PlayerTeam.player eq targetPlayerUuid.toString()
                }.single()
                PlayerTeam.update({ PlayerTeam.player eq targetPlayerUuid.toString() }) {
                    it[team] = targetPlayerTeam.toString()
                }
            } catch (e: NoSuchElementException) {
                PlayerTeam.insert {
                    it[player] = targetPlayerUuid.toString()
                    it[team] = targetPlayerTeam.toString()
                }
            }
        }
        CustomSMPPlugin.playerTeam[targetPlayerUuid] = targetPlayerTeam
        println(CustomSMPPlugin.playerTeam[targetPlayerUuid])
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
        var result = emptyList<UUID>()
        try {
            transaction {
                TeamsMember.select {
                    TeamsMember.uuid eq targetTeamUuid.toString()
                }.single().also { query ->
                    query[TeamsMember.members].split(", ").forEach {
                        if (it.trim() == "") {
                            return@transaction
                        }
                        result = result.plus(UUID.fromString(it))
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
                    TeamsMember.uuid eq targetTeamUuid.toString()
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
        var result = emptyList<UUID>()
        try {
            transaction {
                TeamsUuid.selectAll().also { queries ->
                    run {
                        queries.forEach { query ->
                            run {
                                result = result.plus(UUID.fromString(query[uuid]))
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
            TeamsUuid.insert {
                it[uuid] = targetTeamUuid.toString()
            }
        }
        CustomSMPPlugin.teamsUuid = CustomSMPPlugin.teamsUuid.plus(targetTeamUuid)
    }

    fun removeTeamWithUuid(targetTeamUuid: UUID) {
        transaction {
            TeamsName.deleteWhere {
                TeamsName.uuid eq targetTeamUuid.toString()
            }
            TeamsMember.deleteWhere {
                TeamsMember.uuid eq targetTeamUuid.toString()
            }
            TeamsUuid.deleteWhere {
                TeamsUuid.uuid eq targetTeamUuid.toString()
            }
        }
        CustomSMPPlugin.teamsName.remove(targetTeamUuid)
        CustomSMPPlugin.teamsMember.remove(targetTeamUuid)
        CustomSMPPlugin.teamsUuid.minus(targetTeamUuid)
    }
}
