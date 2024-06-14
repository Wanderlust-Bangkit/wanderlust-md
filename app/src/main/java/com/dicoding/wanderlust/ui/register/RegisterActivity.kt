package com.dicoding.wanderlust.ui.register

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.wanderlust.R
import com.dicoding.wanderlust.data.ResultState
import com.dicoding.wanderlust.databinding.ActivityRegisterBinding
import com.dicoding.wanderlust.ui.ViewModelFactory
import com.dicoding.wanderlust.ui.login.LoginActivity

class RegisterActivity : AppCompatActivity() {

    lateinit var binding: ActivityRegisterBinding

    private val viewModel: RegisterViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
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

        binding.editTextEmail.setTextInputLayout(binding.textInputLayoutEmail)
        binding.editTextPassword.setTextInputLayout(binding.textInputLayoutPassword)
        binding.editTextConfirmPassword.setTextInputLayout(binding.textInputLayoutConfirmPassword)
    }

    private fun setupAction() {
        binding.textViewLogin.setOnClickListener {
            navigateToLogin()
        }

        binding.buttonRegister.setOnClickListener {
            val name = binding.editTextName.text.toString()
            val email = binding.editTextEmail.text.toString()
            val password = binding.editTextPassword.text.toString()
            val confirmPassword = binding.editTextConfirmPassword.text.toString()

            if (validateForm(name, email, password, confirmPassword)) {
                viewModel.register(
                    name, email, password
                )

                viewModel.registerResult.observe(this) { resultState ->
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

                            showToast(resultState.data.message.toString())
                            navigateToLogin()
                        }
                    }
                }
            }
        }
    }

    private fun validateForm(name: String, email: String, password: String, confirmPassword: String): Boolean {
        var isValid = true

        if (name.isEmpty()) {
            binding.editTextName.error = getString(R.string.error_name_required)
            isValid = false
        }

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

        if (confirmPassword.isEmpty()) {
            binding.textInputLayoutConfirmPassword.error = getString(R.string.error_confirm_password_required)
            isValid = false
        } else {
            binding.textInputLayoutConfirmPassword.error = null
        }

        return isValid
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    fun getPassword(): String {
        return binding.editTextPassword.text.toString()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}

