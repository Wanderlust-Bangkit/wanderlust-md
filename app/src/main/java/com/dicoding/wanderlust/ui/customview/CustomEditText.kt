package com.dicoding.wanderlust.ui.customview

import android.content.Context
import android.content.ContextWrapper
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import androidx.appcompat.widget.AppCompatEditText
import com.dicoding.wanderlust.R
import com.dicoding.wanderlust.ui.register.RegisterActivity
import com.google.android.material.textfield.TextInputLayout

class CustomEditText(context: Context, attrs: AttributeSet) : AppCompatEditText(context, attrs) {

    private var textInputLayout: TextInputLayout? = null

    fun setTextInputLayout(layout: TextInputLayout) {
        this.textInputLayout = layout
    }

    init {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                textInputLayout?.let { validateInput(it) }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun validateInput(layout: TextInputLayout) {
        val text = this.text.toString()
        val parentActivity = getParentActivity()

        when (this.id) {
            R.id.editTextEmail -> {
                if (!Patterns.EMAIL_ADDRESS.matcher(text).matches()) {
                    layout.error = context.getString(R.string.error_invalid_email)
                    layout.isErrorEnabled = true
                } else {
                    layout.error = null
                    layout.isErrorEnabled = false
                }
            }
            R.id.editTextPassword -> {
                if (!isValidPassword(text)) {
                    layout.error = context.getString(R.string.error_password_too_short)
                    layout.isErrorEnabled = true
                } else {
                    layout.error = null
                    layout.isErrorEnabled = false
                }
            }
            R.id.editTextConfirmPassword -> {
                val password = parentActivity?.getPassword() ?: ""
                if (text != password) {
                    layout.error = context.getString(R.string.error_passwords_do_not_match)
                    layout.isErrorEnabled = true
                } else {
                    layout.error = null
                    layout.isErrorEnabled = false
                }
            }
        }
    }

    private fun isValidPassword(password: String): Boolean {
        val passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,50}\$"
        return password.matches(passwordPattern.toRegex())
    }

    private fun getParentActivity(): RegisterActivity? {
        var context = context
        while (context is ContextWrapper) {
            if (context is RegisterActivity) {
                return context
            }
            context = context.baseContext
        }
        return null
    }
}


