package com.robertweather.network.model


import java.io.Serializable

data class OneCallEntity(
    val current: Current,
    val hourly: List<Current>,
    var city: CityEntity?
): Serializable
