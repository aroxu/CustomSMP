package me.aroxu.customsmp.database.objects

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object TeamsMember : Table() {
    val uuid: Column<String> = text("uuid").uniqueIndex()
    val members: Column<String> = text("members")
    override val primaryKey = PrimaryKey(uuid)
}
