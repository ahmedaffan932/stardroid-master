package com.liveearth.android.map

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.liveearth.android.map.clasess.Misc
import com.liveearth.android.map.interfaces.*
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import kotlinx.android.synthetic.fdroid.bottom_sheet_quit.*
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), PermissionsListener {
    private val cameraPermissionRequest = 100
    private val cameraPermissionRequestForGPSCam = 10
    private lateinit var permissionsManager: PermissionsManager
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private val micPermissionRequest = 1032
    private val lsvStoragePermission = 101
    private val gpsMapCamStoragePermission = 93
    private val noteCamStoragePermission = 936
    private val altitudeStoragePermission = 989
    private lateinit var myIntent: Intent
    private var isNativeDisplayed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_main)

        permissionsManager = PermissionsManager(this)
        bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.quitBottomSheet))
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

        quitBottomSheet.setOnClickListener { }

//        Misc.showMREC(this,adFrameLayout, Misc.isDashboardMRecEnabled)

        btnPro.setOnClickListener {
            val intent = Intent(this@MainActivity, ProScreenActivity::class.java)
            intent.putExtra(Misc.data, Misc.data)
            startActivity(intent)
        }

        btnMenu.setOnClickListener {
            startActivity(Intent(this@MainActivity, SettingsActivity::class.java))
        }

        llSoundMeter.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(
                        arrayOf(Manifest.permission.RECORD_AUDIO),
                        micPermissionRequest
                    )
                } else {
                    val intent =
                        Intent(this@MainActivity, SoundMeterActivity::class.java)
                    startMyActivity(intent, Misc.soundMeterIntAm_al)
                }
            } else {
                val intent =
                    Intent(this@MainActivity, SoundMeterActivity::class.java)
                startMyActivity(intent, Misc.soundMeterIntAm_al)
            }

        }

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    blockOnClickMain.visibility = View.VISIBLE
                } else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    blockOnClickMain.visibility = View.GONE
                }
            }
        })

        blockOnClickMain.setOnClickListener {
            blockOnClickMain.visibility = View.GONE
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        btnYes.setOnClickListener {
            finishAffinity()
        }

        btnNo.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }


        llSkyMap.setOnClickListener {
            val intent = Intent(this@MainActivity, SkyMapActivity::class.java)
            startMyActivity(intent, Misc.skyMapIntAm_al)
        }

        llWorldQuiz.setOnClickListener {
            startMyActivity(Intent(this@MainActivity, WorldQuizActivity::class.java), Misc.worldQuizIntAm_al)
        }

        llSpeedometer.setOnClickListener {
            val intent = Intent(this@MainActivity, SpeedometerActivity::class.java)
            startMyActivity(intent, Misc.speedometerIntAm_al)
        }

        llGPSMapCams.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(
                        arrayOf(Manifest.permission.CAMERA),
                        cameraPermissionRequestForGPSCam
                    )
                } else {
                    Misc.getStoragePermission(
                        this,
                        gpsMapCamStoragePermission,
                        object : StoragePermissionInterface {
                            override fun onPermissionGranted() {
                                val intent = Intent(
                                    this@MainActivity,
                                    NoteCamActivity::class.java
                                )
                                intent.putExtra(Misc.data, Misc.data)
                                startMyActivity(intent, Misc.noteCamIntAm_al)
                            }
                        })

                }
            } else {
                Misc.getStoragePermission(
                    this,
                    gpsMapCamStoragePermission,
                    object : StoragePermissionInterface {
                        override fun onPermissionGranted() {
                            val intent =
                                Intent(this@MainActivity, NoteCamActivity::class.java)
                            intent.putExtra(Misc.data, Misc.data)
                            startMyActivity(intent, Misc.gpsCamIntAm_al)
                        }
                    })
            }

        }

        llAltitude.setOnClickListener {
            Misc.getStoragePermission(
                this,
                altitudeStoragePermission,
                object : StoragePermissionInterface {
                    override fun onPermissionGranted() {
                        startMyActivity(
                            Intent(this@MainActivity, AltitudeActivity::class.java), Misc.altitudeIntAm_al)
                    }
                })
        }
        llCompass.setOnClickListener {
            startMyActivity(Intent(this@MainActivity, CompassActivity::class.java), Misc.compassIntAm_al)
        }

        llLiveEarthMap.setOnClickListener {
            Misc.getStoragePermission(
                this,
                lsvStoragePermission,
                object : StoragePermissionInterface {
                    override fun onPermissionGranted() {
                        startMyActivity(
                            Intent(this@MainActivity, LiveEarthActivity::class.java), Misc.lsvIntAm_al)
                    }
                }
            )
        }

        llNoteCam.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getCameraPermission()
            } else {
                Misc.getStoragePermission(
                    this,
                    noteCamStoragePermission,
                    object : StoragePermissionInterface {
                        override fun onPermissionGranted() {
                            startMyActivity(
                                Intent(
                                    this@MainActivity,
                                    NoteCamActivity::class.java
                                ), Misc.noteCamIntAm_al)
                        }
                    })
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun getCameraPermission() {
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), cameraPermissionRequest)
        } else {
            Misc.getStoragePermission(
                this,
                noteCamStoragePermission,
                object : StoragePermissionInterface {
                    override fun onPermissionGranted() {
                        startMyActivity(
                            Intent(
                                this@MainActivity,
                                NoteCamActivity::class.java
                            ), Misc.noteCamIntAm_al)
                    }
                })
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults)

        try {
            if (requestCode == lsvStoragePermission && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startMyActivity(
                    Intent(
                        this@MainActivity,
                        LiveEarthActivity::class.java
                    ),Misc.lsvIntAm_al)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (requestCode == gpsMapCamStoragePermission && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            val intent = Intent(this@MainActivity, NoteCamActivity::class.java)
            intent.putExtra(Misc.data, Misc.data)
            startMyActivity(intent, Misc.gpsCamIntAm_al)
        }

        if (requestCode == noteCamStoragePermission && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startMyActivity(Intent(this@MainActivity, NoteCamActivity::class.java), Misc.noteCamIntAm_al)
        }

        if (requestCode == altitudeStoragePermission && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startMyActivity(
                Intent(
                    this@MainActivity,
                    AltitudeActivity::class.java
                ),Misc.altitudeIntAm_al)
        }

        if (requestCode == cameraPermissionRequestForGPSCam && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Misc.getStoragePermission(
                this,
                gpsMapCamStoragePermission,
                object : StoragePermissionInterface {
                    override fun onPermissionGranted() {
                        val intent =
                            Intent(this@MainActivity, NoteCamActivity::class.java)
                        intent.putExtra(Misc.data, Misc.data)
                        startMyActivity(intent, Misc.gpsCamIntAm_al)
                    }
                })
        }

        if (requestCode == cameraPermissionRequest && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Misc.getStoragePermission(
                this@MainActivity,
                noteCamStoragePermission,
                object : StoragePermissionInterface {
                    override fun onPermissionGranted() {
                        startMyActivity(
                            Intent(
                                this@MainActivity,
                                NoteCamActivity::class.java
                            ), Misc.noteCamIntAm_al)
                    }
                })
        }

        if (requestCode == micPermissionRequest) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val intent =
                    Intent(this@MainActivity, SoundMeterActivity::class.java)
                startMyActivity(intent, Misc.soundMeterIntAm_al)
            } else {
                Toast.makeText(
                    this,
                    "Recording Permission is required for Sound Meter",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    }

    override fun onExplanationNeeded(permissionsToExplain: MutableList<String>?) {
        Toast.makeText(this, R.string.user_location_permission_explanation, Toast.LENGTH_LONG)
            .show()
    }

    override fun onPermissionResult(granted: Boolean) {
        if (granted) {
            startMyActivity(myIntent, Misc.lsvIntAm_al)
        } else {
            Toast.makeText(this, "Location permission is required", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onBackPressed() {
        if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            return
        } else {
            Misc.showInterstitial(this, Misc.isQuitIntEnabled, object : InterstitialCallBack {
                override fun onDismiss() {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                    Misc.showNativeAd(this@MainActivity, nativeAd, Misc.isQuitNativeEnabled, null)
                }

            })
        }
    }

    fun startMyActivity(intent: Intent, isEnabled: String) {
        myIntent = intent
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            Misc.showAmAlInterstitial(
                this,
                isEnabled,
                object : InterstitialCallBack {
                    override fun onDismiss() {
                        startActivity(intent)
                    }
                })
        } else {
            permissionsManager.requestLocationPermissions(this)
        }

    }

    override fun onResume() {
        super.onResume()
//        val bannerAdFrameLayout = if(Misc.isBannerAdTop){
//            bannerAdFrameLayoutTop
//        }else{
//            bannerAdFrameLayoutBottom
//        }
        Misc.showBannerAd(Misc.isDashboardBannerEnabled, bannerAdFrameLayoutTop)
        if (!isNativeDisplayed) {
            if (Misc.mNativeAdAdMob != null) {
                Misc.showAdMobNativeAd(
                    this,
                    nativeAdViewMain,
                    object : NativeAdCallBack {
                        override fun onLoad() {
                            nativeAdViewMain.visibility = View.VISIBLE
                            llAd.visibility = View.VISIBLE
                            isNativeDisplayed = true
                        }
                    }
                )
            } else {
                if (Misc.dashboardNativeAm_al == "am_al" || Misc.dashboardNativeAm_al == "al")
                    Misc.showNativeAd(this, adFrameLayoutNative, true, object : NativeAdCallBack {
                        override fun onLoad() {
                            adFrameLayoutNative.visibility = View.VISIBLE
                            llAd.visibility = View.VISIBLE
                            isNativeDisplayed = true
                        }
                    })
            }
        }
    }
}