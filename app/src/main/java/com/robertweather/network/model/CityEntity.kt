package com.robertweather.network.model

import java.io.Serializable

data class CityEntity(
    val name: String,
    val country: String,
) : Serializable
