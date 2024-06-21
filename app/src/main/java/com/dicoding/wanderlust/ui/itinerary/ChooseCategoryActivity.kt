package com.dicoding.wanderlust.ui.itinerary

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.ProgressBar
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.wanderlust.R
import com.dicoding.wanderlust.data.ResultState
import com.dicoding.wanderlust.remote.response.DataItem
import com.dicoding.wanderlust.remote.response.PlanItem
import com.dicoding.wanderlust.ui.ViewModelFactory
import com.dicoding.wanderlust.ui.main.MainActivity
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

class ChooseCategoryActivity : AppCompatActivity() {

    private lateinit var buttonContinue: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var selectedCategories: MutableList<String>

    private val viewModel: ItineraryViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_category)

        selectedCategories = mutableListOf()

        buttonContinue = findViewById(R.id.btn_continue)
        progressBar = findViewById(R.id.progressBar)
        setupCategoryCheckboxes()

        buttonContinue.setOnClickListener {
            val name = intent.getStringExtra("name") ?: return@setOnClickListener
            val location = intent.getStringExtra("location") ?: return@setOnClickListener
            val startDate = intent.getStringExtra("start_date") ?: return@setOnClickListener
            val endDate = intent.getStringExtra("end_date") ?: return@setOnClickListener
            val userId = intent.getStringExtra("user_id") ?: return@setOnClickListener

            val days = calculateDays(startDate, endDate)

            generateItinerary(name, location, startDate, endDate, userId, days)
        }
    }

    fun onBackClicked(view: View) {
        onBackPressed()
    }

    private fun setupCategoryCheckboxes() {
        val categories = listOf(
            R.id.check_budaya to "Budaya",
            R.id.check_taman_hiburan to "Taman Hiburan",
            R.id.check_cagar_alam to "Cagar Alam",
            R.id.check_bahari to "Bahari",
            R.id.check_pusat_perbelanjaan to "Pusat Perbelanjaan",
            R.id.check_tempat_ibadah to "Tempat Ibadah"
        )

        categories.forEach { (checkBoxId, category) ->
            val checkBox = findViewById<CheckBox>(checkBoxId)
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    selectedCategories.add(category)
                } else {
                    selectedCategories.remove(category)
                }
            }
            checkBox.setOnClickListener {
                Log.d("ChooseCategoryActivity", "CheckBox clicked: $category")
            }
        }
    }

    private fun calculateDays(startDate: String, endDate: String): Int {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.US)
        val start = dateFormat.parse(startDate) ?: return 0
        val end = dateFormat.parse(endDate) ?: return 0
        val diff = end.time - start.time
        return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS).toInt() + 1
    }

    private fun generateItinerary(
        name: String,
        location: String,
        startDate: String,
        endDate: String,
        userId: String,
        days: Int
    ) {
        val planItems = mutableListOf<PlanItem>()
        val destinationLists = mutableListOf<List<DataItem>>()

        for (day in 1..days) {
            val category = selectedCategories[day % selectedCategories.size]
            viewModel.generateItinerary(category, location).observe(this) { result ->
                when (result) {
                    is ResultState.Success -> {
                        val destinationResponse = result.data
                        val destinationData = destinationResponse.data
                        if (destinationData != null) {
                            destinationLists.add(destinationData)
                        }
                        if (destinationLists.size == days) {
                            for (i in 0 until days) {
                                planItems.add(PlanItem(destinationLists[i], i + 1))
                            }
                            createItinerary(name, location, startDate, endDate, userId, planItems)
                        }
                    }
                    is ResultState.Error -> {
                        // Handle error or unexpected state
                    }
                    ResultState.Loading -> {
                        // Show loading
                    }
                }
            }
        }
    }


    private fun createItinerary(
        name: String,
        location: String,
        startDate: String,
        endDate: String,
        userId: String,
        plansItems: List<PlanItem>
    ) {
        viewModel.createItinerary(name, location, startDate, endDate, userId, plansItems)
        viewModel.createItineraryResult.observe(this) { result ->
            when (result) {
                is ResultState.Success -> {
                    progressBar.visibility = View.GONE
                    Log.d("Create Itinerary", "Successfully created itinerary")
                    navigateToMainActivity()
                }
                is ResultState.Error -> {
                    progressBar.visibility = View.GONE
                    Log.e("Create Itinerary", "Error creating itinerary: ${result.error}")
                }
                ResultState.Loading -> {
                    progressBar.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("fragment", "ItineraryFragment")
        startActivity(intent)
        finish()
    }

}
