package com.dicoding.wanderlust.ui.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.dicoding.wanderlust.databinding.FragmentProfileBinding
import com.dicoding.wanderlust.ui.ViewModelFactory
import com.dicoding.wanderlust.ui.login.LoginActivity

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val profileViewModel: ProfileViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }

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
            Log.d("ProfileFragment", "User: $user")
            Log.d("ProfileFragment", "User Name: ${user.name}, User Email: ${user.email}")
            Log.d("ProfileFragment", "User Email: ${user.email}, User Name: ${user.name}")
            binding.profileName.text = user.name //switch to name later
            binding.profileEmail.text = user.email
        }
        binding.actionLogout.setOnClickListener {
            profileViewModel.logout()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
        binding.settingsIcon.setOnClickListener {
            // Handle settings click
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
