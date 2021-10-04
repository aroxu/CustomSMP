package me.aroxu.customsmp.database.objects

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object RegionsName : Table() {
    val name: Column<String> = text("name").uniqueIndex()
    override val primaryKey = PrimaryKey(name)
}
