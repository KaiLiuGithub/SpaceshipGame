package com.kailiu.spaceship.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.kailiu.spaceship.AuthSharedPreferences
import com.kailiu.spaceship.R
import com.kailiu.spaceship.SettingsSharedPreferences
import com.kailiu.spaceship.SpaceshipApp
import com.kailiu.spaceship.cloud.UserRepository
import com.kailiu.spaceship.cloud.UserService
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_signup.*
import javax.inject.Inject

class LoginFragment: Fragment() {

    @Inject
    lateinit var userRepository: UserRepository

    @Inject
    lateinit var authSharedPreferences: AuthSharedPreferences

    private lateinit var navController: NavController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        (activity?.applicationContext as SpaceshipApp).appComponent.inject(this)

        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)

        loginBtn.setOnClickListener {
            userRepository.login(usernameLoginField.text.toString(), passwordLoginField.text.toString())
        }

        toSignupBtn.setOnClickListener {
            navController.navigate(R.id.action_loginFragment_to_signupFragment)
        }
    }
}