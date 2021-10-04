package me.aroxu.customsmp.database.objects

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object IsInTeam: Table() {
    val player: Column<String> = text("player").uniqueIndex()
    val isInTeam: Column<Int> = integer("isinteam")
    override val primaryKey = PrimaryKey(player)
}
