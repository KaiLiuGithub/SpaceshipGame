package com.kailiu.spaceship.fragments

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kailiu.spaceship.R
import com.kailiu.spaceship.SpaceshipApp
import com.kailiu.spaceship.database.Score
import com.kailiu.spaceship.database.ScoreRepository
import com.kailiu.spaceship.dialog.LeaderboardDialog
import kotlinx.android.synthetic.main.dialog_leaderboard.*
import javax.inject.Inject

class LeaderboardFragment: Fragment() {

    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    var scoreList = mutableListOf<Score>()

    private lateinit var navController: NavController

    @Inject
    lateinit var scoreRepository: ScoreRepository

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        (activity?.applicationContext as SpaceshipApp).appComponent.inject(this)

        return inflater.inflate(R.layout.dialog_leaderboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)

        viewManager = LinearLayoutManager(context)
        viewAdapter = LeaderboardDialog.ScoreLayoutAdapter(scoreList)

        leaderboardBackground.visibility = View.VISIBLE
        scoreRecyclerView.setBackgroundDrawable(ColorDrawable(Color.parseColor("#44000000")))

        getLocalScores()

        localTab.isSelected = true
        globalTab.isSelected = false

        localTab.setOnClickListener {
            localTab.isSelected = true
            globalTab.isSelected = false
            getLocalScores()
        }

        globalTab.setOnClickListener {
            localTab.isSelected = false
            globalTab.isSelected = true
        }

        scoreRecyclerView.apply {
            setHasFixedSize(false)
            layoutManager = viewManager
            adapter = viewAdapter
        }

        boardBtn.setOnClickListener {
            navController.navigate(R.id.action_leaderboardFragment_to_mainFragment)
        }
    }

    fun getLocalScores() {
        scoreRepository.getScores().observe(viewLifecycleOwner, Observer { scores ->
            scores?.let {
                scoreList.clear()
                scoreList.addAll(scores.sortedWith(compareBy({ it.score }, { it.time }, { it.name })).toMutableList())

                if (scores.size > 50) {
                    scoreRepository.deleteScore(scoreList[0])
                    scoreList.removeAt(0)
                } else if (scores.isEmpty()) {
                    noScores.visibility = View.VISIBLE
                }

                viewAdapter.notifyDataSetChanged()
            }
        })
    }
}