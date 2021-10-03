package me.aroxu.customsmp.handler

import me.aroxu.customsmp.CustomSMPPlugin
import me.aroxu.customsmp.database.DataManager
import org.bukkit.entity.Player
import java.util.*
import kotlin.collections.HashMap

object DataHandler {
    fun handlePlayerData(target: Player) {
        CustomSMPPlugin.isPlayerDataReady[target.uniqueId] = false
        val targetPlayerSurvivalLife = DataManager.getSurvivalLifeWithUuid(target.uniqueId)
        val targetPlayerWarLife = DataManager.getWarLifeWithUuid(target.uniqueId)
        val isTargetInTeam = DataManager.getIsInTeamWithUuid(target.uniqueId)

        if (targetPlayerSurvivalLife == null) {
            DataManager.setSurvivalLifeWithUuid(target.uniqueId, 20)
        }
        if (targetPlayerWarLife == null) {
            DataManager.setWarLifeWithUuid(target.uniqueId, 5)
            DataManager.setIsInWarWithUuid(target.uniqueId, false)
            return handlePlayerData(target)
        }
        if (isTargetInTeam == null) {
            DataManager.setIsInTeamWithUuid(target.uniqueId, false)
        }

        CustomSMPPlugin.survivalLife[target.uniqueId] = DataManager.getSurvivalLifeWithUuid(target.uniqueId)!!
        println("Loaded Player's Survival Life: ${CustomSMPPlugin.survivalLife[target.uniqueId]}")
        CustomSMPPlugin.warLife[target.uniqueId] = DataManager.getWarLifeWithUuid(target.uniqueId)!!
        println("Loaded Player's War Life: ${CustomSMPPlugin.warLife[target.uniqueId]}")
        CustomSMPPlugin.isInWar[target.uniqueId] = DataManager.getIsInWarWithUuid(target.uniqueId)
        println("Loaded Player's War Status: ${CustomSMPPlugin.isInWar[target.uniqueId]}")
        CustomSMPPlugin.isInTeam[target.uniqueId] = DataManager.getIsInTeamWithUuid(target.uniqueId)!!
        println("Loaded Player's Team Status: ${CustomSMPPlugin.isInTeam[target.uniqueId]}")
        if (DataManager.getIsInTeamWithUuid(target.uniqueId)!!) {
            CustomSMPPlugin.playerTeam[target.uniqueId] = DataManager.getPlayerTeamWithUuid(target.uniqueId)!!
            println("Loaded Player's Team Data: ${CustomSMPPlugin.playerTeam[target.uniqueId]}")
        }
        CustomSMPPlugin.isPlayerDataReady[target.uniqueId] = true
        println("Loaded Player's Data Status: ${CustomSMPPlugin.isPlayerDataReady[target.uniqueId]}")
    }

    fun handleTeamsData() {
        val allTeamsUuid = DataManager.getAllTeamUuids()
        val allTeamsName: HashMap<UUID, String> = HashMap()
        val allTeamsMember: HashMap<UUID, List<UUID>> = HashMap()

        allTeamsUuid.forEach {
            allTeamsName[it] = DataManager.getTeamNameWithUuid(it)
            allTeamsMember[it] = DataManager.getTeamMembersWithUuid(it)
        }

        CustomSMPPlugin.teamsUuid = allTeamsUuid
        println("Loaded Team UUID List: ${CustomSMPPlugin.teamsUuid}")
        CustomSMPPlugin.teamsName = allTeamsName
        println("Loaded Team Name List: ${CustomSMPPlugin.teamsName}")
        CustomSMPPlugin.teamsMember = allTeamsMember
        println("Loaded Team Member List: ${CustomSMPPlugin.teamsMember}")
    }
}
