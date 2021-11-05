package com.liveearth.android.map

import android.annotation.SuppressLint
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.TextView
import com.liveearth.android.map.clasess.Misc
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.liveearth.android.map.interfaces.OnBackPressCallBack
import kotlinx.android.synthetic.main.activity_speedometer.*
import java.util.concurrent.TimeUnit
import kotlin.math.roundToLong

class SpeedometerActivity : AppCompatActivity() {
    private lateinit var locationCallback: LocationCallback
    private var isStarted = false
    var maxSpeed = 0
    var distance = 0.0
    var previousLocation: Location? = null
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
                textStartStop.text = "Stop"
                imgBtnStart.setImageResource(R.drawable.ic_baseline_stop_24)
            } else {
                handler.removeCallbacks(runTimer)
                isStarted = false
                textStartStop.text = "Start"
                imgBtnStart.setImageResource(R.drawable.ic_baseline_play_arrow_24)
            }
        }

        locationCallback = object : LocationCallback() {
            @SuppressLint("SetTextI18n", "LogNotTimber")
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)
                if (isStarted) {
                    Log.d(Misc.logKey, p0.toString())
                    val speed = p0.lastLocation.speed.toInt()

                    if (previousLocation != null) {
                        distance += p0.lastLocation.distanceTo(previousLocation!!).roundToLong() / 100.0
                    }

                    textSpeedDigital.text = speed.toString()
                    if (maxSpeed < speed) {
                        maxSpeed = speed
                        textMaxSpeedDigital.text = maxSpeed.toString()
                        handler.post(runTimer)
                    }

                    previousLocation = p0.lastLocation
                }
            }
        }

        val locationRequest = LocationRequest.create()
        locationRequest.fastestInterval = 1000
        locationRequest.interval = 2000
        LocationServices.getFusedLocationProviderClient(this)
            .requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())

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

    override fun onBackPressed() {
        Misc.onBackPress(this, Misc.isSpeedometerBackIntEnabled, object : OnBackPressCallBack {
            override fun onBackPress() {
                finish()
            }
        })
    }

}