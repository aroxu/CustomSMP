package me.aroxu.customsmp.handler

import me.aroxu.customsmp.CustomSMPPlugin
import me.aroxu.customsmp.database.DataManager
import org.bukkit.entity.Player

object PlayerDataHandler {
    fun handleData(target: Player) {
        CustomSMPPlugin.isPlayerDataReady[target.uniqueId] = false
        val targetPlayerSurvivalLife = DataManager.getSurvivalLifeWithUuid(target.uniqueId)
        val targetPlayerWarLife = DataManager.getWarLifeWithUuid(target.uniqueId)

        if (targetPlayerSurvivalLife == -1) {
            DataManager.setSurvivalLifeWithUuid(target.uniqueId, 20)
        }
        if (targetPlayerWarLife == -1) {
            DataManager.setWarLifeWithUuid(target.uniqueId, 5)
            DataManager.setIsInWarWithUuid(target.uniqueId, false)
            return handleData(target)
        }
        CustomSMPPlugin.survivalLife[target.uniqueId] = DataManager.getSurvivalLifeWithUuid(target.uniqueId)
        CustomSMPPlugin.warLife[target.uniqueId] = DataManager.getWarLifeWithUuid(target.uniqueId)
        CustomSMPPlugin.isInWar[target.uniqueId] = DataManager.getIsInWarWithUuid(target.uniqueId)
        CustomSMPPlugin.isPlayerDataReady[target.uniqueId] = true
    }
}
