package com.dicoding.wanderlust.ui.itinerary

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.wanderlust.R
import com.dicoding.wanderlust.ui.ViewModelFactory
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddItineraryActivity : AppCompatActivity() {

    private lateinit var editTextName: TextInputEditText
    private lateinit var spinnerLocation: Spinner
    private lateinit var editTextDepartureDate: TextInputEditText
    private lateinit var editTextReturnDate: TextInputEditText
    private lateinit var buttonContinue: Button

    private val calendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.US)
    private var selectedLocation: String? = null

    private val itineraryViewModel: ItineraryViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_itinerary)

        editTextName = findViewById(R.id.editTextName)
        spinnerLocation = findViewById(R.id.spinnerLocation)
        editTextDepartureDate = findViewById(R.id.editTextDepartureDate)
        editTextReturnDate = findViewById(R.id.editTextReturnDate)
        buttonContinue = findViewById(R.id.btn_continue)

        setupLocationSpinner()
        setupDatePickers()

        // Observe userId from viewModel
        itineraryViewModel.userId.observe(this) { userId ->
            userId?.let {
                setupContinueButton(userId)
            } ?: run {
                // Handle case where userId is null
                Toast.makeText(this, "User ID not available", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupContinueButton(userId: String) {
        buttonContinue.setOnClickListener {
            if (validateInput()) {
                val intent = Intent(this, ChooseCategoryActivity::class.java)
                intent.putExtra("name", editTextName.text.toString())
                intent.putExtra("location", selectedLocation)
                intent.putExtra("start_date", editTextDepartureDate.text.toString())
                intent.putExtra("end_date", editTextReturnDate.text.toString())
                intent.putExtra("user_id", userId)

                Log.d("IntentData", "Name: ${editTextName.text.toString()}")
                Log.d("IntentData", "Location: $selectedLocation")
                Log.d("IntentData", "Start Date: ${editTextDepartureDate.text.toString()}")
                Log.d("IntentData", "End Date: ${editTextReturnDate.text.toString()}")
                Log.d("IntentData", "User ID: $userId")

                startActivity(intent)
            } else {
                Toast.makeText(this, "Please complete your travel information", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupLocationSpinner() {
        val locations = arrayOf(
            "Jakarta", "Bogor", "Bandung", "Malang", "Bali",
            "Yogyakarta", "Solo", "Semarang"
        )
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, locations)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerLocation.adapter = adapter

        spinnerLocation.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                selectedLocation = locations[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                selectedLocation = null
            }
        }
    }

    private fun setupDatePickers() {
        editTextDepartureDate.inputType = InputType.TYPE_NULL
        editTextReturnDate.inputType = InputType.TYPE_NULL

        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateLabel(editTextDepartureDate)
        }

        editTextDepartureDate.setOnClickListener {
            DatePickerDialog(this, dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        val returnDateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateLabel(editTextReturnDate)
        }

        editTextReturnDate.setOnClickListener {
            DatePickerDialog(this, returnDateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }
    }

    private fun updateLabel(editText: TextInputEditText) {
        editText.setText(dateFormat.format(calendar.time))
    }

    private fun validateInput(): Boolean {
        return editTextName.text.toString().isNotEmpty() &&
                selectedLocation != null &&
                editTextDepartureDate.text.toString().isNotEmpty() &&
                editTextReturnDate.text.toString().isNotEmpty()
    }
}
