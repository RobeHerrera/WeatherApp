package mx.kodemia.weatherapp.network.service

import android.content.Context
import com.robertweather.network.api.RetrofitInstance
import com.robertweather.network.api.WeatherService
import com.robertweather.network.model.OneCallEntity
import com.robertweather.network.model.Weather
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

class GetWeather() {

    //Se instancia el servicio de retrofit con la peticion de LogIn
    private val retrofit = RetrofitInstance.getRetrofit().create(WeatherService::class.java)

    //Se crea la funcion para mandar la peticion con los parametros necesarios para realizarla
    //Con un tipo de retorno del modelo de la respuesta
    suspend fun getWeatherService(
        lat: String, lon: String, units: String?, lang: String?, appid: String): Response<OneCallEntity>
    {
        return withContext(Dispatchers.IO){
            val response = retrofit.getOneCallByLatLng(lat, lon, units, lang, appid)
            response
        }
    }

}