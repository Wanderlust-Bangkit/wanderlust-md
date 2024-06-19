package com.dicoding.wanderlust.ui.destination

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.wanderlust.R
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

        setupToolbar()
        setupRecyclerView()
        setupObservers()
        setupSearch()

        binding.searchView.requestFocus()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun setupRecyclerView() {
        destinationAdapter = DestinationAdapter { dataItem ->
            onDataItemClicked(dataItem)
        }
        binding.rvDestination.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = destinationAdapter
            visibility = View.GONE
        }
        binding.tvNoResults.visibility = View.GONE
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
                        binding.rvDestination.visibility = if (it.isEmpty()) View.GONE else View.VISIBLE
                        binding.tvNoResults.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
                    }
                }
                is ResultState.Error -> {
                    showLoading(false)
                    Log.e("DestinationActivity", "Error loading data: ${result.error}")
                    binding.rvDestination.visibility = View.GONE
                    binding.tvNoResults.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun setupSearch() {
        val searchView = findViewById<SearchView>(R.id.searchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    viewModel.searchDestinations(it.toLowerCase(Locale.ROOT))
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    if (it.isNotEmpty()) {
                        viewModel.searchDestinations(it.toLowerCase(Locale.ROOT))
                    } else {
                        destinationAdapter.submitList(emptyList())
                        binding.rvDestination.visibility = View.GONE
                        binding.tvNoResults.visibility = View.GONE
                    }
                }
                return true
            }
        })

        val closeButton = searchView.findViewById<ImageView>(androidx.appcompat.R.id.search_close_btn)
        closeButton.setOnClickListener {
            searchView.setQuery("", false)
            destinationAdapter.submitList(emptyList())
            binding.rvDestination.visibility = View.GONE
            binding.tvNoResults.visibility = View.GONE
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


