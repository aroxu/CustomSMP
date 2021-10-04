package me.aroxu.customsmp.database.objects

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object PlayerTeam : Table() {
    val player: Column<String> = text("player").uniqueIndex()
    val team: Column<String> = text("team")
    override val primaryKey = PrimaryKey(player)
}
