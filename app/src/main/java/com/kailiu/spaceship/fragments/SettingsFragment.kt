package com.kailiu.spaceship.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.kailiu.spaceship.R
import com.kailiu.spaceship.SettingsSharedPreferences
import com.kailiu.spaceship.SpaceshipApp
import kotlinx.android.synthetic.main.fragment_settings.*
import javax.inject.Inject

class SettingsFragment: Fragment() {

    @Inject
    lateinit var settingsSharedPreferences: SettingsSharedPreferences

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        (activity?.applicationContext as SpaceshipApp).appComponent.inject(this)


        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        volumeSeekbar.progress = settingsSharedPreferences.getMusicVolume().toInt()

        soundBtn.text = if (settingsSharedPreferences.getSoundEffects()) "on" else "off"

        soundBtn.setOnClickListener {
            soundBtn.text = if (soundBtn.text == "on") "off" else "on"
            settingsSharedPreferences.setSoundEffects(soundBtn.text == "on")
        }
    }

    override fun onStop() {
        super.onStop()

        settingsSharedPreferences.setMusicVolume(volumeSeekbar.progress.toFloat())

    }
}