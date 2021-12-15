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

class Thermometer(context: Context, controller: SensorsController) {
    private val THERMOMETER_READING_FREQ_MICRO_SEC: Int = 60000
    private var samplingRateInMicroSec: Long = THERMOMETER_READING_FREQ_MICRO_SEC.toLong()
    private var samplingRateInNanoSec: Long = samplingRateInMicroSec * 1000
    private var timePhoneWasLastRebooted: Long = 0
    private var lastReportTime: Long = 0

    private lateinit var accelerometer: Accelerometer
    private var sensorManager: SensorManager?
    private var thermometerSensor: Sensor
    private var thermometerEventListener: SensorEventListener? = null
    private var _isStarted = false
    val isStarted: Boolean
        get() {return _isStarted}

    var temperatureReading: MutableLiveData<Float> = MutableLiveData<Float>()


    init{
        timePhoneWasLastRebooted = System.currentTimeMillis() - SystemClock.elapsedRealtime()

        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        thermometerSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)!!

        /**
         * this inits the listener and establishes the actions to take when a sensor is available
         * It is not registere to listen at this point, but makes sure the object is available to
         * listen when registered.
         */
        thermometerEventListener  = object : SensorEventListener {
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onSensorChanged(event: SensorEvent) {
                val diff = event.timestamp - this@Thermometer.lastReportTime

                if (diff >= this@Thermometer.samplingRateInNanoSec) {
                    val actualTimeInMseconds =
                        this@Thermometer.timePhoneWasLastRebooted + (event.timestamp / 1000000.0).toLong()
                    if(temperatureReading.value != event.values[0]){temperatureReading.value = event.values[0]}
                    val accuracy = event.accuracy
                    this@Thermometer.lastReportTime = event.timestamp
                    // if we have not see any movement on the side of the accelerometer, let's stop
                    val timeLag = actualTimeInMseconds - accelerometer.getLastReportTime()
                    if (timeLag > STOPPING_THRESHOLD) stopThermometerSensing()
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
    }

    companion object {
        private val TAG = Thermometer::class.java.simpleName

        /**
         * this is used to stop the thermometer if we have not seen any movement in the last 20 seconds
         */
        private const val STOPPING_THRESHOLD = 20000.toLong()
    }

    /**
     * it starts the temperature monitoring and updates the _isStarted status flag
     * @param accelerometer
     */
    fun startThermometerSensing(accelerometer: Accelerometer) {
        this.accelerometer = accelerometer
        sensorManager?.let {
            // if the sensor is null,then mSensorManager is null and we get a crash
            Log.d(TAG, "Starting listener")
            // delay is in microseconds (1millisecond=1000 microseconds)
            // it does not seem to work though
            //stopThermometer();
            // otherwise we stop immediately because
            it.registerListener(
                thermometerEventListener,
                thermometerSensor,
                samplingRateInMicroSec.toInt()
            )
            _isStarted = true
        }
    }

    /**
     * this stops the thermometer and updates the _isStarted status flag
     */
    fun stopThermometerSensing() {
        sensorManager?.let {
            Log.d(TAG, "Stopping listener")
            try {
                it.unregisterListener(thermometerEventListener)
                _isStarted = false
            } catch (e: Exception) {
                // probably already unregistered
                Log.d(Accelerometer.TAG, "failed to unregister sensor, probably not running already")
            }
        }
    }


}