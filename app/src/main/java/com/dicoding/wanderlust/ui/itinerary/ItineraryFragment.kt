package com.dicoding.wanderlust.ui.itinerary

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.dicoding.wanderlust.data.ResultState
import com.dicoding.wanderlust.databinding.FragmentItineraryBinding
import com.dicoding.wanderlust.remote.response.ItineraryItem
import com.dicoding.wanderlust.ui.ViewModelFactory
import com.dicoding.wanderlust.ui.adapter.ItineraryAdapter

class ItineraryFragment : Fragment() {

    private var _binding: FragmentItineraryBinding? = null
    private val binding get() = _binding!!

    private val itineraryViewModel: ItineraryViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }

    private lateinit var itineraryAdapter: ItineraryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentItineraryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModel()

        binding.fabAdd.setOnClickListener {
            val intent = Intent(activity, AddItineraryActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupRecyclerView() {
        itineraryAdapter = ItineraryAdapter { itineraryItem ->
            onDataItemClicked(itineraryItem)
        }
        binding.rvItinerary.apply {
            adapter = itineraryAdapter
            setHasFixedSize(true)
        }
    }

    private fun onDataItemClicked(itineraryItem: ItineraryItem) {
        val intent = Intent(requireContext(), ItineraryDetailActivity::class.java).apply {
            putExtra(ItineraryDetailActivity.EXTRA_ITINERARY, itineraryItem)
        }
        startActivity(intent)
    }

    private fun observeViewModel() {
        itineraryViewModel.itineraryList.observe(viewLifecycleOwner) { resultState ->
            Log.d("ItineraryFragment", "Observe ViewModel: $resultState")
            when (resultState) {
                is ResultState.Loading -> {
                    Log.d("ItineraryFragment", "Loading state")
                    binding.progressBar.visibility = View.VISIBLE
                    binding.rvItinerary.visibility = View.GONE
                    binding.imgNoIllustration.visibility = View.GONE
                    binding.tvNoResults.visibility = View.GONE
                }
                is ResultState.Success -> {
                    Log.d("ItineraryFragment", "Success state with data: ${resultState.data}")
                    binding.progressBar.visibility = View.GONE
                    val itineraries = resultState.data
                    if (itineraries.isEmpty()) {
                        Log.d("ItineraryFragment", "No itineraries found")
                        binding.rvItinerary.visibility = View.GONE
                        binding.imgNoIllustration.visibility = View.VISIBLE
                        binding.tvNoResults.visibility = View.VISIBLE
                    } else {
                        binding.rvItinerary.visibility = View.VISIBLE
                        binding.imgNoIllustration.visibility = View.GONE
                        binding.tvNoResults.visibility = View.GONE
                        itineraryAdapter.submitList(itineraries)

                        Log.d("ItineraryList", "Number of itineraries: ${itineraries.size}")
                    }
                }
                is ResultState.Error -> {
                    Log.e("ItineraryFragment", "Error state with message: ${resultState.error}")
                    binding.progressBar.visibility = View.GONE
                    binding.rvItinerary.visibility = View.GONE
                    binding.imgNoIllustration.visibility = View.VISIBLE
                    binding.tvNoResults.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

