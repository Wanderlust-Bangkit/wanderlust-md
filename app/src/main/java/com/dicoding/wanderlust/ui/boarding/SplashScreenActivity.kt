package com.dicoding.wanderlust.ui.boarding

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.dicoding.wanderlust.R
import com.dicoding.wanderlust.databinding.ActivitySplashScreenBinding
import com.dicoding.wanderlust.ui.ViewModelFactory
import com.dicoding.wanderlust.ui.main.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

import android.view.animation.AnimationUtils

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding
    private val activityScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Optional: You can set a timeout for how long the splash screen will be shown
        val splashTimeoutMillis: Long = 2000 // 2 seconds

        // Load animation
        val animation = AnimationUtils.loadAnimation(this, R.anim.text_animation)
        binding.textViewAppName.startAnimation(animation)

        activityScope.launch {
            delay(splashTimeoutMillis)
            navigateToMain()
        }
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
