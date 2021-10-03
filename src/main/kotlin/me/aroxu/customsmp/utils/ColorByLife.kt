package me.aroxu.customsmp.utils

import net.kyori.adventure.text.format.TextColor

object ColorByLife {
    val WHITE = TextColor { 0xFFFFFF }
    val CYAN = TextColor { 0x00FFFF }
    val LIME = TextColor { 0x00FF00 }
    val YELLOW = TextColor { 0xFFFF00 }
    val ORANGE = TextColor { 0xFFA500 }
    val RED = TextColor { 0xFF0000 }

    fun getSurvivalColorByLife(life: Int): TextColor {
        var color = RED
        when {
            life > 15 -> {
                color = CYAN
            }
            life > 10 -> {
                color = LIME
            }
            life > 5 -> {
                color = YELLOW
            }
            life > 0 -> {
                color = RED
            }
        }
        return color
    }

    fun getWarColorByLife(life: Int): TextColor {
        var color = RED
        when {
            life > 3 -> {
                color = YELLOW
            }
            life > 2 -> {
                color = ORANGE
            }
            life > 1 -> {
                color = ORANGE
            }
            life > 0 -> {
                color = RED
            }
        }
        return color
    }
}
