package me.aroxu.customsmp.database.objects

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object TeamsRegion : Table() {
    val uuid: Column<String> = text("uuid").uniqueIndex()
    val region: Column<String> = text("region")
    override val primaryKey = PrimaryKey(uuid)
}
