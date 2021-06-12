package com.joejoenchill.clearskies

import android.Manifest
import android.content.DialogInterface
import android.content.IntentFilter
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.joejoenchill.clearskies.adapters.DailyAdapter
import com.joejoenchill.clearskies.location.LocationCallback
import com.joejoenchill.clearskies.location.LocationService
import com.joejoenchill.clearskies.location.LocationStorage
import com.joejoenchill.clearskies.models.DailyItem
import com.joejoenchill.clearskies.models.WeatherResponse
import com.joejoenchill.clearskies.utils.Constants
import com.joejoenchill.clearskies.utils.DataStorage.Companion.setDataStatus
import com.joejoenchill.clearskies.utils.DataStorage.Companion.isDataReceived
import com.joejoenchill.clearskies.utils.Network
import com.joejoenchill.clearskies.utils.NetworkChangeReceiver
import com.joejoenchill.clearskies.utils.OnNetworkListener
import com.joejoenchill.clearskies.weather.WeatherContract
import com.joejoenchill.clearskies.weather.WeatherPresenter
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso


class MainActivity : AppCompatActivity(), WeatherContract.View, SharedPreferences.OnSharedPreferenceChangeListener,
    LocationCallback, OnNetworkListener {

    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var infoNoInternet: MaterialCardView
    private lateinit var txtNoInternet: TextView

    private lateinit var presenter: WeatherPresenter

    private var networkReceiver: NetworkChangeReceiver? = null


    companion object {
        private const val logTag = "MainActivity"
        private const val PERMISSIONS_REQUEST_LOCATION: Int = 99
    }

    private var locationManager : LocationManager? = null
    private var locationService: LocationService? = null

    private var lat: Double = 0.0
    private var lon: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(
            this
        )

        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager?
        locationService = LocationService(this, this)

        presenter = WeatherPresenter(this)

        loadWeatherData()

        networkReceiver = NetworkChangeReceiver(this)

        // Register the listener
        PreferenceManager.getDefaultSharedPreferences(this)
            .registerOnSharedPreferenceChangeListener(this)

        swipeRefresh =  findViewById(R.id.swipeRefresh)

        infoNoInternet = findViewById(R.id.infoNoInternet)
        txtNoInternet = findViewById(R.id.txtNoInternet)

        /*
         * Sets up a SwipeRefreshLayout.OnRefreshListener that is invoked when the user
         * performs a swipe-to-refresh gesture.
         */
        swipeRefresh.setOnRefreshListener {
            Log.i(logTag, "onRefresh called from SwipeRefreshLayout")

            // This method performs the actual data-refresh operation.
            // The method calls setRefreshing(false) when it's finished.
            loadWeatherData()
        }
    }

    private fun loadWeatherData(){
        if (Network.isOnline(this)){
            if(LocationStorage.getLoc(this).getLat() != null && LocationStorage.getLoc(this).getLon()  != null) {
                lat = LocationStorage.getLoc(this).getLat()!!.toDouble()
                lon = LocationStorage.getLoc(this).getLon()!!.toDouble()
                presenter.startLoadingData(lat, lon)
            }else{
                // User refused permission, Accra is added as a default
                presenter.startLoadingData(Constants.Accra_LAT, Constants.Accra_LON)
            }
        }else{
            Toast.makeText(this, "No Internet connection", Toast.LENGTH_LONG).show()
            infoNoInternet.visibility = View.VISIBLE
            txtNoInternet.text = getString(R.string.no_internet_connection)
        }
    }

    override fun onLoadFinished(weatherResponse: WeatherResponse?) {
        if(weatherResponse != null){
            // Today
            val todayWeather = weatherResponse.daily.first()

            // Weather Icon
            val imgWeatherIcon = findViewById<ImageView>(R.id.imgWeatherIcon)
            val imageUrl: String = getString(R.string.icon_2, todayWeather.weather[0].icon)
            Picasso.get()
                .load(imageUrl)
                .into(imgWeatherIcon, object : Callback {
                    override fun onSuccess() {
                        Log.d("icon", "success")
                    }

                    override fun onError(e: Exception?) {
                        Log.d("icon", "error: $imageUrl")
                    }
                })

            // Temperature
            val txtTemperature = findViewById<TextView>(R.id.txtTemperature)
            val valTemp: String = getString(R.string.temperature, todayWeather.temp.day.toInt())
            txtTemperature.text = valTemp

            // Temperature Desc
            val txtWeatherDescription = findViewById<TextView>(R.id.txtWeatherDescription)
            txtWeatherDescription.text = todayWeather.weather[0].description

            // Humidity
            val txtHumidity = findViewById<TextView>(R.id.txtHumidity)
            val valHumidity: String = getString(R.string.humidity, todayWeather.humidity)
            txtHumidity.text = valHumidity

            // Percentage chance of Precipitation
            val txtPoP = findViewById<TextView>(R.id.txtPoP)
            txtPoP.text = todayWeather.pop.toString()


            // Next 7 Days
            val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
            val horizontalLayoutManager =
                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            recyclerView.layoutManager = horizontalLayoutManager
            recyclerView.setHasFixedSize(true)

            val dividerItemDecoration = DividerItemDecoration(
                recyclerView.context,
                horizontalLayoutManager.orientation
            )
            recyclerView.addItemDecoration(dividerItemDecoration)

            val dailyWeather: List<DailyItem> = weatherResponse.daily.drop(1)
            val dailyWeatherAdapter = DailyAdapter(dailyWeather, this)
            recyclerView.adapter = dailyWeatherAdapter

            swipeRefresh.isRefreshing = false
        }
    }


    override fun onLoadFailed(error: String) {
        Log.d(logTag, error)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        return true
    }

    /*
     * Listen for option item selections so that we receive a notification
     * when the user requests a refresh by selecting the refresh action bar item.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            // Check if user triggered a refresh:
            R.id.menu_refresh -> {
                Log.i(logTag, "Refresh menu item selected")

                // Signal SwipeRefreshLayout to start the progress indicator
                swipeRefresh.isRefreshing  = true

                // Start the refresh background task.
                // This method calls setRefreshing(false) when it's finished.
                loadWeatherData()

                return true
            }
        }

        // User didn't trigger a refresh, let the superclass handle this action
        return super.onOptionsItemSelected(item)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if(LocationStorage.getLoc(this).getLat() != null && LocationStorage.getLoc(this).getLon()  != null){
            presenter.startLoadingData(lat, lon)
        }else{
            // No location available, Accra is added as a default
            presenter.startLoadingData(Constants.Accra_LAT, Constants.Accra_LON)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        PreferenceManager.getDefaultSharedPreferences(this)
            .unregisterOnSharedPreferenceChangeListener(this)
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || !isDataReceived(this)) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )) {
                promptUserToAccept()
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    PERMISSIONS_REQUEST_LOCATION
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_LOCATION) {
            // If request is not cancelled, the result arrays are full.
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                // permission was granted, yay! Do the location-related task you need to do.
                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                    == PackageManager.PERMISSION_GRANTED) {

                    // Request location updates
                    locationService?.let {
                        locationManager?.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            10000L,
                            500f,
                            it
                        )
                    }

                    locationService?.let {
                        locationManager?.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            10000L,
                            500f,
                            it
                        )
                    }
                    locationService?.let {
                        locationManager?.requestLocationUpdates(
                            LocationManager.PASSIVE_PROVIDER,
                            10000L,
                            500f,
                            it
                        )
                    }
                }
            } else {
                // User refused, Accra is added as a default
                presenter.startLoadingData(Constants.Accra_LAT, Constants.Accra_LON)
            }
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        return LocationManagerCompat.isLocationEnabled(locationManager)
    }

    private fun promptUserToAccept() {
        AlertDialog.Builder(this)
            .setMessage("Location permission is required to get access to weather data")
            .setPositiveButton(
                "Ok"
            ) { _: DialogInterface?, _: Int ->
                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    PERMISSIONS_REQUEST_LOCATION
                )
            }
            .create()
            .show()
    }

    override fun onLocationResult() {
        Log.d(logTag, "onLocationResult: It's First Time")
        lat = LocationStorage.getLoc(this).getLat()!!.toDouble()
        lon = LocationStorage.getLoc(this).getLon()!!.toDouble()
        presenter.startLoadingData(lat, lon)
        setDataStatus(this, true)
    }

    override fun onNetworkConnected() {
        infoNoInternet.visibility = View.GONE
        loadWeatherData()
    }

    override fun onNetworkDisconnected() {
        infoNoInternet.visibility = View.VISIBLE
        txtNoInternet.text = getString(R.string.no_internet_connection)
    }

    private fun registerNetworkBroadcastForNougat() {
        registerReceiver(networkReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
    }

    override fun onStart() {
        super.onStart()
        try {
            registerNetworkBroadcastForNougat()
        } catch (e: Exception) {
            Log.d(logTag, "onStart: " + "already registered")
        }
    }

    override fun onStop() {
        try {
            unregisterReceiver(networkReceiver)
        } catch (e: Exception) {
            Log.d(logTag, "onStop: " + "already unregistered")
        }
        super.onStop()
    }

}