package me.aroxu.customsmp.database.objects

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object InvincibleTeams: Table() {
    val team: Column<String> = text("team").uniqueIndex()
    override val primaryKey = PrimaryKey(team)
}
