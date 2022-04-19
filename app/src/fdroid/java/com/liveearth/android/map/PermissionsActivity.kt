package com.liveearth.android.map

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import kotlinx.android.synthetic.fdroid.activity_permissions.*

class PermissionsActivity : AppCompatActivity(), PermissionsListener {
    private lateinit var permissionsManager: PermissionsManager

   override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_permissions)
        permissionsManager = PermissionsManager(this)
        btnAllow.setOnClickListener {
            if (PermissionsManager.areLocationPermissionsGranted(this)) {
                startActivity(Intent(this, ProScreenActivity::class.java))
            }else{
                permissionsManager.requestLocationPermissions(this)
            }
        }
    }

    override fun onExplanationNeeded(permissionsToExplain: MutableList<String>?) {
        Toast.makeText(this, R.string.user_location_permission_explanation, Toast.LENGTH_LONG)
            .show()
    }

    override fun onPermissionResult(granted: Boolean) {
        if (granted) {
            startActivity(Intent(this, ProScreenActivity::class.java))
        } else {
            Toast.makeText(this, "Location permission is required", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}