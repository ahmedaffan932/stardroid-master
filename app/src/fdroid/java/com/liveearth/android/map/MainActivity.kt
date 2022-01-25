package com.liveearth.android.map

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.style.ImageSpan
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.liveearth.android.map.AltitudeActivity
import com.liveearth.android.map.clasess.CustomDialog
import com.liveearth.android.map.clasess.Misc
import com.liveearth.android.map.interfaces.*
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import kotlinx.android.synthetic.fdroid.activity_sound_meter.*
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (intent.getStringExtra(Misc.data) != null) {
            setContentView(R.layout.activity_main_seconde_design)
        } else {
            setContentView(R.layout.activity_main)
        }

        Misc.showNativeAd(
            this,
            nativeAdViewMain,
            object : NativeAdCallBack {
                override fun onLoad() {
                    nativeAdViewMain.visibility = View.VISIBLE
                }
            }
        )

        permissionsManager = PermissionsManager(this)
        bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.quitBottomSheet))
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED


        btnPro.setOnClickListener {
            val intent = Intent(this@MainActivity, ProScreenActivity::class.java)
            intent.putExtra(Misc.data, Misc.data)
            startMyActivity(intent)
        }

        btnMenu.setOnClickListener {
            startMyActivity(Intent(this@MainActivity, SettingsActivity::class.java))
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
                    startMyActivity(intent)
                }
            } else {
                val intent =
                    Intent(this@MainActivity, SoundMeterActivity::class.java)
                startMyActivity(intent)
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
            startMyActivity(intent)
        }

        llWorldQuiz.setOnClickListener {
            startMyActivity(Intent(this@MainActivity, WorldQuizActivity::class.java))
        }

        llSpeedometer.setOnClickListener {
            val intent = Intent(this@MainActivity, SpeedometerActivity::class.java)
            startMyActivity(intent)
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
                                startMyActivity(intent)
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
                            startMyActivity(intent)
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
                            Intent(
                                this@MainActivity,
                                AltitudeActivity::class.java
                            )
                        )
                    }
                })
        }
        llCompass.setOnClickListener {
            startMyActivity(Intent(this@MainActivity, CompassActivity::class.java))
        }

        llLiveEarthMap.setOnClickListener {
            if (PermissionsManager.areLocationPermissionsGranted(this)) {
                Misc.getStoragePermission(
                    this,
                    lsvStoragePermission,
                    object : StoragePermissionInterface {
                        override fun onPermissionGranted() {
                            startMyActivity(
                                Intent(
                                    this@MainActivity,
                                    LiveEarthActivity::class.java
                                )
                            )
                        }
                    }
                )
            } else {
                permissionsManager.requestLocationPermissions(this)
            }
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
                                )
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
                            )
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

        if (requestCode == lsvStoragePermission && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startMyActivity(
                Intent(
                    this@MainActivity,
                    LiveEarthActivity::class.java
                )
            )
        }

        if (requestCode == gpsMapCamStoragePermission && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            val intent = Intent(this@MainActivity, NoteCamActivity::class.java)
            intent.putExtra(Misc.data, Misc.data)
            startMyActivity(intent)
        }

        if (requestCode == noteCamStoragePermission && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startMyActivity(Intent(this@MainActivity, NoteCamActivity::class.java))
        }

        if (requestCode == altitudeStoragePermission && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startMyActivity(
                Intent(
                    this@MainActivity,
                    AltitudeActivity::class.java
                )
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
                        startMyActivity(intent)
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
                            )
                        )
                    }
                })
        }

        if (requestCode == micPermissionRequest) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val intent =
                    Intent(this@MainActivity, SoundMeterActivity::class.java)
                startMyActivity(intent)
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
            Misc.getStoragePermission(
                this,
                lsvStoragePermission,
                object : StoragePermissionInterface {
                    override fun onPermissionGranted() {
                        startMyActivity(
                            Intent(
                                this@MainActivity,
                                LiveEarthActivity::class.java
                            )
                        )
                    }
                }
            )
        } else {
            Toast.makeText(this, "Location permission is required", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onBackPressed() {
        if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            return
        } else {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    fun startMyActivity(intent: Intent) {

        val objCustomDialog = CustomDialog(this)
        objCustomDialog.show()

        val window: Window = objCustomDialog.window!!
        window.setLayout(
            WindowManager.LayoutParams.FILL_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window.setBackgroundDrawableResource(R.color.nothing)
        objCustomDialog.setCancelable(false)

        Misc.loadInterstitial(this, Misc.isDashboardIntEnabled, object : LoadInterstitialCallBack {
            override fun onFailed() {
                objCustomDialog.dismiss()
                startActivity(intent)
            }

            override fun onLoaded() {
                objCustomDialog.dismiss()
                Misc.showInterstitial(this@MainActivity, object : InterstitialCallBack {
                    override fun onDismiss() {
                        startActivity(intent)
                    }
                })
            }
        })
    }
}