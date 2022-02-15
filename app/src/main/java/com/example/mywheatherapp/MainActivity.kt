package com.example.mywheatherapp

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.example.mywheatherapp.databinding.ActivityMainBinding
import com.example.mywheatherapp.network.WeatherEntity
import com.example.mywheatherapp.network.WeatherService
import com.example.mywheatherapp.utils.checkForInternet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
//        setContentView(R.layout.activity_main)

//        setupActionBar()
        setupViewData()
//        binding.addressTextView.text = "Holaaa"
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setupViewData() {
        if (checkForInternet(this)) {
            lifecycleScope.launch {
                formatResponse(getWeather())
            }
        } else {
            showError("Sin Accesso a Internet")
        }
    }

    private fun setupActionBar() {
        lifecycleScope.launch {
            formatResponse(getWeather())
        }
    }

    private suspend fun getWeather(): WeatherEntity = withContext(Dispatchers.IO)
    {
        setupTitle("Consultando")
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service: WeatherService = retrofit.create(WeatherService::class.java)

        service.getWeatherById(4005539L, "metric", "ad46f66e8ad0d2391fbede79171d7c90")
    }

    private fun setupTitle(newTitle: String) {
        supportActionBar?.let { title = newTitle }
    }

    private fun formatResponse(weatherEntity: WeatherEntity) {
        try {
            val temp = "${weatherEntity.main.temp.toInt()} °C"
            val cityName = weatherEntity.name
            val country = weatherEntity.sys.country
            val address = "$cityName, $country"
            val dateNow = Calendar.getInstance().time
            val tempMin = "Min: ${weatherEntity.main.temp_min.toInt()}°"
            val tempMax = "Max: ${weatherEntity.main.temp_max.toInt()}°"
            val status = "Sensación: ${weatherEntity.main.feels_like.toInt()}°"
//        setupTitle("$temp en $name, $country")

            // Otra forma de utilzarlo
            // binding.addressTextView.text = address

            binding.apply {
                addressTextView.text = address
                dateTextView.text = dateNow.toString()
                temperatureTextView.text = temp
                statusTextView.text = status
                tempMinTextView.text = tempMin
                tempMaxTextView.text = tempMax
            }
//        showIndicator(false)
        } catch (exception: Exception) {
            showError("Ha ocurrido un error")
            Log.e("Error format", "Ha ocurrido un error")
        }

    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showIndicator(visible: Boolean) {
        binding.progressBarIndicator.isVisible = visible
    }

}