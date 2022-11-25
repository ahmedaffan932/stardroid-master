package com.liveearth.android.map

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.liveearth.android.map.clasess.Ads
import com.liveearth.android.map.clasess.CustomDialog
import com.liveearth.android.map.clasess.Misc
import com.liveearth.android.map.interfaces.*
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import kotlinx.android.synthetic.fdroid.bottom_sheet_quit.*
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), PermissionsListener {
    private lateinit var myIntent: Intent
    private var isNativeDisplayed = false
    private val lsvStoragePermission = 101
    private val micPermissionRequest = 1032
    private val cameraPermissionRequest = 100
    private val noteCamStoragePermission = 936
    private val altitudeStoragePermission = 989
    private val gpsMapCamStoragePermission = 93
    private val cameraPermissionRequestForGPSCam = 10
    private lateinit var permissionsManager: PermissionsManager
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>

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
        Ads.showMREC(this, adFrameLayout, Misc.isDashboardMRecEnabled)



        Handler().postDelayed({
            val a: Animation =
                AnimationUtils.loadAnimation(this, R.anim.pop_up)
            llLiveEarthMap.startAnimation(a)

            a.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(p0: Animation?) {
                }

                override fun onAnimationEnd(p0: Animation?) {
                    val a1: Animation =
                        AnimationUtils.loadAnimation(this@MainActivity, R.anim.pop_down)
                    llLiveEarthMap.startAnimation(a1)
                }

                override fun onAnimationRepeat(p0: Animation?) {
                }
            })
        }, 1000)

        if (intent.getStringExtra(Misc.data) != null)
            if (Misc.isSplashIntAm_al.contains("am") || Misc.isSplashIntAm_al.contains("al")) {
                val objDialog = CustomDialog(this)
                objDialog.setCancelable(false)
                objDialog.setCanceledOnTouchOutside(false)
                objDialog.window?.setBackgroundDrawableResource(R.color.nothing)
                objDialog.show()
                Handler().postDelayed({
                    objDialog.dismiss()
                    Ads.showInterstitial(this@MainActivity, Misc.isSplashIntAm_al, null)
                }, 2000)
            }

        btnPro.setOnClickListener {
            val intent = Intent(this@MainActivity, PremiumScreenActivity::class.java)
            intent.putExtra(Misc.data, Misc.data)
            Firebase.analytics.logEvent("ProScreen", null)
            startActivity(intent)
        }

        btnMenu.setOnClickListener {
            startActivity(Intent(this@MainActivity, SettingsActivity::class.java))
        }

        llSoundMeter.setOnClickListener {
            Firebase.analytics.logEvent("SoundMeter", null)
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
            Firebase.analytics.logEvent("SkyMap", null)

            startMyActivity(intent, Misc.skyMapIntAm_al)
        }

        llWorldQuiz.setOnClickListener {
            Firebase.analytics.logEvent("WorldMapQuiz", null)
            startMyActivity(
                Intent(this@MainActivity, WorldQuizActivity::class.java),
                Misc.worldQuizIntAm_al
            )
        }

        llSpeedometer.setOnClickListener {
            Firebase.analytics.logEvent("Speedometer", null)
            val intent = Intent(this@MainActivity, SpeedometerActivity::class.java)
            startMyActivity(intent, Misc.speedometerIntAm_al)
        }

        llGPSMapCams.setOnClickListener {
            Firebase.analytics.logEvent("GPSMapCams", null)
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
            Firebase.analytics.logEvent("Altitude", null)
            Misc.getStoragePermission(
                this,
                altitudeStoragePermission,
                object : StoragePermissionInterface {
                    override fun onPermissionGranted() {
                        startMyActivity(
                            Intent(this@MainActivity, AltitudeActivity::class.java),
                            Misc.altitudeIntAm_al
                        )
                    }
                })
        }
        llCompass.setOnClickListener {
            Firebase.analytics.logEvent("llCompass", null)
            startMyActivity(
                Intent(this@MainActivity, CompassActivity::class.java),
                Misc.compassIntAm_al
            )
        }

        llLiveEarthMap.setOnClickListener {
            Firebase.analytics.logEvent("LiveEarthMap", null)
            Misc.getStoragePermission(
                this,
                lsvStoragePermission,
                object : StoragePermissionInterface {
                    override fun onPermissionGranted() {
                        startMyActivity(
                            Intent(this@MainActivity, LiveEarthActivity::class.java),
                            Misc.lsvIntAm_al
                        )
                    }
                }
            )
        }

        llNoteCam.setOnClickListener {
            Firebase.analytics.logEvent("NoteCam", null)
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
                                ), Misc.noteCamIntAm_al
                            )
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
                            ), Misc.noteCamIntAm_al
                        )
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

        if (grantResults.isEmpty()) {
            Toast.makeText(this, "Please give required permission.", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            if (requestCode == lsvStoragePermission && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startMyActivity(
                    Intent(
                        this@MainActivity,
                        LiveEarthActivity::class.java
                    ), Misc.lsvIntAm_al
                )
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
            startMyActivity(
                Intent(this@MainActivity, NoteCamActivity::class.java),
                Misc.noteCamIntAm_al
            )
        }

        if (requestCode == altitudeStoragePermission && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startMyActivity(
                Intent(
                    this@MainActivity,
                    AltitudeActivity::class.java
                ), Misc.altitudeIntAm_al
            )
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
                            ), Misc.noteCamIntAm_al
                        )
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
            Ads.showInterstitial(this, Misc.isQuitIntAm_Al, object : InterstitialCallBack {
                override fun onDismiss() {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                    Ads.showNativeAd(this@MainActivity, nativeAd, Misc.quitNativeAm_Al, null)
                }

            })
        }
    }

    fun startMyActivity(intent: Intent, isEnabled: String) {
        myIntent = intent
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            Ads.showInterstitial(
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
        Ads.showBannerAd(Misc.isDashboardBannerEnabled, bannerAdFrameLayoutTop)
        if (!isNativeDisplayed) {
            Ads.showNativeAd(
                this,
                adFrameLayoutNative,
                Misc.dashboardNativeAm_Al,
                object : NativeAdCallBack {
                    override fun onLoad() {
                        adFrameLayoutNative.visibility = View.VISIBLE
                        llAd.visibility = View.VISIBLE
                        isNativeDisplayed = true
                    }
                }
            )
        }
    }
}