package com.dicoding.wanderlust.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.wanderlust.R
import com.dicoding.wanderlust.data.ResultState
import com.dicoding.wanderlust.databinding.FragmentHomeBinding
import com.dicoding.wanderlust.remote.response.DataItem
import com.dicoding.wanderlust.ui.ViewModelFactory
import com.dicoding.wanderlust.ui.adapter.DestinationAdapter
import com.dicoding.wanderlust.ui.destination.DestinationCategoryActivity
import com.dicoding.wanderlust.ui.destination.DestinationDetailActivity
import com.dicoding.wanderlust.ui.destination.SearchActivity

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val homeViewModel: HomeViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }
    private lateinit var destinationAdapter: DestinationAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupSearchBar()
        setupCategoryClickListeners()
        setupRecyclerView()
        observeViewModel()

        return root
    }

    private fun setupSearchBar() {
        binding.searchBar.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                val intent = Intent(requireContext(), SearchActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun setupCategoryClickListeners() {
        binding.categoryContainer1.setOnClickListener {
            openCategoryActivity("Budaya")
        }
        binding.categoryContainer2.setOnClickListener {
            openCategoryActivity("Taman Hiburan")
        }
        binding.categoryContainer3.setOnClickListener {
            openCategoryActivity("Cagar Alam")
        }
        binding.categoryContainer4.setOnClickListener {
            openCategoryActivity("Bahari")
        }
        binding.categoryContainer5.setOnClickListener {
            openCategoryActivity("Pusat Perbelanjaan")
        }
        binding.categoryContainer6.setOnClickListener {
            openCategoryActivity("Tempat Ibadah")
        }
    }

    private fun openCategoryActivity(category: String) {
        val intent = Intent(requireContext(), DestinationCategoryActivity::class.java).apply {
            putExtra(DestinationCategoryActivity.EXTRA_CATEGORY, category)
        }
        startActivity(intent)
    }

    private fun setupRecyclerView() {
        destinationAdapter = DestinationAdapter { dataItem ->
            onDataItemClicked(dataItem)
        }
        binding.rvTopRatedDestinations.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = destinationAdapter
        }
    }

    private fun observeViewModel() {
        homeViewModel.topRatedDestinations.observe(viewLifecycleOwner) { resultState ->
            when (resultState) {
                is ResultState.Loading -> {
                    // Nothing to do
                }
                is ResultState.Success -> {
                    destinationAdapter.submitList(resultState.data)
                }
                is ResultState.Error -> {
                    Log.e("DestinationActivity", "Error loading data: ${resultState.error}")
                }
            }
        }

        homeViewModel.userName.observe(viewLifecycleOwner) { userName ->
            binding.tvWelcome.text = getString(R.string.halo_nama, userName)
        }
    }

    private fun onDataItemClicked(dataItem: DataItem) {
        val intent = Intent(requireContext(), DestinationDetailActivity::class.java).apply {
            putExtra(DestinationDetailActivity.EXTRA_DESTINATION, dataItem)
        }
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}





