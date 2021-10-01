package me.aroxu.customsmp.database.objects

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object TeamsUuid : Table() {
    val uuid: Column<String> = text("uuid").uniqueIndex()
}
