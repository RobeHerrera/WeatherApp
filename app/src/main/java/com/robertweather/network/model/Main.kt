package com.robertweather.network.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Main(
    val temp: Double,
    @SerializedName(value = "feels_like")
    val feelsLike: Double,
    @SerializedName(value = "temp_max")
    val tempMax: Double,
    @SerializedName(value = "temp_min")
    val tempMin: Double,
    val pressure: Int,
    val humidity: Int,
): Serializable