package com.robertweather.ui.view

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
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.PermissionChecker
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.robertweather.BuildConfig
import com.robertweather.R
import com.robertweather.core.SharedPreferencesInstance
import com.robertweather.databinding.ActivityMainBinding
import com.robertweather.network.model.CityEntity
import com.robertweather.network.api.CityService
import com.robertweather.network.model.OneCallEntity
import com.robertweather.network.api.WeatherService
import com.robertweather.ui.fragments.HomeErrorFragment
import com.robertweather.ui.fragments.HomeFragment
import com.robertweather.ui.fragments.HomeLoadingFragment
import com.robertweather.ui.viewmodel.MainActivityViewModel
import com.robertweather.utils.checkForInternet
import com.robertweather.utils.showSnack
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivityError"
    private lateinit var binding: ActivityMainBinding
    private val REQUEST_PERMISSIONS_REQUEST_CODE = 34
    private var latitude = ""
    private var longitude = ""

    private var units = false
    private var language = false

    val viewModel: MainActivityViewModel by viewModels()

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    lateinit var shared : SharedPreferencesInstance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        init()
        showLoadingFragment()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        setupLocation()
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        units = sharedPreferences.getBoolean("units", false)
        language = sharedPreferences.getBoolean("language", false)
    }
    private fun init(){
        //Shared
        shared = SharedPreferencesInstance.obtenerInstancia(this)

        //binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.onCreate()
    }

    private fun setupLocation(){
        if(checkPermission()){
            getLastLocation()
        } else {
            requestPermissions()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.actions_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.preferenciasMenu -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
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

    private fun showHomeFragment(oneCallEntity: OneCallEntity, units: Boolean) {
        try {
            supportFragmentManager.beginTransaction()
                .replace(R.id.home_fragment_container, HomeFragment.newInstance(oneCallEntity, units))
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
                    showHomeFragment(oneCallEntity, units)
                } else {
                    showErrorFragment()
                }
            }
        } else {
            showSnack(findViewById(android.R.id.content),getString(R.string.no_internet))
            showErrorFragment()
        }
    }


    private suspend fun getWeatherOneCall(latitude: String, longitude: String): OneCallEntity?
        = withContext(Dispatchers.IO){
        var result: OneCallEntity? = null
        var unit = "metric"
        var languageCode = "es"

        if (units) {
            unit = "imperial"
        }
        if (language) {
            languageCode = "en"
        }

        try {
            val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(getString(R.string.open_weather_api_url))
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val service: WeatherService = retrofit.create(WeatherService::class.java)

            result = service.getOneCallByLatLng(
                latitude,
                longitude,
                unit,
                languageCode,
                getString(R.string.api_key)
            ).body()
        } catch (e: HttpException) {
            Log.e("MAIN_ACTIVITY", "getWeather: ${e.response()}")
        }
        result
    }

    private suspend fun getCityNameByLatLng(latitude: String, longitude: String): CityEntity? = withContext(
        Dispatchers.IO){

//        viewModel.getCity(latitude, longitude, getString(R.string.api_key))
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
            ).body()?.first()
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