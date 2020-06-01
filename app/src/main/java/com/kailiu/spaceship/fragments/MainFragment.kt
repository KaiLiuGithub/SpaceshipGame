package com.kailiu.spaceship.fragments

import android.content.Intent
import android.media.AudioManager
import android.media.SoundPool
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kailiu.spaceship.*
import kotlinx.android.synthetic.main.fragment_main.*
import javax.inject.Inject
import kotlin.math.abs


class MainFragment: Fragment() {

    private lateinit var navController: NavController

    private var soundPool: SoundPool? = null

    private var menuSound: Int = 0

    private var playing = false
    private var startX = -1

    @Inject
    lateinit var settingsSharedPreferences: SettingsSharedPreferences

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        (activity?.applicationContext as SpaceshipApp).appComponent.inject(this)

        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)

        soundPool = SoundPool(1, AudioManager.STREAM_MUSIC, 0)
        menuSound = soundPool!!.load(context, R.raw.menu, 1)

        val soundList = mutableListOf<Boolean>()
        var i = 0
        while (i in 0 until menuList.childCount) {
            soundList.add(false)
            i++
        }

        mainTitle.animateTextColor()

        menuList.setOnTouchListener { _, event ->
            val size = 4
            when (event.action) {
                MotionEvent.ACTION_MOVE -> {
                    val err = paddingStart.width / 2
                    val check1 = menuList.scrollX / paddingStart.width * paddingStart.width
                    val check2 = (menuList.scrollX / paddingStart.width + 1) * paddingStart.width
                    if ((check1 > startX && check1 < menuList.scrollX) || (check2 < startX && check2 > menuList.scrollX)) {
                        soundPool!!.play(menuSound, 1f, 1f, 0, 0, 1f)
                        startX = menuList.scrollX
                    }
                }
                MotionEvent.ACTION_DOWN -> {}
                MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {}
            }
            false
        }


        startBtn.setOnTouchListener(object: OnTouchAndClickListener() {
            override fun setPressed(v: View, status: Boolean) {
               // if (status) startBack.startAnimateBorderColor() else startBack.stopAnimateBorderColor()
            }

            override fun onClick(v: View) {
                val myIntent = Intent(context, GameActivity::class.java)
                this@MainFragment.startActivity(myIntent)
            }
        })

        settingsBtn.setOnTouchListener(object: OnTouchAndClickListener() {
            override fun setPressed(v: View, status: Boolean) {
                //if (status) settingsBack.startAnimateBorderColor() else settingsBack.stopAnimateBorderColor()
            }

            override fun onClick(v: View) {
                navController.navigate(R.id.action_mainFragment_to_settingsFragment)
            }
        })

        leaderBtn.setOnTouchListener(object: OnTouchAndClickListener() {
            override fun setPressed(v: View, status: Boolean) {
                //if (status) leaderBack.startAnimateBorderColor() else leaderBack.stopAnimateBorderColor()
            }

            override fun onClick(v: View) {
                navController.navigate(R.id.action_mainFragment_to_leaderboardFragment)
            }
        })

        helpBtn.setOnClickListener {
            navController.navigate(R.id.action_mainFragment_to_helpFragment)
        }

        profileBtn.setOnClickListener {
            navController.navigate(R.id.action_mainFragment_to_loginFragment)
        }
    }

    abstract class MenuScrollListener : RecyclerView.OnScrollListener() {
        private var mScrolledDistance = 0
        private var mControlsVisible = true
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val firstVisibleItem =
                (recyclerView.layoutManager as LinearLayoutManager?)!!.findFirstVisibleItemPosition()
            if (firstVisibleItem == 0) {
                if (!mControlsVisible) {
                    onShow()
                    mControlsVisible = true
                }
            } else {
                if (mScrolledDistance > HIDE_THRESHOLD && mControlsVisible) {
                    onHide()
                    mControlsVisible = false
                    mScrolledDistance = 0
                } else if (mScrolledDistance < -HIDE_THRESHOLD && !mControlsVisible) {
                    onShow()
                    mControlsVisible = true
                    mScrolledDistance = 0
                }
            }
            if (mControlsVisible && dy > 0 || !mControlsVisible && dy < 0) {
                mScrolledDistance += dy
            }
        }

        abstract fun onHide()
        abstract fun onShow()

        companion object {
            private const val HIDE_THRESHOLD = 20
        }
    }
}