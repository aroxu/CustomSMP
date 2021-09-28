package me.aroxu.customsmp.database.objects

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object IsInWar : Table() {
    val player: Column<String> = text("player").uniqueIndex()
    val isInWar: Column<Int> = integer("isinwar")
    override val primaryKey = PrimaryKey(player)
}
