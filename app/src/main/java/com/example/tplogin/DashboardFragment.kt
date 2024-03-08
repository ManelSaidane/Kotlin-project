package com.example.tplogin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.tplogin.databinding.FragmentDashboardBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class DashboardFragment : Fragment() {

    lateinit var user: FirebaseUser
    lateinit var auth: FirebaseAuth
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDashboardBinding.inflate(layoutInflater, container, false)

        val rootView = binding.root

        auth = FirebaseAuth.getInstance()
        user = auth.currentUser!!

        setData(binding)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnNavigateToUsers.setOnClickListener {
            // Use the Navigation component to navigate to the ProfileFragment
            findNavController().navigate(R.id.action_dashboardFragment_to_userListFragment)
        }
        // Assuming you have a button with ID btnNavigateToProfile in your FragmentDashboardBinding
        binding.btnNavigateToProfile.setOnClickListener {
            // Use the Navigation component to navigate to the ProfileFragment
            findNavController().navigate(R.id.action_dashboardFragment_to_profileFragment4)
        }
    }

    private fun setData(binding: FragmentDashboardBinding) {
        binding.tvDetails.text = user.email.toString()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}
