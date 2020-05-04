package com.kailiu.spaceship.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.kailiu.spaceship.*
import kotlinx.android.synthetic.main.fragment_main.*
import javax.inject.Inject


class MainFragment: Fragment() {

    private lateinit var navController: NavController

    @Inject
    lateinit var settingsSharedPreferences: SettingsSharedPreferences

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        (activity?.applicationContext as SpaceshipApp).appComponent.inject(this)

        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)

        mainTitle.animateTextColor()

//        val animationDrawable = startBtn.background as AnimationDrawable
  //      animationDrawable.start()

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
                navController.navigate(R.id.action_mainFragment_to_leaderboardFramgent)
            }
        })
    }
}