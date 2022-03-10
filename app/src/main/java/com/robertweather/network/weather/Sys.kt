package com.robertweather.network.weather

import java.io.Serializable

data class Sys (
    val country: String,
    val sunrise: Long,
    val sunset: Long,
): Serializable