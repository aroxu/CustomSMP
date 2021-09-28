package me.aroxu.customsmp.handler

import me.aroxu.customsmp.CustomSMPPlugin
import me.aroxu.customsmp.database.DataManager
import org.bukkit.entity.Player

object PlayerDataHandler {
    fun handleData(target: Player) {
        CustomSMPPlugin.isPlayerDataReady[target.uniqueId] = false
        val targetPlayerSurvivalLife = DataManager.getSurvivalLifeWithUuid(target.uniqueId.toString())
        val targetPlayerWarLife = DataManager.getWarLifeWithUuid(target.uniqueId.toString())

        if (targetPlayerSurvivalLife == -1) {
            DataManager.setSurvivalLifeWithUuid(target.uniqueId.toString(), 20)
        }
        if (targetPlayerWarLife == -1) {
            DataManager.setWarLifeWithUuid(target.uniqueId.toString(), 5)
            DataManager.setIsInWarWithUuid(target.uniqueId.toString(), false)
            return handleData(target)
        }
        CustomSMPPlugin.survivalLife[target.uniqueId] = DataManager.getSurvivalLifeWithUuid(target.uniqueId.toString())
        CustomSMPPlugin.warLife[target.uniqueId] = DataManager.getWarLifeWithUuid(target.uniqueId.toString())
        CustomSMPPlugin.isInWar[target.uniqueId] = DataManager.getIsInWarWithUuid(target.uniqueId.toString())
        CustomSMPPlugin.isPlayerDataReady[target.uniqueId] = true
    }
}
