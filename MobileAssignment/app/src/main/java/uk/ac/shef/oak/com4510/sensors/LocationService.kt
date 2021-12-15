package uk.ac.shef.oak.com4510.sensors

import android.Manifest
import android.app.Activity
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import uk.ac.shef.oak.com4510.MapsActivity

class LocationService : Service() {

    private lateinit var mainActivity: MapsActivity
    lateinit var locationPermissionRequest: ActivityResultLauncher<Array<String>>

    fun setActivity(activity: MapsActivity){
        mainActivity = activity
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun getRequest(): ActivityResultLauncher<Array<String>> {
        locationPermissionRequest = mainActivity.registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                    // Precise location access granted.
                }
                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    // Only approximate location access granted.
                } else -> {
                // No location access granted.
            }
            }
        }
        return locationPermissionRequest
    }


    override fun onBind(intent: Intent?): IBinder? {
        Log.d("LocationService", "onBind!")
        TODO("Not yet implemented")

    }

}