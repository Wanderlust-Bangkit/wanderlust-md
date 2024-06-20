package com.dicoding.wanderlust.ui.destination

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.wanderlust.R
import com.dicoding.wanderlust.data.ResultState
import com.dicoding.wanderlust.databinding.ActivityDestinationDetailBinding
import com.dicoding.wanderlust.remote.response.DataItem
import com.dicoding.wanderlust.ui.ViewModelFactory
import java.util.Locale

class DestinationDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDestinationDetailBinding

    private val viewModel by viewModels<DestinationViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private var currentDataItem: DataItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDestinationDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dataItem = intent.getParcelableExtra<DataItem>(EXTRA_DESTINATION)
        if (dataItem != null) {
            currentDataItem = dataItem
            showDetail(dataItem)
            dataItem.id?.let { viewModel.checkIfFavorite(it) } // Panggil fungsi checkIfFavorite()
        }

        binding.fabFavorite.setOnClickListener {
            currentDataItem?.id?.let { destinationId ->
                viewModel.toggleFavorite(destinationId)
            }
        }

        binding.actionBack.setOnClickListener {
            onBackPressed()
        }

        binding.actionShare.setOnClickListener {
            currentDataItem?.let { dataItem ->
                val shareIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text, dataItem.placeName, dataItem.description))
                    type = "text/plain"
                }
                startActivity(Intent.createChooser(shareIntent, getString(R.string.share_title)))
            }
        }

        observeViewModel()
    }

    private fun showDetail(dataItem: DataItem) {
        binding.apply {
            tvPlaceName.text = dataItem.placeName

            val tvRating: TextView = findViewById(R.id.tvRating)
            val formattedRating: String = getString(R.string.rating, String.format(Locale.US, "%.1f", dataItem.rating))
            tvRating.text = formattedRating

            tvDescription.text = dataItem.description
            tvLocation.text = dataItem.location

            val tvOpenHour: TextView = findViewById(R.id.tvOpenHour)
            val formattedText: String = getString(R.string.open_hour, dataItem.openHour)
            tvOpenHour.text = formattedText

            tvCategory.text = dataItem.category
        }
    }

    private fun observeViewModel() {
        viewModel.favoriteResult.observe(this) { result ->
            when (result) {
                is ResultState.Success -> {
                    // Do nothing, as the favorite status is already handled by isFavorite observer
                }

                is ResultState.Error -> {
                    Log.e("Favorite", "Error loading data: ${result.error}")
                }

                is ResultState.Loading -> {
                    // Nothing to do
                }
            }
        }

        viewModel.isFavorite.observe(this) { isFavorite ->
            if (isFavorite) {
                binding.fabFavorite.setImageResource(R.drawable.ic_favorite)
            } else {
                binding.fabFavorite.setImageResource(R.drawable.ic_favorite_border)
            }
        }
    }

    companion object {
        const val EXTRA_DESTINATION = "extra_destination"
    }
}


