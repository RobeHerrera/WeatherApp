package mx.kodemia.weatherapp.network.service

import com.robertweather.network.api.CityService
import com.robertweather.network.api.RetrofitInstance
import com.robertweather.network.model.CityEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

class GetCity {

    //Se instancia el servicio de retrofit con la peticion de la Ciudad
    private val retrofit = RetrofitInstance.getRetrofit().create(CityService::class.java)

    //Se crea la funcion para mandar la peticion con los parametros necesarios para realizarla
    //Con un tipo de retorno del modelo de la respuesta
    suspend fun getCityService(lat: String, lon: String, appid: String):  Response<List<CityEntity>>  {
        return withContext(Dispatchers.IO){
            val response = retrofit.getCitiesByLatLng(lat, lon, appid)
            response
        }
    }

}