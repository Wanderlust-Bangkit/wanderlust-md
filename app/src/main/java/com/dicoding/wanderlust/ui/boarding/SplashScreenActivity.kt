package com.dicoding.wanderlust.ui.boarding

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.wanderlust.R
import com.dicoding.wanderlust.ui.boarding.BoardingActivity
import com.dicoding.wanderlust.ui.main.MainActivity
import kotlinx.coroutines.*

class SplashScreenActivity : AppCompatActivity() {

    private val activityScope = CoroutineScope(Dispatchers.Main)

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        sharedPreferences = getSharedPreferences("com.dicoding.wanderlust.PREFERENCE_FILE_KEY", Context.MODE_PRIVATE)

        activityScope.launch {
            delay(2000) // Splash screen delay
            checkFirstLaunch()
        }
    }

    private fun checkFirstLaunch() {
        val isFirstLaunch = sharedPreferences.getBoolean("isFirstLaunch", true)

        if (isFirstLaunch) {
            // First time launch, go to BoardingActivity
            sharedPreferences.edit().putBoolean("isFirstLaunch", false).apply()
            navigateToBoarding()
        } else {
            // Not the first time launch, go to MainActivity
            navigateToMain()
        }
    }

    private fun navigateToBoarding() {
        val intent = Intent(this@SplashScreenActivity, BoardingActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToMain() {
        val intent = Intent(this@SplashScreenActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        activityScope.cancel() // Cancel coroutine scope to avoid memory leaks
        super.onDestroy()
    }
}
