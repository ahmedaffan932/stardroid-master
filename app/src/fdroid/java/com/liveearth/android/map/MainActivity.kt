package com.liveearth.android.map

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.style.ImageSpan
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.liveearth.android.map.AltitudeActivity
import com.liveearth.android.map.clasess.Misc
import com.liveearth.android.map.interfaces.StartActivityCallBack
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (intent.getStringExtra(Misc.data) != null) {
            setContentView(R.layout.activity_main_seconde_design)
        } else {
            setContentView(R.layout.activity_main)
        }

        permissionsManager = PermissionsManager(this)
        bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.quitBottomSheet))
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

        btnPro.setOnClickListener {
            Misc.startActivity(this, Misc.isProScreenIntEnabled, object : StartActivityCallBack{
                override fun onStart() {
                    val intent = Intent(this@MainActivity, ProScreenActivity::class.java)
                    intent.putExtra(Misc.data, Misc.data)
                    startActivity(intent)
                }
            })
        }

        btnMenu.setOnClickListener {
            Misc.startActivity(this, Misc.isSettingIntEnabled, object : StartActivityCallBack{
                override fun onStart() {
                    startActivity(Intent(this@MainActivity, SettingsActivity::class.java))
                }
            })
        }

        llSoundMeter.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(
                        arrayOf(Manifest.permission.RECORD_AUDIO),
                        micPermissionRequest
                    )
                } else {
                    Misc.startActivity(
                        this,
                        Misc.isSoundMeterIntEnabled,
                        object : StartActivityCallBack {
                            override fun onStart() {
                                val intent =
                                    Intent(this@MainActivity, SoundMeterActivity::class.java)
                                startActivity(intent)
                            }
                        })
                }
            } else {
                Misc.startActivity(
                    this,
                    Misc.isSoundMeterIntEnabled,
                    object : StartActivityCallBack {
                        override fun onStart() {
                            val intent =
                                Intent(this@MainActivity, SoundMeterActivity::class.java)
                            startActivity(intent)
                        }
                    }
                )
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
            Misc.startActivity(this, Misc.isSkyMapIntEnabled, object : StartActivityCallBack {
                override fun onStart() {
                    val intent = Intent(this@MainActivity, SkyMapActivity::class.java)
                    startActivity(intent)
                }
            })
        }

        findViewById<LinearLayout>(R.id.llWorldQuiz).setOnClickListener {
            Misc.startActivity(this, Misc.isGameIntEnabled, object : StartActivityCallBack {
                override fun onStart() {
                    val intent = Intent(this@MainActivity, WorldQuizActivity::class.java)
                    startActivity(intent)
                }
            })
        }

        llSpeedometer.setOnClickListener {
            Misc.startActivity(this, Misc.isSpeedometerIntEnabled, object : StartActivityCallBack {
                override fun onStart() {
                    val intent = Intent(this@MainActivity, SpeedometerActivity::class.java)
                    startActivity(intent)
                }
            })
        }

        llGPSMapCams.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(arrayOf(Manifest.permission.CAMERA), cameraPermissionRequestForGPSCam)
                } else {
                    Misc.startActivity(this, Misc.isGPSMapCamsIntEnabled, object : StartActivityCallBack {
                        override fun onStart() {
                            val intent = Intent(this@MainActivity, NoteCamActivity::class.java)
                            intent.putExtra(Misc.data, Misc.data)
                            startActivity(intent)
                        }
                    })
                }
            } else {
                Misc.startActivity(this, Misc.isGPSMapCamsIntEnabled, object : StartActivityCallBack {
                    override fun onStart() {
                        val intent = Intent(this@MainActivity, NoteCamActivity::class.java)
                        intent.putExtra(Misc.data, Misc.data)
                        startActivity(intent)
                    }
                })
            }

        }

        llAltitude.setOnClickListener {
            Misc.startActivity(this, Misc.isAltitudeIntEnabled, object : StartActivityCallBack {
                override fun onStart() {
                    startActivity(
                        Intent(
                            this@MainActivity,
                            com.liveearth.android.map.AltitudeActivity::class.java
                        )
                    )
                }
            })
        }
        llCompass.setOnClickListener {
            Misc.startActivity(this, Misc.isCompassIntEnabled, object : StartActivityCallBack {
                override fun onStart() {
                    startActivity(Intent(this@MainActivity, CompassActivity::class.java))
                }
            })
        }

        llLiveEarthMap.setOnClickListener {
            if (PermissionsManager.areLocationPermissionsGranted(this)) {
                Misc.startActivity(this, Misc.isLiveEarthIntEnabled, object :
                    StartActivityCallBack {
                    override fun onStart() {
                        startActivity(Intent(this@MainActivity, LiveEarthActivity::class.java))
                    }
                })
            } else {
                permissionsManager.requestLocationPermissions(this)
            }
        }

        llNoteCam.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getCameraPermission()
            } else {
                Misc.startActivity(this, Misc.isNoteCamIntEnabled, object : StartActivityCallBack {
                    override fun onStart() {
                        startActivity(Intent(this@MainActivity, NoteCamActivity::class.java))
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
            Misc.startActivity(this, Misc.isNoteCamIntEnabled, object : StartActivityCallBack {
                override fun onStart() {
                    startActivity(Intent(this@MainActivity, NoteCamActivity::class.java))
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
        if (requestCode == cameraPermissionRequestForGPSCam && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Misc.startActivity(this, Misc.isGPSMapCamsIntEnabled, object : StartActivityCallBack {
                override fun onStart() {
                    val intent = Intent(this@MainActivity, NoteCamActivity::class.java)
                    intent.putExtra(Misc.data, Misc.data)
                    startActivity(intent)
                }
            })
        }

        if (requestCode == cameraPermissionRequest && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Misc.startActivity(this, Misc.isNoteCamIntEnabled, object : StartActivityCallBack {
                override fun onStart() {
                    startActivity(Intent(this@MainActivity, NoteCamActivity::class.java))
                }
            })
        }

        if (requestCode == micPermissionRequest) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Misc.startActivity(
                    this,
                    Misc.isSoundMeterIntEnabled,
                    object : StartActivityCallBack {
                        override fun onStart() {
                            val intent =
                                Intent(this@MainActivity, SoundMeterActivity::class.java)
                            startActivity(intent)
                        }
                    }
                )
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
            Misc.startActivity(this, Misc.isLiveEarthIntEnabled, object : StartActivityCallBack {
                override fun onStart() {
                    startActivity(Intent(this@MainActivity, LiveEarthActivity::class.java))
                }
            })
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
}