package com.dicoding.wanderlust.ui.itinerary

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.wanderlust.R
import com.dicoding.wanderlust.databinding.ActivityItineraryDetailBinding
import com.dicoding.wanderlust.remote.response.ItineraryItem
import com.dicoding.wanderlust.ui.ViewModelFactory
import com.dicoding.wanderlust.ui.adapter.ItineraryDayAdapter

class ItineraryDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityItineraryDetailBinding

    private val viewModel by viewModels<ItineraryViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private var currentDataItem: ItineraryItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityItineraryDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()

        val itineraryItem = intent.getParcelableExtra<ItineraryItem>(EXTRA_ITINERARY)
        if (itineraryItem != null) {
            currentDataItem = itineraryItem
            showDetail(itineraryItem)
        } else {
            Log.e(TAG, "No itinerary data found in intent extras.")
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun showDetail(dataItem: ItineraryItem) {
        binding.apply {
            toolbar.title = dataItem.nameItinerary ?: getString(R.string.title_itinerary)
            rvItineraryDay.adapter = ItineraryDayAdapter(dataItem.plan ?: emptyList())
            rvItineraryDay.layoutManager = LinearLayoutManager(this@ItineraryDetailActivity)
        }
        Log.d(TAG, "Showing details for itinerary: ${dataItem.nameItinerary}")
    }

    companion object {
        private const val TAG = "ItineraryDetailActivity"
        const val EXTRA_ITINERARY = "extra_itinerary"
    }
}
