package com.example.mywheatherapp

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import coil.load
import com.example.mywheatherapp.databinding.ActivityMainBinding
import com.example.mywheatherapp.databinding.ModelBinding
import com.example.mywheatherapp.network.WeatherEntity
import com.example.mywheatherapp.network.WeatherService
import com.example.mywheatherapp.utils.checkForInternet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ModelBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ModelBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        setContentView(R.layout.activity_main)

//        setupActionBar()
        setupViewData()
//        binding.addressTextView.text = "Holaaa"
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.actions_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_actualizar -> {
                Toast.makeText(this, "Menú seleccionado", Toast.LENGTH_SHORT).show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupViewData() {
        if (checkForInternet(this)) {
            lifecycleScope.launch {
                formatResponse(getWeather())
            }
        } else {
            showError("Sin acceso a Internet")
            binding.detailsContainer.isVisible = false
        }
    }

    private fun setupActionBar() {
        lifecycleScope.launch {
            formatResponse(getWeather())
        }
    }

//    private fun setupTitle(newTitle: String) {
//        supportActionBar?.let { title = newTitle }
//    }

    private fun formatResponse(weatherEntity: WeatherEntity) {
        try {
            val temp = "${weatherEntity.main.temp.toInt()} °C"
            val cityName = weatherEntity.name
            val country = weatherEntity.sys.country
            val address = "$cityName, $country"
            val dateNow = Calendar.getInstance().time
            val tempMin = "Min: ${weatherEntity.main.temp_min.toInt()}°"
            val tempMax = "Max: ${weatherEntity.main.temp_max.toInt()}°"
            val status = weatherEntity.weather[0].description.uppercase()
            // setupTitle("$temp en $cityName, $country")
            val dt = weatherEntity.dt
            val updatedAt = "Actualizado: ${
                SimpleDateFormat(
                    "hh:mm a",
                    Locale.ENGLISH
                ).format(Date(dt * 1000))
            }"
            val sunrise = weatherEntity.sys.sunrise
            val sunriseFormat =
                SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunrise * 1000))
            val sunset = weatherEntity.sys.sunset
            val sunsetFormat =
                SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunset * 1000))
            val wind = "${weatherEntity.wind.speed} km/h"
            val pressure = "${weatherEntity.main.pressure} mb"
            val humidity = "${weatherEntity.main.humidity}%"
            val feelsLike = "Sensación: ${weatherEntity.main.feels_like.toInt()}º"
            val icon = weatherEntity.weather[0].icon
            val iconUrl = "https://openweathermap.org/img/w/$icon.png"

            // Otra forma de utilzarlo
            // binding.addressTextView.text = address

            binding.apply {
                iconImageView.load(iconUrl)
                adressTextView.text = address
                //dateTextView.text = dateNow.toString()
                dateTextView.text = updatedAt
                temperatureTextView.text = temp
                statusTextView.text = status
                tempMinTextView.text = tempMin
                tempMaxTextView.text = tempMax
                sunriseTextView.text = sunriseFormat
                sunsetTextView.text = sunsetFormat
                windTextView.text = wind
                pressureTextView.text = pressure
                humidityTextView.text = humidity
                detailsContainer.isVisible = true
                feelsLikeTextView.text = feelsLike
            }
            showIndicator(false)
        } catch (exception: Exception) {
            showError("Ha ocurrido un error con los datos")
            Log.e("Error format", "Ha ocurrido un error")
            showIndicator(false)
        }

    }

    private suspend fun getWeather(): WeatherEntity = withContext(Dispatchers.IO) {
        showIndicator(true)

        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service: WeatherService = retrofit.create(WeatherService::class.java)

        service.getWeatherById(4005539L, "metric", "sp", "ad46f66e8ad0d2391fbede79171d7c90")
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun showIndicator(visible: Boolean) {
        binding.progressBarIndicator.isVisible = visible
    }

}