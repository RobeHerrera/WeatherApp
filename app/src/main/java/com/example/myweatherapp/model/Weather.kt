package mx.datafox.climapersonal.model

import java.io.Serializable

data class Weather(
    val main: String,
    val description: String,
    val icon: String,
): Serializable
