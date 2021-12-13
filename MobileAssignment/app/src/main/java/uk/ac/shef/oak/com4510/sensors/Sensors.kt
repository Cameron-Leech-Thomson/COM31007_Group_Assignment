package uk.ac.shef.oak.com4510.sensors

import android.content.Context.SENSOR_SERVICE
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import uk.ac.shef.oak.com4510.MapsActivity

class Sensors constructor(mainActivity: MapsActivity) {

    private var sensorManager: SensorManager = mainActivity.getSystemService(SENSOR_SERVICE) as SensorManager

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

}