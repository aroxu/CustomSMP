package me.aroxu.customsmp.utils

import org.bukkit.attribute.Attribute
import org.bukkit.entity.LivingEntity

object BetterMaxHealth {
    fun getMaxHealth(target: LivingEntity): Double {
        return target.getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.baseValue
    }
    fun setMaxHealth(target: LivingEntity, maxHealth: Double) {
        target.getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.baseValue = maxHealth
    }
}
