package com.robertweather.ui.viewmodel

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.view.isVisible
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.robertweather.R
import com.robertweather.databinding.ActivityMainBinding
import com.robertweather.network.api.CityService
import com.robertweather.network.model.CityEntity
import com.robertweather.network.model.OneCallEntity
import kotlinx.coroutines.launch
import mx.kodemia.weatherapp.network.service.GetCity
import mx.kodemia.weatherapp.network.service.GetWeather
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivityViewModel : ViewModel() {

    //Service
    lateinit var serviceGetWeather: GetWeather
    lateinit var serviceGetCity: GetCity

    //LiveDatas
    val getWeatherResponse = MutableLiveData<OneCallEntity>()
    val getCityResponse = MutableLiveData<List<CityEntity>>()

    private lateinit var binding: ActivityMainBinding

    fun onCreate() {
        serviceGetWeather = GetWeather()
        serviceGetCity = GetCity()
    }

    //Funcion
    fun getWeather(
        lat: String,
        lon: String,
        units: String?,
        lang: String?,
        appid: String
    ): OneCallEntity? {
        var entity: OneCallEntity? = null
        viewModelScope.launch {
            val response = serviceGetWeather.getWeatherService(lat, lon, units, lang, appid)
            if (response.isSuccessful) {
                getWeatherResponse.postValue(response.body())
                entity = response.body()!!
            } else {
//                binding.errorContainer.isVisible = true
            }
        }
        return entity
    }

    fun getCity(latitude: String, longitude: String, appid: String): CityEntity? {
            var result: CityEntity? = null
        viewModelScope.launch {
            try {
                val retrofit: Retrofit = Retrofit.Builder()
                    .baseUrl("https://api.openweathermap.org/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                val service: CityService = retrofit.create(CityService::class.java)

                result = service.getCitiesByLatLng(
                    latitude,
                    longitude,
                    R.string.api_key.toString()
                ).body()?.first()
            } catch (e: HttpException) {
                Log.e("MAIN_ACTIVITY", "getCity: ${e.response()}")
            }
        }
            return result
    }
}