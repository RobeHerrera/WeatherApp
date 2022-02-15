package com.example.mywheatherapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.example.mywheatherapp.databinding.ActivityMainBinding
import com.example.mywheatherapp.network.WeatherEntity
import com.example.mywheatherapp.network.WeatherService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_main)

        setupActionBar()
    }

    private fun setupActionBar(){
        lifecycleScope.launch{
            formatResponse(getWeather())
        }
    }

    private suspend fun getWeather():WeatherEntity = withContext(Dispatchers.IO)
        {
            setupTitle("Consultando")
            val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val service: WeatherService = retrofit.create(WeatherService::class.java)

            service.getWeatherById(4005539L,"metric","ad46f66e8ad0d2391fbede79171d7c90")
        }
    private fun setupTitle(newTitle: String){
        supportActionBar?.let { title = newTitle }
    }

    private fun formatResponse(weatherEntity: WeatherEntity){
        val temp = "${weatherEntity.main.temp} C°"
        val name = weatherEntity.name
        val country = weatherEntity.sys.country
        setupTitle("$temp en $name, $country")
    }

}