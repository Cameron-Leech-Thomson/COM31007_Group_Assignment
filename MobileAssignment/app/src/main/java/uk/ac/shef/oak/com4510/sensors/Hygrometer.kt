/*
 * Copyright (c) 2019. This code has been developed by Fabio Ciravegna, The University of Sheffield.
 * Updated 2021 by Temitope Adeosun, using Kotlin with MVVM and LiveData implementation
 * All rights reserved. No part of this code can be used without the explicit written permission by the author
 */

package uk.ac.shef.oak.com4510.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.SystemClock
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import java.lang.Exception

class Hygrometer(context: Context, controller: SensorsController) {
    private val HYGROMETER_READING_FREQ_MICRO_SEC: Int = 120000
    private var samplingRateInMicroSec: Long = HYGROMETER_READING_FREQ_MICRO_SEC.toLong()
    private var samplingRateInNanoSec: Long = samplingRateInMicroSec * 1000
    private var timePhoneWasLastRebooted: Long = 0
    private var lastReportTime: Long = 0

    private lateinit var accelerometer: Accelerometer
    private var sensorManager: SensorManager?
    private var hygrometerSensor: Sensor
    private var hygrometerEventListener: SensorEventListener? = null
    private var _isStarted = false
    val isStarted: Boolean
        get() {return _isStarted}

    var humidityReading: MutableLiveData<Float> = MutableLiveData<Float>()


    init{
        timePhoneWasLastRebooted = System.currentTimeMillis() - SystemClock.elapsedRealtime()

        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        hygrometerSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY)!!

        /**
         * this inits the listener and establishes the actions to take when a sensor is available
         * It is not registere to listen at this point, but makes sure the object is available to
         * listen when registered.
         */
        hygrometerEventListener  = object : SensorEventListener {
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onSensorChanged(event: SensorEvent) {
                val diff = event.timestamp - this@Hygrometer.lastReportTime

                if (diff >= this@Hygrometer.samplingRateInNanoSec) {
                    val actualTimeInMseconds =
                        this@Hygrometer.timePhoneWasLastRebooted + (event.timestamp / 1000000.0).toLong()
                    if(humidityReading.value != event.values[0]){humidityReading.value = event.values[0]}
                    val accuracy = event.accuracy
                    Log.i(
                        TAG,
                        controller.mSecsToString(actualTimeInMseconds) +
                                ": current ambient humidity: " +
                                humidityReading.value + "with accuracy: " + accuracy
                    )
                    this@Hygrometer.lastReportTime = event.timestamp
                    // if we have not see any movement on the side of the accelerometer, let's stop
                    val timeLag = actualTimeInMseconds - accelerometer.getLastReportTime()
                    if (timeLag > STOPPING_THRESHOLD) stopHygrometerSensing()
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
    }

    companion object {
        private val TAG = Hygrometer::class.java.simpleName

        /**
         * this is used to stop the hygrometer if we have not seen any movement in the last 20 seconds
         */
        private const val STOPPING_THRESHOLD = 20000.toLong()
    }

    /**
     * it starts the humidity monitoring and updates the _isStarted status flag
     * @param accelerometer
     */
    fun startHygrometerSensing(accelerometer: Accelerometer) {
        this.accelerometer = accelerometer
        sensorManager?.let {
            // if the sensor is null,then mSensorManager is null and we get a crash
            Log.d(TAG, "Starting listener")
            // delay is in microseconds (1millisecond=1000 microseconds)
            // it does not seem to work though
            //stopHygrometer();
            // otherwise we stop immediately because
            it.registerListener(
                hygrometerEventListener,
                hygrometerSensor,
                samplingRateInMicroSec.toInt()
            )
            _isStarted = true
        }
    }

    /**
     * this stops the hygrometer and updates the _isStarted status flag
     */
    fun stopHygrometerSensing() {
        sensorManager?.let {
            Log.d(TAG, "Stopping listener")
            try {
                it.unregisterListener(hygrometerEventListener)
                _isStarted = false
            } catch (e: Exception) {
                // probably already unregistered
                Log.d(Accelerometer.TAG, "failed to unregister sensor, probably not running already")
            }
        }
    }


}