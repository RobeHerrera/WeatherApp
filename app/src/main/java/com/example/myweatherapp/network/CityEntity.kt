package com.example.myweatherapp.network

import com.example.myweatherapp.model.Current
import java.io.Serializable

data class CityEntity(
    val name: String,
    val country: String,
) : Serializable