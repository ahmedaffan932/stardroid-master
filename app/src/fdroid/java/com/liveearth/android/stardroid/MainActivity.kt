package com.liveearth.android.stardroid

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.liveearth.android.stardroid.clasess.Misc
import com.liveearth.android.stardroid.interfaces.StartActivityCallBack
import com.liveearth.android.stardroid.AltitudeActivity
import com.liveearth.android.stardroid.SpeedometerActivity
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import kotlinx.android.synthetic.fdroid.bottom_sheet_quit.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.llSoundMeter


class MainActivity : AppCompatActivity(), PermissionsListener {
    private val cameraPermissionRequest = 100
    private lateinit var permissionsManager: PermissionsManager
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.quitBottomSheet))
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

        svLocation.setOnClickListener {
            Misc.startActivity(this, Misc.isLiveEarthIntEnabled, object :
                StartActivityCallBack {
                override fun onStart() {
                    val intent = Intent(this@MainActivity, LiveEarthActivity::class.java)
                    intent.putExtra(Misc.data, Misc.data)
                    startActivity(intent)
                }
            })
        }

        llSoundMeter.setOnClickListener {
            Misc.startActivity(this, Misc.isSoundMeterIntEnabled, object : StartActivityCallBack {
                override fun onStart() {
                    val intent = Intent(this@MainActivity, SoundMeterActivity::class.java)
                    startActivity(intent)
                }
            })
        }

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if(newState == BottomSheetBehavior.STATE_EXPANDED){
                    blockOnClickMain.visibility = View.VISIBLE
                }else if(newState == BottomSheetBehavior.STATE_COLLAPSED){
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


        llSkyMap.setOnClickListener{
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

        llSpeedometer.setOnClickListener{
            Misc.startActivity(this, Misc.isSpeedometerIntEnabled, object : StartActivityCallBack {
                override fun onStart() {
                    val intent = Intent(this@MainActivity, SpeedometerActivity::class.java)
                    startActivity(intent)
                }
            })
        }

        llGPSMapCams.setOnClickListener {
            Misc.startActivity(this, Misc.isGPSMapCamsIntEnabled, object : StartActivityCallBack {
                override fun onStart() {
                    val intent = Intent(this@MainActivity, NoteCamActivity::class.java)
                    intent.putExtra(Misc.data, Misc.data)
                    startActivity(intent)
                }
            })
        }

        llAltitude.setOnClickListener {
            Misc.startActivity(this, Misc.isAltitudeIntEnabled, object : StartActivityCallBack {
                override fun onStart() {
                    startActivity(Intent(this@MainActivity, AltitudeActivity::class.java))
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
            }else{
                permissionsManager = PermissionsManager(this)
                permissionsManager.requestLocationPermissions(this)
            }
        }

        llNoteCam.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getCameraPermission()
            }else{
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
        }else{
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
        if(requestCode == cameraPermissionRequest && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            Misc.startActivity(this, Misc.isNoteCamIntEnabled, object : StartActivityCallBack {
                override fun onStart() {
                    startActivity(Intent(this@MainActivity, NoteCamActivity::class.java))
                }
            })
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
        }else {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

        }
    }
}