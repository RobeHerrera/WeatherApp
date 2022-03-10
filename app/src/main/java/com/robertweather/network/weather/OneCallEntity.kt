package com.robertweather.network.weather


import com.robertweather.network.city.CityEntity
import java.io.Serializable

data class OneCallEntity(
    val current: Current,
    val hourly: List<Current>,
    var city: CityEntity?
): Serializable
