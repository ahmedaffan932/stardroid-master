package com.liveearth.android.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.blongho.country_data.World
import com.liveearth.android.map.clasess.Misc
import com.liveearth.android.map.interfaces.StartActivityCallBack
import com.google.firebase.FirebaseApp

import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import kotlinx.android.synthetic.main.activity_splash_screen.*

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

        btnStart.setOnClickListener {
            start()
        }

        textView.setOnClickListener {
//            start()
            val i = Intent(this, MainActivity::class.java)
            i.putExtra(Misc.data, Misc.data)
            startActivity(i)
        }

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
            Misc.startActivity(this, Misc.isSplashIntEnabled, object : StartActivityCallBack {
                override fun onStart() {
                    startActivity(Intent(this@SplashScreenActivity, ProScreenActivity::class.java))
                }
            })
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
            Misc.startActivity(this, Misc.isSplashIntEnabled, object : StartActivityCallBack {
                override fun onStart() {
                    startActivity(Intent(this@SplashScreenActivity, ProScreenActivity::class.java))
                }
            })
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
            Misc.startActivity(this, Misc.isSplashIntEnabled, object : StartActivityCallBack {
                override fun onStart() {
                    startActivity(Intent(this@SplashScreenActivity, ProScreenActivity::class.java))
                }
            })
        }
    }

    fun start() {
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getStoragePermission()
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
                    })
            }
        } else {
            permissionsManager.requestLocationPermissions(this)
        }

    }
}