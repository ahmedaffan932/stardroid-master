package com.liveearth.android.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
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
import com.liveearth.android.map.interfaces.NativeAdCallBack

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : BaseActivity(), PermissionsListener {
    private val storageReadPermissionRequest = 101
    private val cameraPermissionRequest = 100
    private lateinit var permissionsManager: PermissionsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        permissionsManager = PermissionsManager(this)
        World.init(this)
        FirebaseApp.initializeApp(applicationContext)

        MobileAds.initialize(this) {}

        btnStart.setOnClickListener {
            start()
        }

        Handler().postDelayed({
            if (btnStart.visibility != View.VISIBLE)
                Misc.zoomInView(btnStart, this@SplashScreenActivity, 300)
        }, 3000)

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
}