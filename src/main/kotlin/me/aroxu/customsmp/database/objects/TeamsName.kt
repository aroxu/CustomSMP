package me.aroxu.customsmp.database.objects

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object TeamsName : Table() {
    val uuid: Column<String> = text("uuid").uniqueIndex()
    val name: Column<String> = text("name")
    override val primaryKey = PrimaryKey(uuid)
}
