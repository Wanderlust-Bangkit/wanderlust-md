package com.dicoding.wanderlust.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.wanderlust.databinding.FragmentHomeBinding
import com.dicoding.wanderlust.ui.ViewModelFactory

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val factory = ViewModelFactory.getInstance(requireContext())
        homeViewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]

        // Setup RecyclerView
        setupRecyclerView()

        return root
    }

    private fun setupRecyclerView() {
        val layoutManagerSuka = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.rvDestinasiSuka.layoutManager = layoutManagerSuka

        val layoutManagerTerdekat = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.rvDestinasiTerdekat.layoutManager = layoutManagerTerdekat
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
