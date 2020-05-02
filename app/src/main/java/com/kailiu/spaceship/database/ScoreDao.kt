package com.kailiu.spaceship.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
abstract class ScoreDao {
    @Query("SELECT * FROM scores")
    abstract fun getScores(): LiveData<List<Score>>

    @Insert
    abstract fun addScore(score: Score)

    @Delete
    abstract fun deleteScore(score: Score)
}