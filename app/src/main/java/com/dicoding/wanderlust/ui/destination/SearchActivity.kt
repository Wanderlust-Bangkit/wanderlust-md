package com.dicoding.wanderlust.ui.destination

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.wanderlust.data.ResultState
import com.dicoding.wanderlust.databinding.ActivitySearchBinding
import com.dicoding.wanderlust.remote.response.DataItem
import com.dicoding.wanderlust.ui.ViewModelFactory
import com.dicoding.wanderlust.ui.adapter.DestinationAdapter
import java.util.Locale

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private lateinit var destinationAdapter: DestinationAdapter

    private val viewModel by viewModels<DestinationViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupObservers()
        setupSearch()

        viewModel.getDestinations()
    }

    private fun setupRecyclerView() {
        destinationAdapter = DestinationAdapter { dataItem ->
            onDataItemClicked(dataItem)
        }
        binding.rvDestination.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = destinationAdapter
        }
    }

    private fun setupObservers() {
        viewModel.destinationList.observe(this) { result ->
            when (result) {
                is ResultState.Loading -> {
                    showLoading(true)
                }
                is ResultState.Success -> {
                    showLoading(false)
                    result.data.let {
                        destinationAdapter.submitList(it)
                        binding.tvNoResults.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
                    }
                }
                is ResultState.Error -> {
                    showLoading(false)
                    Log.e("DestinationActivity", "Error loading data: ${result.error}")
                    binding.tvNoResults.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun setupSearch() {
        binding.searchBar.addTextChangedListener { text ->
            val keyword = text.toString().toLowerCase(Locale.ROOT)
            if (keyword.isNotEmpty()) {
                viewModel.searchDestinations(keyword)
            } else {
                viewModel.getDestinations()
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    private fun onDataItemClicked(dataItem: DataItem) {
        val intent = Intent(this, DestinationDetailActivity::class.java).apply {
            putExtra(DestinationDetailActivity.EXTRA_DESTINATION, dataItem)
        }
        startActivity(intent)
    }
}
