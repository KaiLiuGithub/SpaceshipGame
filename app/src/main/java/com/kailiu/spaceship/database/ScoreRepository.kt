package com.kailiu.spaceship.database

import android.content.Context
import androidx.lifecycle.LiveData
import kotlin.concurrent.thread

class ScoreRepository(context: Context) {
    protected val database by lazy {
        createDatabase(context)
    }
    
    fun createDatabase(context: Context): ScoreDatabase {
        ScoreDatabase.sDatabase?.let {
            return ScoreDatabase.sDatabase!!
        } ?: run {
            return ScoreDatabase.createScoreDB(context)
        }
    }


    fun getScores(): LiveData<List<Score>> {
        return database.getScores()
    }

    fun addScore(score: Score) {
        thread {
            database.addScore(score)
        }
    }

    fun deleteScore(score: Score) {
        thread {
            database.deleteScore(score)
        }
    }
}