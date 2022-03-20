package com.linbug.compass

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import com.linbug.compass.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), SensorEventListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var sensorManager: SensorManager
    private lateinit var accelerometer: Sensor
    private lateinit var magnetometer: Sensor
//    private lateinit var gravityMeter: Sensor

    private var lastAccelerometer = FloatArray(3)
    private var lastMagnetometer = FloatArray(3)
//    private var lastGravityMeter = FloatArray(3)

    private var lastAccelerometerSet = false
    private var lastMagnetometerSet = false
//    private var lastGravityMeterSet = false

    private var r = FloatArray(9)
    private var orientation = FloatArray(3)
    private var currentDegree = 0f
//    private val alpha: Float = 0.8f

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
//        gravityMeter = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)
    }


    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME)
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_GAME)
//        sensorManager.registerListener(this, gravityMeter, SensorManager.SENSOR_DELAY_GAME)
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this, accelerometer)
        sensorManager.unregisterListener(this, magnetometer)
//        sensorManager.unregisterListener(this, gravityMeter)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) {
            return
        }

        when (event.sensor) {
            accelerometer -> {
                System.arraycopy(event.values, 0, lastAccelerometer, 0, event.values.size)

//                lastGravityMeter[0] = alpha * lastGravityMeter[0] + (1 - alpha) * event.values[0]
//                lastGravityMeter[1] = alpha * lastGravityMeter[1] + (1 - alpha) * event.values[1]
//                lastGravityMeter[2] = alpha * lastGravityMeter[2] + (1 - alpha) * event.values[2]
//
//                lastAccelerometer[0] = event.values[0] - lastGravityMeter[0]
//                lastAccelerometer[1] = event.values[1] - lastGravityMeter[1]
//                lastAccelerometer[2] = event.values[2] - lastGravityMeter[2]

                lastAccelerometerSet = true
            }
            magnetometer -> {
                System.arraycopy(event.values, 0, lastMagnetometer, 0, event.values.size)
                lastMagnetometerSet = true
            }
//            gravityMeter -> {
//                System.arraycopy(event.values, 0, lastGravityMeter, 0, event.values.size)
//                lastGravityMeter[0] = alpha * lastGravityMeter[0] + (1 - alpha) * event.values[0]
//                lastGravityMeter[1] = alpha * lastGravityMeter[1] + (1 - alpha) * event.values[1]
//                lastGravityMeter[2] = alpha * lastGravityMeter[2] + (1 - alpha) * event.values[2]
//                lastGravityMeterSet = true
//            }
        }

        if (lastAccelerometerSet && lastMagnetometerSet) {
            updateCompass()
        }
    }

    private fun updateCompass() {
        val degrees = (Math.toDegrees(SensorManager.getOrientation(r, orientation)[0].toDouble()) + 360).toFloat() % 360

        SensorManager.getRotationMatrix(r, null, lastAccelerometer, lastMagnetometer)
        binding.degree.text = "Heading : ${degrees.toInt()} degrees"

        val ra = RotateAnimation(currentDegree, -degrees, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        ra.duration = 250
        ra.fillAfter = true
        binding.pointer.startAnimation(ra)
        currentDegree = -degrees
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        //DO nothing
    }
}