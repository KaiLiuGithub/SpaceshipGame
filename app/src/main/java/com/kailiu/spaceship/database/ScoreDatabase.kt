package com.kailiu.spaceship.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlin.concurrent.thread


@Database(
    entities = [Score::class],
    version = 1,
    exportSchema = true)
abstract class ScoreDatabase: RoomDatabase() {
    companion object {
        val DB_Name = "scoreDatabase.db"

        var sDatabase: ScoreDatabase? = null

        fun createScoreDB(context: Context): ScoreDatabase {

            val database = Room.databaseBuilder(context, ScoreDatabase::class.java, DB_Name)
                .fallbackToDestructiveMigration()
                .build()

            sDatabase = database
            return database

        }
    }

    abstract fun scoreDao(): ScoreDao

    fun getScores(): LiveData<List<Score>> {
        return scoreDao().getScores()
    }

    fun addScore(score: Score) {
        scoreDao().addScore(score)
    }

    fun deleteScore(score: Score) {
        scoreDao().deleteScore(score)
    }
}