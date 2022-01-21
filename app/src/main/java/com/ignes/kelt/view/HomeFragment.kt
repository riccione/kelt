package com.ignes.kelt.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.ignes.kelt.R
import com.ignes.kelt.databinding.FragmentHomeBinding

/**
 * Home Fragment - main screen of the app: match functionality
 */
class HomeFragment : Fragment() {
    private lateinit var _binding: FragmentHomeBinding
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        auth = Firebase.auth

        // Enable Firestore logging
        FirebaseFirestore.setLoggingEnabled(true)

        return binding.root
    }

    companion object {
        fun newInstance() = HomeFragment()
        private const val TAG = "HomeFragment"
    }
}