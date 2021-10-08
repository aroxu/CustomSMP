package me.aroxu.customsmp.database

import me.aroxu.customsmp.CustomSMPPlugin
import me.aroxu.customsmp.CustomSMPPlugin.Companion.plugin
import me.aroxu.customsmp.database.objects.*
import me.aroxu.customsmp.database.objects.InvincibleTeams.team
import me.aroxu.customsmp.database.objects.RegionsName.name
import me.aroxu.customsmp.database.objects.TeamsRegion.region
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
                exec(
                    """
                    CREATE TABLE "TeamsRegion" (
                        "uuid"    TEXT NOT NULL UNIQUE,
                        "region"    TEXT NOT NULL,
                        PRIMARY KEY("uuid")
                    );
                    """
                )
                exec(
                    """
                    CREATE TABLE "RegionsPos" (
                        "name"    TEXT NOT NULL UNIQUE,
                        "pos"    TEXT NOT NULL,
                        PRIMARY KEY("name")
                    );
                    """
                )
                exec(
                    """
                    CREATE TABLE "RegionsName" (
                        "name"    TEXT NOT NULL UNIQUE,
                        PRIMARY KEY("name")
                    );
                    """
                )
                exec(
                    """
                    CREATE TABLE "InvincibleTeams" (
                        "team"    TEXT NOT NULL UNIQUE,
                        PRIMARY KEY("team")
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
                WarLife.update({ WarLife.player eq targetPlayerUuid.toString() }) {
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
                IsInWar.update({ IsInWar.player eq targetPlayerUuid.toString() }) {
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
        var result: Boolean? = null
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
                IsInTeam.update({ IsInTeam.player eq targetPlayerUuid.toString() }) {
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
                TeamsName.update({ TeamsName.uuid eq targetTeamUuid.toString() }) {
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
                    if (query[TeamsMember.members] == "") {
                        return@transaction
                    }
                    query[TeamsMember.members].split("|").forEach {
                        result = result.plus(UUID.fromString(it))
                    }
                }
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
                TeamsMember.update({ TeamsMember.uuid eq targetTeamUuid.toString() }) {
                    it[members] = targetTeamMembers.joinToString("|")
                }
            } catch (e: NoSuchElementException) {
                TeamsMember.insert {
                    it[uuid] = targetTeamUuid.toString()
                    it[members] = targetTeamMembers.joinToString("|")
                }
            }
        }
        CustomSMPPlugin.teamsMember[targetTeamUuid] = targetTeamMembers
    }


    // Get Team RegionsPos name with Team's UUID. returns String
    fun getTeamRegionsNameWithUuid(targetTeamUuid: UUID): List<String> {
        var result = emptyList<String>()
        try {
            transaction {
                TeamsRegion.select {
                    TeamsRegion.uuid eq targetTeamUuid.toString()
                }.single().also {
                    it[TeamsRegion.region].split("|").forEach { regionName ->
                        result = result.plus(regionName)
                    }
                }
            }
        } catch (e: NoSuchElementException) {
            plugin.logger.warning("[DataBase] 해당 조건에 만족하는 데이터가 없습니다.")
        }
        return result
    }

    fun setTeamRegionsNameWithUuid(targetTeamUuid: UUID, targetTeamRegion: List<String>) {
        transaction {
            try {
                TeamsRegion.select {
                    TeamsRegion.uuid eq targetTeamUuid.toString()
                }.single()
                TeamsRegion.update({ TeamsRegion.uuid eq targetTeamUuid.toString() }) {
                    it[region] = targetTeamRegion.joinToString("|")
                }
            } catch (e: NoSuchElementException) {
                TeamsRegion.insert {
                    it[uuid] = targetTeamUuid.toString()
                    it[region] = targetTeamRegion.joinToString("|")
                }
            }
        }
        CustomSMPPlugin.teamsRegion[targetTeamUuid] = targetTeamRegion
    }


    // Get Team RegionsPos's Pos data with RegionsPos's name. returns List<Int>
    fun getRegionPosDataWithName(targetRegionName: String): List<Double> {
        var result = emptyList<Double>()
        try {
            transaction {
                RegionsPos.select {
                    RegionsPos.name eq targetRegionName
                }.single().also {
                    it[RegionsPos.pos].split("|").forEach { pos ->
                        result = result.plus(pos.toDouble())
                    }
                }
            }
        } catch (e: NoSuchElementException) {
            plugin.logger.warning("[DataBase] 해당 조건에 만족하는 데이터가 없습니다.")
        }
        return result
    }

    fun setRegionPosDataWithName(targetRegionName: String, targetRegionPos: List<Double>) {
        transaction {
            try {
                RegionsPos.select {
                    RegionsPos.name eq targetRegionName
                }.single()
                RegionsPos.update({ RegionsPos.name eq targetRegionName }) {
                    it[pos] = targetRegionPos.joinToString("|")
                }
            } catch (e: NoSuchElementException) {
                RegionsPos.insert {
                    it[name] = targetRegionName
                    it[pos] = targetRegionPos.joinToString("|")
                }
            }
        }
        CustomSMPPlugin.regionsPos[targetRegionName] = targetRegionPos
    }


    // Get List of all Region's name. returns List<String>
    fun getAllRegionNames(): List<String> {
        var result = emptyList<String>()
        try {
            transaction {
                RegionsName.selectAll().also { queries ->
                    run {
                        queries.forEach { query ->
                            run {
                                result = result.plus(query[name].split("|"))
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

    fun addToRegionNames(targetRegionName: String) {
        transaction {
            RegionsName.insert {
                it[name] = targetRegionName
            }
        }
        CustomSMPPlugin.regionsName = CustomSMPPlugin.regionsName.plus(targetRegionName)
    }

    fun removeRegionWithName(targetRegionName: String) {
        transaction {
            RegionsName.deleteWhere {
                RegionsName.name eq targetRegionName
            }
            RegionsPos.deleteWhere {
                RegionsPos.name eq targetRegionName
            }
        }
        CustomSMPPlugin.regionsName = CustomSMPPlugin.regionsName.minus(targetRegionName)
        CustomSMPPlugin.regionsPos.remove(targetRegionName)
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


    // Get List of all invincible team's UUID. returns List<UUID>
    fun getAllInvinsibleTeamUuids(): List<UUID> {
        var result = emptyList<UUID>()
        try {
            transaction {
                InvincibleTeams.selectAll().also { queries ->
                    run {
                        queries.forEach { query ->
                            run {
                                result = result.plus(UUID.fromString(query[team]))
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

    fun addToInvincibleTeamUuids(targetTeamUuid: UUID) {
        transaction {
            InvincibleTeams.insert {
                it[team] = targetTeamUuid.toString()
            }
        }
        CustomSMPPlugin.invincibleTeams = CustomSMPPlugin.invincibleTeams.plus(targetTeamUuid)
    }

    fun removeFromInvincibleTeamUuids(targetTeamUuid: UUID) {
        transaction {
            InvincibleTeams.deleteWhere {
                team eq targetTeamUuid.toString()
            }
        }
        CustomSMPPlugin.invincibleTeams = CustomSMPPlugin.invincibleTeams.minus(targetTeamUuid)
    }
}
