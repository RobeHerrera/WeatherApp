package com.robertweather.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.robertweather.databinding.FragmentHomeBinding
import com.robertweather.network.model.Current
import com.robertweather.network.model.OneCallEntity
import com.robertweather.ui.adapters.PredictionCardAdapter
import java.text.SimpleDateFormat
import java.util.*

private const val ONE_CALL_ENTITY = "oneCallEntity"

class HomeFragment: Fragment(){
    private var oneCallEntity: OneCallEntity? = null
    private var units: Boolean = false

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        arguments?.let {
            oneCallEntity = it.getSerializable(ONE_CALL_ENTITY) as OneCallEntity
            units = it.getSerializable("units") as Boolean
        }
        formatResponse(oneCallEntity)
        return binding.root
    }

    companion object {
        /**
         * @param oneCallEntity Weather data to show in the ui.
         * @return A new instance of fragment HomeFragment.
         */
        @JvmStatic
        fun newInstance(oneCallEntity: OneCallEntity, units: Boolean) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ONE_CALL_ENTITY, oneCallEntity)
                    putSerializable("units", units)
                }
            }
    }

    private fun initPredictionsRecyclerView(predictions: List<Current>){
        binding.recyclerViewNextHours.apply {
            layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)
            setHasFixedSize(true)
            adapter = PredictionCardAdapter(this.context, predictions)
        }
    }

    private fun formatResponse(oneCallEntity: OneCallEntity?) {
            if (oneCallEntity != null) {
                var unitSymbol = "ºC"

                if (units) {
                    unitSymbol = "ºF"
                }
                val current = oneCallEntity.current
                val cityName = oneCallEntity.city?.name
                val countryCode = oneCallEntity.city?.country
                val address = "$cityName, $countryCode"

                val temp = "${current.temp}$unitSymbol"
                val sorterPredictions = oneCallEntity.hourly.sortedByDescending {it.temp}
                val minTemp = "Min: ${sorterPredictions.last().temp.toInt()}°"
                val maxTemp = "Max: ${sorterPredictions.first().temp.toInt()}°"
                // val feelsLike = "Sensación: ${weatherEntity.main.feelsLike.toInt()}°"
                // val sunrise = weatherEntity.sys.sunrise
                // val sunset = weatherEntity.sys.sunset

                val wind = "${current.wind} km/h"
                val pressure = "${current.pressure} mb"
                val humidity = "${current.humidity}%"

                val icon = current.weather.first().icon.replace('n', 'd')
                val iconResource = resources.getIdentifier("ic_weather_$icon", "drawable", activity?.applicationContext?.packageName)
                val description = current.weather.first().description.uppercase()

                val dateFormatter = SimpleDateFormat("dd MMMM, yyyy", Locale.ENGLISH)
                val updatedAt = dateFormatter.format(Date(oneCallEntity.current.dt*1000))
                // val sunriseFormat = dateFormatter.format(weatherEntity.sys.sunrise*1000)
                // val sunsetFormat = dateFormatter.format(weatherEntity.sys.sunset*1000)

                binding.apply {
                    textViewDate.text = updatedAt
                    textViewCity.text = address
                    textViewWeatherMaxTemp.text = maxTemp
                    textViewWeatherMinTemp.text = minTemp
                    imageWeather.load(iconResource)
                    textViewDescription.text = description
                    textViewWeatherCurrentTemp.text = temp
                    pressureValue.text = pressure
                    humidityValue.text = humidity
                    windValue.text = wind
                }

                initPredictionsRecyclerView(oneCallEntity.hourly)
            }
    }
}