package com.example.mywheatherapp

import android.os.AsyncTask
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import java.lang.Exception
import java.net.URL
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.*

class AlternateActivity  : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.model)

        weatherTask().execute()
    }

    inner class weatherTask(): AsyncTask<String, Void, String>(){
        override fun onPreExecute() {
            super.onPreExecute()
            // Muestr el ProgressVar y oculta el diseño principal
            findViewById<ProgressBar>(R.id.progressBarIndicator).visibility = View.VISIBLE
        }

        override fun doInBackground(vararg p0: String?): String {
            val city = "4005539" //Guadalajara = 4005539
            val api = "ad46f66e8ad0d2391fbede79171d7c90"

            val response: String = try {
                URL("https://api.openweathermap.org/data/2.5/weather?id=$city&units=metric&appid=$api")
                    .readText(
                        Charsets.UTF_8
                    )
            }catch (e: Exception){
                e.localizedMessage
            }
            return response
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            try {
                val jsonObj = JSONObject(result)
                val main = jsonObj.getJSONObject("main")
                val sys = jsonObj.getJSONObject("sys")
                val wind = jsonObj.getJSONObject("wind")
                val weather = jsonObj.getJSONArray("weather").getJSONObject(0)
                val lastUpdate: Long = jsonObj.getLong("dt")
                val updateAt = "Actualizado el: " + SimpleDateFormat("dd/mm/yyyy hh:mma",
                Locale.ENGLISH).format(lastUpdate*1000)
                val temp = main.getString("temp") + " °C"
                val tempMin = "Temp min: " + main.getString("temp_min") + "°C"
                val tempMax = "Temp max: " + main.getString("temp_max") + "°C"
                val pressure = main.getString("pressure")
                val humidity = main.getString("humidity")
                val sunrise:Long = sys.getLong("sunrise")
                val sunset:Long = sys.getLong("sunset")
                val windSpeed = wind.getString("speed")
                val weatherDescription = weather.getString("description")

                findViewById<TextView>(R.id.dateTextView).text = updateAt
                findViewById<TextView>(R.id.statusTextView).text = weatherDescription.capitalize()
                findViewById<TextView>(R.id.temperatureTextView).text = temp
                findViewById<TextView>(R.id.tempMaxTextView).text = tempMax
                findViewById<TextView>(R.id.tempMinTextView).text = tempMin
                findViewById<TextView>(R.id.sunriseTextView).text = SimpleDateFormat("hh:mma",
                Locale.ENGLISH).format(Date(sunrise*1000))
                findViewById<TextView>(R.id.sunsetTextView).text = SimpleDateFormat("hh:mma",
                Locale.ENGLISH).format(Date(sunset*1000))
                findViewById<TextView>(R.id.pressureTextView).text = pressure
                findViewById<TextView>(R.id.humidityTextView).text = humidity
                findViewById<TextView>(R.id.windTextView).text = windSpeed

                findViewById<ProgressBar>(R.id.progressBarIndicator).visibility = View.GONE
                findViewById<View>(R.id.detailsContainer).visibility = View.VISIBLE

            }catch (e: Exception){
                print(e.message)
                findViewById<ProgressBar>(R.id.progressBarIndicator).visibility = View.GONE
                findViewById<View>(R.id.detailsContainer).visibility = View.GONE

            }
        }
    }
}