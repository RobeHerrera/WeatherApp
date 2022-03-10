package com.robertweather.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.robertweather.databinding.FragmentHomeErrorBinding

class HomeErrorFragment : Fragment(){
    private var _binding: FragmentHomeErrorBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentHomeErrorBinding.inflate(inflater, container, false)
        return binding.root
    }
}