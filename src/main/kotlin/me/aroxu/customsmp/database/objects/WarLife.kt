package me.aroxu.customsmp.database.objects

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object WarLife : Table() {
    val player: Column<String> = text("player").uniqueIndex()
    val life: Column<Int> = integer("life")
    override val primaryKey = PrimaryKey(player)
}
