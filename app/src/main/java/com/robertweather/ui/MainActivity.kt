package com.robertweather.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.PermissionChecker
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.robertweather.BuildConfig
import com.robertweather.R
import com.robertweather.databinding.ActivityMainBinding
import com.robertweather.network.city.CityEntity
import com.robertweather.network.city.CityService
import com.robertweather.network.weather.OneCallEntity
import com.robertweather.network.weather.WeatherService
import com.robertweather.ui.fragments.HomeErrorFragment
import com.robertweather.ui.fragments.HomeFragment
import com.robertweather.ui.fragments.HomeLoadingFragment
import com.robertweather.utils.checkForInternet
import com.robertweather.utils.showSnack
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val REQUEST_PERMISSIONS_REQUEST_CODE = 34

    private var latitude = ""
    private var longitude = ""

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        showLoadingFragment()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        setupLocation()
    }

    private fun setupLocation(){
        if(checkPermission()){
            getLastLocation()
        } else {
            requestPermissions()
        }
    }

    private fun showLoadingFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.home_fragment_container, HomeLoadingFragment())
            .commit()
    }

    private fun showErrorFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.home_fragment_container, HomeErrorFragment())
            .commit()
    }

    private fun showHomeFragment(oneCallEntity: OneCallEntity) {
        try {
            supportFragmentManager.beginTransaction()
                .replace(R.id.home_fragment_container, HomeFragment.newInstance(oneCallEntity))
                .commit()
        } catch (e: Exception) {
            showErrorFragment()
        }
    }

    private fun setupViewData(location: Location) {
        if (checkForInternet(applicationContext)) {
            lifecycleScope.launch {
                latitude = location.latitude.toString()
                longitude = location.longitude.toString()
                val oneCallEntity = getWeatherOneCall(
                    latitude,
                    longitude,
                )
                val city = getCityNameByLatLng(
                    latitude,
                    longitude,
                )
                if (oneCallEntity != null) {
                    oneCallEntity.city = city
                    showHomeFragment(oneCallEntity)
                } else {
                    showErrorFragment()
                }
            }
        } else {
            showSnack(findViewById(android.R.id.content),getString(R.string.no_internet))
            showErrorFragment()
        }
    }

    private suspend fun getWeatherOneCall(latitude: String, longitude: String): OneCallEntity? = withContext(
        Dispatchers.IO){
        var result: OneCallEntity? = null
        try {
            val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(getString(R.string.open_weather_api_url))
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val service: WeatherService = retrofit.create(WeatherService::class.java)

            result = service.getOneCallByLatLng(
                latitude,
                longitude,
                getString(R.string.metric_units),
                getString(R.string.latin_spanish_language_code),
                getString(R.string.api_key)
            )
        } catch (e: HttpException) {
            Log.e("MAIN_ACTIVITY", "getWeather: ${e.response()}")
        }
        result
    }

    private suspend fun getCityNameByLatLng(latitude: String, longitude: String): CityEntity? = withContext(
        Dispatchers.IO){
        var result: CityEntity? = null
        try {
            val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(getString(R.string.open_weather_api_url))
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val service: CityService = retrofit.create(CityService::class.java)

            result = service.getCitiesByLatLng(
                latitude,
                longitude,
                getString(R.string.api_key)
            ).first()
        } catch (e: HttpException) {
            Log.e("MAIN_ACTIVITY", "getCity: ${e.response()}")
        }
        result
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation(onLocation: ((location: Location)-> Unit)? = this::setupViewData){
        fusedLocationClient.lastLocation
            .addOnCompleteListener { taskLocation ->
                if(taskLocation.isSuccessful && taskLocation.result != null){
                    if(onLocation != null) {
                        onLocation(taskLocation.result)
                    }
                } else {
                    Log.e("GET LAST LOCATION", taskLocation.exception.toString())
                    showSnack(
                        findViewById(android.R.id.content),
                        getString(R.string.location_permission_required),
                        getString(R.string.ok)
                    ){
                        requestPermissions()
                    }
                }
            }
    }

    private fun checkPermission():Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PermissionChecker.PERMISSION_GRANTED
    }

    private fun startLocationPermissionRequest(){
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
            REQUEST_PERMISSIONS_REQUEST_CODE
        )
    }

    private fun requestPermissions(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            showSnack(
                findViewById(android.R.id.content),
                getString(R.string.location_request_indispensable),
                getString(R.string.request)
            ) {
                startLocationPermissionRequest()
            }
        } else {
            startLocationPermissionRequest()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == REQUEST_PERMISSIONS_REQUEST_CODE){
            when {
                grantResults.isEmpty() -> Log.i("ON_REQUEST_PERM", "interaccion del usuario cancelada")
                grantResults.first() == PackageManager.PERMISSION_GRANTED -> getLastLocation()
                else -> {
                    showSnack(
                        findViewById(android.R.id.content),
                        getString(R.string.permission_denied),
                        getString(R.string.open_settings)
                    ) {
                        Intent().apply{
                            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                    }
                }
            }
        }
    }
}