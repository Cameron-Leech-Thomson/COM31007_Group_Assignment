package uk.ac.shef.oak.com4510.sensors

import android.Manifest
import android.content.Context.SENSOR_SERVICE
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorManager
import android.location.Location
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import uk.ac.shef.oak.com4510.MapsActivity
import uk.ac.shef.oak.com4510.R

class Sensors constructor(private val mainActivity: MapsActivity,
                          private var fusedLocationService: FusedLocationProviderClient) {

    private var sensorManager: SensorManager = mainActivity.getSystemService(SENSOR_SERVICE) as SensorManager
    private var locationService = LocationService()
    private var cancelLocationSource = CancellationTokenSource()

    init{
        locationService.setActivity(mainActivity)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun getSensorData(){
        // Get all available sensors on the device:
        var allSensors = sensorManager.getSensorList(Sensor.TYPE_ALL)


        // All relevent sensor names:
        var sensorNames = arrayOf("")

        for (sensor: Sensor in allSensors) {
            Log.d("sensor#" + sensor.id, sensor.name)
        }
        Log.d("sensors", allSensors.size.toString())
    }

    private lateinit var latLng: LatLng

    @RequiresApi(Build.VERSION_CODES.N)
    fun requestLocation() {
        // Check for location permissions:
        if (ActivityCompat.checkSelfPermission(
                mainActivity.baseContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                mainActivity.baseContext,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request permissions if missing:
            locationService.getRequest().launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )

        }

        // Request location:
        fusedLocationService.getCurrentLocation(
            LocationRequest.PRIORITY_HIGH_ACCURACY,
            cancelLocationSource.token).addOnSuccessListener { location: Location? ->
            Log.d("location","Location Retrieved")
            if (location != null) {
                latLng = locationToLatLng(location)
            } else{
                Snackbar.make(mainActivity.findViewById(R.id.coordinator),
                    "Location Unavailable. Please try again later",
                    BaseTransientBottomBar.LENGTH_LONG).show()
            }
        }
    }

    private fun locationToLatLng(location: Location): LatLng{
        val lat = location.latitude
        val lng = location.longitude

        return LatLng(lat, lng)
    }

    fun getLatLng(): LatLng{
        return latLng
    }

}