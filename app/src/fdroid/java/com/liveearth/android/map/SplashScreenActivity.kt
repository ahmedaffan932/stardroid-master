package com.liveearth.android.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.blongho.country_data.World
import com.liveearth.android.map.clasess.Misc
import com.liveearth.android.map.interfaces.StartActivityCallBack
import com.google.firebase.FirebaseApp

import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import kotlinx.android.synthetic.main.activity_splash_screen.*
import com.google.android.gms.ads.MobileAds
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.liveearth.android.map.interfaces.NativeAdCallBack

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : BaseActivity(), PermissionsListener {
    private var isAdRequestSent: Boolean = false
    private val storageReadPermissionRequest = 101
    private val cameraPermissionRequest = 100
    private lateinit var permissionsManager: PermissionsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        getRemoteConfigValues()

        permissionsManager = PermissionsManager(this)
        World.init(this)
        FirebaseApp.initializeApp(applicationContext)
//        FirebaseMessaging.getInstance().subscribeToTopic("com.liveearthmap.liveearthcam.streetview.gps.map.worldmap.satellite.app")

        MobileAds.initialize(this) {}

        btnStart.setOnClickListener {
            start()
        }

        Handler().postDelayed({
            if (btnStart.visibility != View.VISIBLE) {
                Misc.zoomInView(btnStart, this@SplashScreenActivity, 300)
                Misc.zoomOutView(
                    animLoading,
                    this@SplashScreenActivity,
                    300
                )
            }
        }, 3000)


    }

    override fun onExplanationNeeded(permissionsToExplain: MutableList<String>?) {
        Toast.makeText(this, R.string.user_location_permission_explanation, Toast.LENGTH_LONG)
            .show()
    }

    override fun onPermissionResult(granted: Boolean) {
        if (granted) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getStoragePermission()
            } else {
                startActivity()
            }
        } else {
            Toast.makeText(this, "Location permission is required", Toast.LENGTH_SHORT).show()
        }
    }

//
//    @RequiresApi(Build.VERSION_CODES.M)
//    fun getCameraPermission() {
//        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//            requestPermissions(arrayOf(Manifest.permission.CAMERA), cameraPermissionRequest)
//        } else {
//            getStoragePermission()
//        }
//    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == cameraPermissionRequest && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getStoragePermission()
            }
        }

        if (requestCode == storageReadPermissionRequest && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startActivity()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun getStoragePermission() {
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                storageReadPermissionRequest
            )
        } else {
            startActivity()
        }
    }

    fun start() {
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getStoragePermission()
            } else {
                startActivity()
            }
        } else {
            permissionsManager.requestLocationPermissions(this)
        }
    }

    fun startActivity() {
        if (Misc.getPurchasedStatus(this)) {
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            Misc.startActivity(
                this,
                Misc.isSplashIntEnabled,
                object : StartActivityCallBack {
                    override fun onStart() {
                        startActivity(
                            Intent(
                                this@SplashScreenActivity,
                                ProScreenActivity::class.java
                            )
                        )
                    }
                }
            )
        }
    }

    private fun getRemoteConfigValues(){
        FirebaseApp.initializeApp(this)
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(1)
            .build()
        val mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings)
        mFirebaseRemoteConfig.ensureInitialized()
        mFirebaseRemoteConfig.fetch().addOnCompleteListener(this) { p0 ->
            mFirebaseRemoteConfig.activate()

            if (p0.isSuccessful) {
                Misc.isSettingIntEnabled = mFirebaseRemoteConfig.getBoolean("isSettingIntEnabled")
                Misc.isCompassIntEnabled = mFirebaseRemoteConfig.getBoolean("isCompassIntEnabled")
                Misc.isNoteCamIntEnabled = mFirebaseRemoteConfig.getBoolean("isNoteCamIntEnabled")
                Misc.isGameBackIntEnabled = mFirebaseRemoteConfig.getBoolean("isGameBackIntEnabled")
                Misc.isAltitudeIntEnabled = mFirebaseRemoteConfig.getBoolean("isAltitudeIntEnabled")
                Misc.isProScreenIntEnabled = mFirebaseRemoteConfig.getBoolean("isProScreenIntEnabled")
                Misc.isLiveEarthIntEnabled = mFirebaseRemoteConfig.getBoolean("isLiveEarthIntEnabled")
                Misc.isViewWorldIntEnabled = mFirebaseRemoteConfig.getBoolean("isViewWorldIntEnabled")
                Misc.isStartGameIntEnabled = mFirebaseRemoteConfig.getBoolean("isStartGameIntEnabled")
                Misc.isSplashNativeEnabled = mFirebaseRemoteConfig.getBoolean("isSplashNativeEnabled")
                Misc.isSoundMeterIntEnabled = mFirebaseRemoteConfig.getBoolean("isSoundMeterIntEnabled")
                Misc.isGenerateQRIntEnabled = mFirebaseRemoteConfig.getBoolean("isGenerateQRIntEnabled")
                Misc.isNavigationIntEnabled = mFirebaseRemoteConfig.getBoolean("isNavigationIntEnabled")
                Misc.isGPSMapCamsIntEnabled = mFirebaseRemoteConfig.getBoolean("isGPSMapCamsIntEnabled")
                Misc.isSkyMapBackIntEnabled = mFirebaseRemoteConfig.getBoolean("isSkyMapBackIntEnabled")
                Misc.isSettingBackIntEnabled = mFirebaseRemoteConfig.getBoolean("isSettingBackIntEnabled")
                Misc.isCompassBackIntEnabled = mFirebaseRemoteConfig.getBoolean("isCompassBackIntEnabled")
                Misc.isSpeedometerIntEnabled = mFirebaseRemoteConfig.getBoolean("isSpeedometerIntEnabled")
                Misc.isPlayGameBackIntEnabled = mFirebaseRemoteConfig.getBoolean("isPlayGameBackIntEnabled")
                Misc.isAltitudeBackIntEnabled = mFirebaseRemoteConfig.getBoolean("isAltitudeBackIntEnabled")
                Misc.isQuizCompleteIntEnabled = mFirebaseRemoteConfig.getBoolean("isQuizCompleteIntEnabled")
                Misc.isSoundMeterNativeEnabled = mFirebaseRemoteConfig.getBoolean("isSoundMeterNativeEnabled")
                Misc.isQuizScreenOneIntEnabled = mFirebaseRemoteConfig.getBoolean("isQuizScreenOneIntEnabled")
                Misc.isQuizCountriesIntEnabled = mFirebaseRemoteConfig.getBoolean("isQuizCountriesIntEnabled")
                Misc.isProScreenBackIntEnabled = mFirebaseRemoteConfig.getBoolean("isProScreenBackIntEnabled")
                Misc.isViewWorldBackIntEnabled = mFirebaseRemoteConfig.getBoolean("isViewWorldBackIntEnabled")
                Misc.isNoteCamOnBackIntEnabled = mFirebaseRemoteConfig.getBoolean("isNoteCamOnBackIntEnabled")
                Misc.isStartGameBackIntEnabled = mFirebaseRemoteConfig.getBoolean("isStartGameBackIntEnabled")
                Misc.isSoundMeterBackIntEnabled = mFirebaseRemoteConfig.getBoolean("isSoundMeterBackIntEnabled")
                Misc.isQuizSelectModeIntEnabled = mFirebaseRemoteConfig.getBoolean("isQuizSelectModeIntEnabled")
                Misc.isNavigationBackIntEnabled = mFirebaseRemoteConfig.getBoolean("isNavigationBackIntEnabled")
                Misc.isQuizCurrenciesIntEnabled = mFirebaseRemoteConfig.getBoolean("isQuizCurrenciesIntEnabled")
                Misc.isSpeedometerNativeEnabled = mFirebaseRemoteConfig.getBoolean("isSpeedometerNativeEnabled")
                Misc.isSearchLocationIntEnabled = mFirebaseRemoteConfig.getBoolean("isSearchLocationIntEnabled")
                Misc.isContinentSelectIntEnabled = mFirebaseRemoteConfig.getBoolean("isContinentSelectIntEnabled")
                Misc.isQuizCompleteNativeEnabled = mFirebaseRemoteConfig.getBoolean("isQuizCompleteNativeEnabled")
                Misc.isQuizActivitySplashEnabled = mFirebaseRemoteConfig.getBoolean("isQuizActivitySplashEnabled")
                Misc.isSpeedometerBackIntEnabled = mFirebaseRemoteConfig.getBoolean("isSpeedometerBackIntEnabled")
                Misc.isLiveEarthOnBackIntEnabled = mFirebaseRemoteConfig.getBoolean("isLiveEarthOnBackIntEnabled")
                Misc.isQuizCompleteBackIntEnabled = mFirebaseRemoteConfig.getBoolean("isQuizCompleteBackIntEnabled")
                Misc.isQuizScreenOneNativeEnabled = mFirebaseRemoteConfig.getBoolean("isQuizScreenOneNativeEnabled")
                Misc.isGenerateQrOnBackIntEnabled = mFirebaseRemoteConfig.getBoolean("isGenerateQrOnBackIntEnabled")
                Misc.isQuizScreenOneBackIntEnabled = mFirebaseRemoteConfig.getBoolean("isQuizScreenOneBackIntEnabled")
                Misc.isQuizSelectModeNativeEnabled = mFirebaseRemoteConfig.getBoolean("isQuizSelectModeNativeEnabled")
                Misc.isMainFromProScreenIntEnabled = mFirebaseRemoteConfig.getBoolean("isMainFromProScreenIntEnabled")
                Misc.isContinentSelectNativeEnabled = mFirebaseRemoteConfig.getBoolean("isContinentSelectNativeEnabled")
                Misc.isGenerateQrOnBackNativeEnabled = mFirebaseRemoteConfig.getBoolean("isGenerateQrOnBackNativeEnabled")
                Misc.isContinentSelectBackIntEnabled = mFirebaseRemoteConfig.getBoolean("isContinentSelectBackIntEnabled")

                mFirebaseRemoteConfig.reset()

                if (Misc.nativeAdId != "" && !isAdRequestSent) {
                    isAdRequestSent = true

                    Misc.loadInterstitial(this, Misc.interstitialAdId)

                    Misc.loadNativeAd(
                        this,
                        Misc.nativeAdId,
                        object : NativeAdCallBack {
                            override fun onLoad() {
                                Misc.showNativeAd(
                                    this@SplashScreenActivity,
                                    nativeAdViewSplash,
                                    Misc.isSplashNativeEnabled,
                                    object : NativeAdCallBack {
                                        override fun onLoad() {
                                            nativeAdViewSplash.visibility = View.VISIBLE
                                            Misc.zoomInView(
                                                nativeAdViewSplash,
                                                this@SplashScreenActivity,
                                                300
                                            )
                                            Misc.zoomOutView(
                                                animLoading,
                                                this@SplashScreenActivity,
                                                300
                                            )
                                            Misc.showView(btnStart, this@SplashScreenActivity, false)
                                        }
                                    }
                                )
//                        showStartButton()
                            }
                        }
                    )
                    Log.e(Misc.logKey, "Values fetched.")
                }
            } else {
                Log.d(Misc.logKey, p0.exception.toString())
            }
        }
    }
}