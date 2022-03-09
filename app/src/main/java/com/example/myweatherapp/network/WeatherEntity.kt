package com.example.myweatherapp.network

import com.example.myweatherapp.model.Current

data class WeatherEntity(
    val current: Current,
    val base: String,
    val main: Main,
    val sys: Sys,
    val id: Int,
    val name: String,
    val wind: Wind,
    val weather: List<Weather>,
    val dt: Long
)
