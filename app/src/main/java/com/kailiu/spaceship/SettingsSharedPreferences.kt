package com.kailiu.spaceship

import android.content.Context
import android.content.SharedPreferences


class SettingsSharedPreferences(context: Context) {
    private var sharedPreferences: SharedPreferences = context.getSharedPreferences("settings_shared_preference", Context.MODE_PRIVATE)

    companion object {
        private const val SOUND_EFFECTS = "sound_effect"
        private const val MUSIC_VOLUME = "music"
    }

    fun setSoundEffects(isOn: Boolean) {
        sharedPreferences.edit().putBoolean(SOUND_EFFECTS, isOn).apply()
    }

    fun getSoundEffects(): Boolean {
        return sharedPreferences.getBoolean(SOUND_EFFECTS, true)
    }
    
    fun setMusicVolume(volume: Float) {
        sharedPreferences.edit().putFloat(MUSIC_VOLUME, volume).apply()
    }

    fun getMusicVolume(): Float {
        return sharedPreferences.getFloat(MUSIC_VOLUME, 100f)
    }
    
}