package me.aroxu.customsmp.database.objects

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object RegionsPos : Table() {
    val name: Column<String> = text("name").uniqueIndex()
    val pos: Column<String> = text("pos")
    override val primaryKey = PrimaryKey(name)
}
