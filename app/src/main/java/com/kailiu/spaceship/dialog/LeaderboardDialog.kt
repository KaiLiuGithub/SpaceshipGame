package com.kailiu.spaceship.dialog

import android.app.Activity
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kailiu.spaceship.DialogCloseListener
import com.kailiu.spaceship.R
import com.kailiu.spaceship.SpaceshipApp
import com.kailiu.spaceship.database.Score
import com.kailiu.spaceship.database.ScoreRepository
import kotlinx.android.synthetic.main.dialog_leaderboard.*
import kotlinx.android.synthetic.main.view_score.view.*
import javax.inject.Inject


class LeaderboardDialog(var width: Int, var height: Int, var activity: Activity): DialogFragment() {
    lateinit var closeListener: DialogCloseListener

    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    var scoreList = mutableListOf<Score>()

    @Inject
    lateinit var scoreRepository: ScoreRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (activity.applicationContext as SpaceshipApp).appComponent.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_leaderboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#11FFFFFF")))
        dialog?.setCanceledOnTouchOutside(false)

        leaderboardFrame.minWidth = (width * 0.9).toInt()
        leaderboardFrame.minHeight = (height * 0.9).toInt()

        val ft: FragmentTransaction = requireFragmentManager().beginTransaction()
        val prev: Fragment? = requireFragmentManager().findFragmentByTag("dialog")
        if (prev != null) {
            ft.remove(prev)
        }
        ft.addToBackStack(null)

        viewManager = LinearLayoutManager(context)
        viewAdapter = ScoreLayoutAdapter(scoreList)

        scoreRecyclerView.apply {
            setHasFixedSize(false)
            layoutManager = viewManager
            adapter = viewAdapter
        }

        scoreRepository.getScores().observe(this, Observer { scores ->
            scores?.let {
                scoreList.clear()
                scoreList.addAll(scores.sortedWith(compareBy({ it.score }, { it.time }, { it.name })).toMutableList())

                if (scores.size > 50) {
                    scoreRepository.deleteScore(scoreList[0])
                    scoreList.removeAt(0)
                }

                for (i in scoreList) {
                    println("name: ${i.name}, score: ${i.score}")
                }

                viewAdapter.notifyDataSetChanged()
            }
        })

        boardBtn.setOnClickListener {
            dismiss()
        }
    }

    class ScoreLayoutAdapter(private val myDataset: MutableList<Score>) :
        RecyclerView.Adapter<ScoreLayoutAdapter.MyViewHolder>() {
        class MyViewHolder(val view: LinearLayout) : RecyclerView.ViewHolder(view)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.view_score, parent, false) as LinearLayout
            return MyViewHolder(view)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val index = myDataset.size - position - 1
            holder.view.name.text = myDataset[index].name
            holder.view.score.text = "${myDataset[index].score}"
            holder.view.time.text = myDataset[index].time.toString()
        }

        override fun getItemCount() = myDataset.size
    }

    fun setDismissListener(closeListener: DialogCloseListener) {
        this.closeListener = closeListener
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        closeListener.handleDialogClose(null)
    }
}