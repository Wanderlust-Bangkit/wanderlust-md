package com.dicoding.wanderlust.ui.home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
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
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val homeViewModel: HomeViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }

    private lateinit var topRatedDestinationAdapter: DestinationAdapter
    private lateinit var nearestDestinationAdapter: DestinationAdapter
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        setupSearchBar()
        setupCategoryClickListeners()
        setupRecyclerView()
        observeViewModel()

        homeViewModel.getDestinations()

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
        topRatedDestinationAdapter = DestinationAdapter { dataItem ->
            onDataItemClicked(dataItem)
        }
        binding.rvTopRatedDestinations.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = topRatedDestinationAdapter
        }

        nearestDestinationAdapter = DestinationAdapter { dataItem ->
            onDataItemClicked(dataItem)
        }
        binding.rvNearestDestinations.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = nearestDestinationAdapter
        }
    }

    private fun observeViewModel() {
        homeViewModel.topRatedDestinations.observe(viewLifecycleOwner) { resultState ->
            when (resultState) {
                is ResultState.Loading -> {
                    // Nothing to do
                }
                is ResultState.Success -> {
                    topRatedDestinationAdapter.submitList(resultState.data)
                }
                is ResultState.Error -> {
                    Log.e("HomeFragment", "Error loading data: ${resultState.error}")
                }
            }
        }

        homeViewModel.userName.observe(viewLifecycleOwner) { userName ->
            binding.tvWelcome.text = getString(R.string.halo_nama, userName)
        }

        homeViewModel.destinationList.observe(viewLifecycleOwner) { resultState ->
            when (resultState) {
                is ResultState.Loading -> {
                    // Nothing to do
                }
                is ResultState.Success -> {
                    getCurrentLocation(resultState.data)
                }
                is ResultState.Error -> {
                    Log.e("HomeFragment", "Error loading data: ${resultState.error}")
                }
            }
        }
    }

    private fun onDataItemClicked(dataItem: DataItem) {
        val intent = Intent(requireContext(), DestinationDetailActivity::class.java).apply {
            putExtra(DestinationDetailActivity.EXTRA_DESTINATION, dataItem)
        }
        startActivity(intent)
    }

    private fun getCurrentLocation(destinations: List<DataItem>) {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                val nearestDestinations = getNearestDestinations(location, destinations)
                nearestDestinationAdapter.submitList(nearestDestinations)

                // Log nearest destinations
                nearestDestinations.forEach { destination ->
                    Log.d("HomeFragment", "Nearest Destination: ${destination.placeName} at (${destination.lat}, ${destination.lon})")
                }
            }
        }
    }

    private fun getNearestDestinations(currentLocation: Location, destinations: List<DataItem>): List<DataItem> {
        return destinations.filter { it.lat != null && it.lon != null }
            .sortedBy { destination ->
                val destinationLocation = Location("").apply {
                    latitude = destination.lat!!
                    longitude = destination.lon!!
                }
                currentLocation.distanceTo(destinationLocation)
            }.take(5)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 100
    }
}







