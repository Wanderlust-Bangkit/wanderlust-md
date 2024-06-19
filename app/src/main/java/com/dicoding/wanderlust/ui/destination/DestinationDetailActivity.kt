package com.dicoding.wanderlust.ui.destination

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.wanderlust.R
import com.dicoding.wanderlust.databinding.ActivityDestinationDetailBinding
import com.dicoding.wanderlust.remote.response.DataItem
import com.bumptech.glide.Glide

class DestinationDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDestinationDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDestinationDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dataItem = intent.getParcelableExtra<DataItem>(EXTRA_DESTINATION)
        if (dataItem != null) {
            showDetail(dataItem)
        }
    }

    private fun showDetail(dataItem: DataItem) {
        binding.apply {
            tvPlaceName.text = dataItem.placeName
            tvRating.text = dataItem.rating.toString()
            tvDescription.text = dataItem.description
            tvLocation.text = dataItem.location
            tvCity.text = dataItem.city
            tvOpenHour.text = dataItem.openHour
            tvCategory.text = dataItem.category

//            Glide.with(this@DestinationDetailActivity)
//                .load(dataItem.photoUrl)
//                .into(ivDetailPhoto)
        }
    }

    companion object {
        const val EXTRA_DESTINATION = "extra_destination"
    }
}
