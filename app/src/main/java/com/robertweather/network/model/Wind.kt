package com.robertweather.network.model

import java.io.Serializable

data class Wind(
    val speed:Float,
    val deg: Int,
): Serializable
