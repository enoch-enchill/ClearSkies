package com.joejoenchill.clearskies.location

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AlertDialog

class LocationService(context: Context, locationCallback : LocationCallback) : LocationListener {

    private var isDialogShown = true
    private val logTag = "LocationService"
    private var context: Context? = null
    private var callback : LocationCallback

    init {
        this.context = context
        this.callback = locationCallback
    }

    override fun onLocationChanged(location: Location) {
        if(location.latitude != 0.0 && location.longitude !=0.0){
            Log.d(logTag, "onLocationChanged: " + location.latitude)
            Log.d(logTag, "onLocationChanged: " + location.longitude)
            LocationStorage.setLoc(context,  Location(location.latitude.toString(), location.longitude.toString()))
            callback.onLocationResult()
        }
    }

    override fun onStatusChanged(status: String?, i: Int, bundle: Bundle?) {
        Log.d(logTag, "onStatusChanged:")
    }

    override fun onProviderEnabled(provider: String) {
        Log.d(logTag, "onProviderEnabled:")
    }

    override fun onProviderDisabled(provider: String) {
        Log.d(logTag, "onProviderDisabled:$provider")
        if(isDialogShown) {
            openSettings(context)
            isDialogShown = false
        }else {
            isDialogShown = true
        }
    }

    private fun openSettings(context: Context?) {
        if (context != null) {
            AlertDialog.Builder(context)
                .setMessage("The location service is disabled. Access \"Settings\" to check.")
                .setPositiveButton("SETTINGS") { _: DialogInterface?, _: Int ->
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    context.startActivity(intent) }
                .create()
                .show()
        }
    }
}

