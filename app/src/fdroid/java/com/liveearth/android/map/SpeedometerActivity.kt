package com.liveearth.android.map

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.type.LatLng
import com.liveearth.android.map.clasess.Misc
import kotlinx.android.synthetic.fdroid.activity_world_quiz_select_continet.*
import kotlinx.android.synthetic.main.activity_speedometer.*
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt
import kotlin.math.roundToLong

@SuppressLint("LogNotTimber")
class SpeedometerActivity : AppCompatActivity() {
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationManager: LocationManager
    private lateinit var listener: LocationListener
    private var isStarted = false
    var maxSpeed = 0
    var distance = 0.0
    var previousLocation: Location? = null
    var previousTime: Long = 0
    private val handler: Handler = Handler()

    @SuppressLint("MissingPermission", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_speedometer)

        btnBackSpeedometer.setOnClickListener {
            onBackPressed()
        }

        btnStart.setOnClickListener {
            if (!isStarted) {
                maxSpeed = 0
                distance = 0.0
                Misc.startingTime = System.currentTimeMillis()
                previousLocation = null
                handler.post(runTimer)
                isStarted = true
                textStart.text = "Stop"
                imgBtnStart.setImageResource(R.drawable.ic_baseline_stop_24)
            } else {
                handler.removeCallbacks(runTimer)
                isStarted = false
                textStart.text = "Start"
                textSpeedDigital.text = "0"

                imgBtnStart.setImageResource(R.drawable.ic_baseline_play_arrow_24)
            }
        }

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        listener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                if (isStarted) {
                    Log.d(Misc.logKey, location.toString())
                    val speed = (location.speed * 3.6).roundToInt()

                    if (previousLocation != null) {
                        distance += (location.distanceTo(previousLocation!!)
                            .roundToLong() / 1000.0)
                        textDistanceDigital.text = String.format("%.3f", distance)
                    }

                    textSpeedDigital.text = speed.toString()
                    if (maxSpeed < speed) {
                        maxSpeed = speed
                        textMaxSpeedDigital.text = maxSpeed.toString()
                        handler.post(runTimer)
                    }

                    previousLocation = location
                }
            }

            override fun onStatusChanged(s: String, i: Int, bundle: Bundle) {}
            override fun onProviderEnabled(s: String) {}
            override fun onProviderDisabled(s: String) {
            }
        }
//        if (ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_COARSE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            return
//        }
        locationManager.requestLocationUpdates("gps", 0, 0f, listener)

//        locationCallback = object : LocationCallback() {
//            @SuppressLint("SetTextI18n", "LogNotTimber")
//            override fun onLocationResult(p0: LocationResult) {
//                super.onLocationResult(p0)
//                if (isStarted) {
//                    Log.d(Misc.logKey, p0.toString())
//                    var speed = p0.lastLocation.speed.toInt()
//
//                    if (previousLocation != null) {
//                        distance += p0.lastLocation.distanceTo(previousLocation!!)
//                            .roundToLong() / 100.0
//                        speed =
//                            (distance / ((System.currentTimeMillis() - previousTime) / 3600000))?.roundToInt()
//                    }
//
//                    textSpeedDigital.text = speed.toString()
//                    if (maxSpeed < speed) {
//                        maxSpeed = speed
//                        textMaxSpeedDigital.text = maxSpeed.toString()
//                        handler.post(runTimer)
//                    }
//
//                    previousLocation = p0.lastLocation
//                    previousTime = System.currentTimeMillis()
//                }
//            }
//        }

//        val locationRequest = LocationRequest.create()
//        locationRequest.fastestInterval = 1000
//        locationRequest.interval = 2000
//        LocationServices.getFusedLocationProviderClient(this)
//            .requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())

    }

    @SuppressLint("DefaultLocale")
    private fun setTime(textView: TextView) {
        val millis: Long = System.currentTimeMillis() - Misc.startingTime
        val hms = java.lang.String.format(
            "%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
            TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(
                TimeUnit.MILLISECONDS.toHours(
                    millis
                )
            ),
            TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(
                TimeUnit.MILLISECONDS.toMinutes(
                    millis
                )
            )
        )
        textView.text = hms
    }


    private val runTimer: Runnable by lazy {
        return@lazy object : Runnable {
            override fun run() {
                setTime(textTimeDigital)
                handler.postDelayed(this, 1000)
            }
        }
    }

}