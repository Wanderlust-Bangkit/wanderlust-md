package com.dicoding.wanderlust.ui.itinerary

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
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

        val itineraryItem = intent.getParcelableExtra<ItineraryItem>(ItineraryDetailActivity.EXTRA_ITINERARY)
        if (itineraryItem != null) {
            currentDataItem = itineraryItem
            showDetail(itineraryItem)
        }
    }

    private fun showDetail(dataItem: ItineraryItem) {
        binding.apply {
            toolbar.title = dataItem.nameItinerary ?: getString(R.string.title_itinerary)
            rvItineraryDay.adapter = ItineraryDayAdapter(dataItem.plan ?: emptyList())
        }
    }

    companion object {
        const val EXTRA_ITINERARY = "extra_itinerary"
    }
}