package com.kailiu.spaceship.database

import androidx.room.*
import java.util.*

@Entity(
    tableName = "scores",
    indices = [Index(value = ["index_key"], unique = true)])
@TypeConverters(value = [DateTypeConverter::class])
class Score(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "index_key")
    val indexKey: Int = 0,
    @ColumnInfo(name = "name")
    val name: String = "",
    @ColumnInfo(name = "score")
    val score: Int = 0,
    @ColumnInfo(name = "time")
    val time: Date = Date()
)