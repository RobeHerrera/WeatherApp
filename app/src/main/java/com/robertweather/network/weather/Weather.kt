package com.robertweather.network.weather

import java.io.Serializable

data class Weather(
    val main: String,
    val description: String,
    val icon: String,
): Serializable
