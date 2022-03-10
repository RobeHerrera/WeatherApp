package com.robertweather.network.city

import java.io.Serializable

data class CityEntity(
    val name: String,
    val country: String,
) : Serializable
