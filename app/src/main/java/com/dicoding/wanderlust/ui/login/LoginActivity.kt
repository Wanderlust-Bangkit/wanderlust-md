package com.dicoding.wanderlust.ui.login

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.wanderlust.R
import com.dicoding.wanderlust.data.ResultState
import com.dicoding.wanderlust.data.model.UserModel
import com.dicoding.wanderlust.databinding.ActivityLoginBinding
import com.dicoding.wanderlust.ui.ViewModelFactory
import com.dicoding.wanderlust.ui.main.MainActivity
import com.dicoding.wanderlust.ui.register.RegisterActivity

class LoginActivity : AppCompatActivity() {

    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupAction() {
        binding.textViewRegister.setOnClickListener {
            navigateToRegister()
        }

        binding.buttonLogin.setOnClickListener {
            val email = binding.editTextEmail.text.toString()
            val password = binding.editTextPassword.text.toString()

            if (validateForm(email, password)) {
                viewModel.login(email, password)

                viewModel.loginResult.observe(this) { resultState ->
                    when (resultState) {
                        is ResultState.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                        }
                        is ResultState.Error -> {
                            binding.progressBar.visibility = View.GONE
                            val error = resultState.error
                            showToast(error)
                        }
                        is ResultState.Success -> {
                            binding.progressBar.visibility = View.GONE
                            val sessionData = resultState.data
                            viewModel.saveSession(
                                UserModel(
                                    sessionData.loginResult?.name ?: "",
                                    sessionData.loginResult?.token ?: "",
                                    true
                                )
                            )
                            Log.d("LoginActivity", "Token: ${sessionData.loginResult?.token}")
                            navigateToHome()
                        }
                    }
                }

            }
        }
    }

    private fun validateForm(email: String, password: String): Boolean {
        var isValid = true


        if (email.isEmpty()) {
            binding.textInputLayoutEmail.error = getString(R.string.error_email_required)
            isValid = false
        } else {
            binding.textInputLayoutEmail.error = null
        }

        if (password.isEmpty()) {
            binding.textInputLayoutPassword.error = getString(R.string.error_password_required)
            isValid = false
        } else {
            binding.textInputLayoutPassword.error = null
        }

        return isValid
    }

    private fun navigateToHome() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    private fun navigateToRegister() {
        val intent = Intent(this, RegisterActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
