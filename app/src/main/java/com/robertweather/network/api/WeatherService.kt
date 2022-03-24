package com.robertweather.network.api

import com.robertweather.network.model.OneCallEntity
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {
    @GET("data/2.5/onecall")
    suspend fun getOneCallByLatLng(
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("units") units: String?,
        @Query("lang") lang: String?,
        @Query("appid") appid: String
    ): Response<OneCallEntity>
}