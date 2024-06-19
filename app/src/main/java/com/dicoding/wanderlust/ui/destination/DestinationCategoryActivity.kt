package com.dicoding.wanderlust.ui.destination

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.wanderlust.data.ResultState
import com.dicoding.wanderlust.databinding.ActivityDestinationCategoryBinding
import com.dicoding.wanderlust.remote.response.DataItem
import com.dicoding.wanderlust.ui.ViewModelFactory
import com.dicoding.wanderlust.ui.adapter.DestinationAdapter

class DestinationCategoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDestinationCategoryBinding
    private lateinit var destinationAdapter: DestinationAdapter

    private val viewModel by viewModels<DestinationViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDestinationCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        setupObservers()

        val category = intent.getStringExtra(EXTRA_CATEGORY)
        category?.let {
            title = it
            viewModel.getDestinationByCategory(it)
        }
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
                    }
                }
                is ResultState.Error -> {
                    showLoading(false)
                    Log.e("DestinationActivity", "Error loading data: ${result.error}")
                }
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

    companion object {
        const val EXTRA_CATEGORY = "extra_category"
    }
}
