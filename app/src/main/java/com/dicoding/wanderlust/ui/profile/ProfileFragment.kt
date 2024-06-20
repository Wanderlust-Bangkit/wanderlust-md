package com.dicoding.wanderlust.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.dicoding.wanderlust.data.ResultState
import com.dicoding.wanderlust.databinding.FragmentProfileBinding
import com.dicoding.wanderlust.ui.ViewModelFactory
import com.dicoding.wanderlust.ui.adapter.FavoriteAdapter
import com.dicoding.wanderlust.ui.destination.DestinationDetailActivity
import com.dicoding.wanderlust.ui.settings.SettingsActivity

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val profileViewModel: ProfileViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }

    private lateinit var favoriteAdapter: FavoriteAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profileViewModel.user.observe(viewLifecycleOwner) { user ->
            binding.profileName.text = user.email
            binding.profileEmail.text = user.name
            profileViewModel.getAllFavorites(user.userId)
        }

        favoriteAdapter = FavoriteAdapter { dataItem ->
            val intent = Intent(requireContext(), DestinationDetailActivity::class.java)
            intent.putExtra(DestinationDetailActivity.EXTRA_DESTINATION, dataItem)
            startActivity(intent)
        }

        binding.rvFavoriteDestination.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = favoriteAdapter
        }

        profileViewModel.destinationList.observe(viewLifecycleOwner) { result ->
            when (result) {
                is ResultState.Loading -> {
                    // Show loading
                }
                is ResultState.Success -> {
                    if (result.data.isEmpty()) {
                        binding.tvEmptyFavorites.visibility = View.VISIBLE
                        binding.rvFavoriteDestination.visibility = View.GONE
                    } else {
                        binding.tvEmptyFavorites.visibility = View.GONE
                        binding.rvFavoriteDestination.visibility = View.VISIBLE
                        favoriteAdapter.submitList(result.data)
                    }
                }
                is ResultState.Error -> {
                    // Handle error
                }
            }
        }

        binding.settingsIcon.setOnClickListener {
            val intent = Intent(requireContext(), SettingsActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


