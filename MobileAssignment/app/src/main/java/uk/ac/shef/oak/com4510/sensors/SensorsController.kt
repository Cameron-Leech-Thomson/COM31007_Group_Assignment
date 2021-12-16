package uk.ac.shef.oak.com4510.sensors

import android.Manifest
import android.app.Activity
import android.app.Application
import android.content.Context.SENSOR_SERVICE
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import uk.ac.shef.oak.com4510.MapsActivity
import uk.ac.shef.oak.com4510.R
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class SensorsController constructor(private val mainActivity: MapsActivity,
                                    private var fusedLocationService: FusedLocationProviderClient) : Activity() {

    private var locationService = LocationService()
    private var cancelLocationSource = CancellationTokenSource()

    private var pressure: Float = 0f
    private var temperature: Float = 0f
    private var humidity: Float = 0f

    private var barometer: Barometer = Barometer(mainActivity, this)
    private var thermometer: Thermometer = Thermometer(mainActivity,this)
    private var hygrometer: Hygrometer = Hygrometer(mainActivity, this)
    private var accelerometer: Accelerometer = Accelerometer(mainActivity, this,
        barometer, thermometer, hygrometer)


    init{
        locationService.setActivity(mainActivity)

        this.retrieveAccelerometerData().observe(mainActivity,
            { newValue ->
                newValue?.also{
                    //Log.i("Data in UI - Accel", "Sensor time: $it[0], Sensor data: $it[1]")
                }
            })

        this.retrievePressureData().observe(mainActivity,
            { newValue ->
                newValue?.also{
                    pressure = it
                }
            })

        this.retrieveThermometerData().observe(mainActivity,
            { newValue ->
                newValue?.also{
                    temperature = it
                }
            })

        this.retrieveHygrometerData().observe(mainActivity,
            { newValue ->
                newValue?.also{
                    humidity = it
                }
            })

        this.startSensing()
    }

    fun getSensorData(): Array<Float?>{
        return arrayOf(temperature, pressure, humidity)
    }

    private var latLng: LatLng? = null

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
            locationService.getRequest()
            val permissions = locationService.getPermissions()
            permissions.launch(
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

    fun getLatLng(): LatLng? {
        return latLng
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun mSecsToString(actualTimeInMseconds: Long): String {
        val date = Date(actualTimeInMseconds)
        val config = mainActivity.resources.configuration
        val locale = config.locales.get(0)
        return with(SimpleDateFormat("HH:mm:ss", locale) as DateFormat) {
            this.timeZone = TimeZone.getTimeZone("UTC")
            this.format(date)
        }
    }

    /**
     * Calls the needed sensor class to start monitoring the sensor data
     */
    fun startSensing() {
        accelerometer.startAccelerometerSensing()
    }


    /**
     * Calls the needed sensor class to stop monitoring the sensor data
     */
    fun stopSensing() {
        accelerometer.stopAccelerometerSensing()
    }

    /**
     * Func that exposes the humidity as LiveData to the View object
     * @return humidity reading
     */
    private fun retrieveHygrometerData(): LiveData<Float> {
        return hygrometer.humidityReading
    }

    /**
     * Func that exposes the temperature as LiveData to the View object
     * @return temperature reading
     */
    private fun retrieveThermometerData(): LiveData<Float> {
        return thermometer.temperatureReading
    }

    /**
     * Func that exposes the pressure as LiveData to the View object
     * @return pressure
     */
    private fun retrievePressureData(): LiveData<Float> {
        return barometer.pressureReading
    }

    /**
     * Func that exposes the Accelerometer data as LiveData to the View object
     * @return accelerometer reading
     */
    private fun retrieveAccelerometerData(): LiveData<Pair<String, Map<String, Float>>> {
        return accelerometer.accelerometerReading
    }

    /**
     * Func that exposes the status change of the sensor monitoring
     */
    fun isStarted(): LiveData<Boolean> {
        return accelerometer.isStarted
    }

}