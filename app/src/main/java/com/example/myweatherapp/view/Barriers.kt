package com.example.myweatherapp.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.myweatherapp.R
import com.example.myweatherapp.databinding.BarriersLayoutBinding

class Barriers: AppCompatActivity() {
    private lateinit var binding: BarriersLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = BarriersLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}