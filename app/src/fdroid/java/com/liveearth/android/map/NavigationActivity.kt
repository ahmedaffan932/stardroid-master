package com.liveearth.android.map

import android.os.Bundle
import android.os.PersistableBundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.liveearth.android.map.clasess.Misc
import com.liveearth.android.map.interfaces.OnBackPressCallBack
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.services.android.navigation.ui.v5.NavigationViewOptions
import com.mapbox.services.android.navigation.ui.v5.OnNavigationReadyCallback
import com.mapbox.services.android.navigation.ui.v5.listeners.NavigationListener
import kotlinx.android.synthetic.fdroid.activity_navigation.*

class NavigationActivity : AppCompatActivity(), OnNavigationReadyCallback, NavigationListener {


   override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)
        Mapbox.getInstance(this, resources.getString(R.string.mapbox_access_token))
        setContentView(R.layout.activity_navigation)

        navigationView.onCreate(savedInstanceState)
        navigationView.initialize(this)
    }

    override fun onStart() {
        super.onStart()
        if (navigationView != null) {
            navigationView.onStart()
        }
    }

    override fun onResume() {
        super.onResume()
        if (navigationView != null) {
            navigationView.onResume()
        }
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        if (navigationView != null) {
            navigationView.onSaveInstanceState(outState)
        }
    }

    override fun onPause() {
        super.onPause()
        if (navigationView != null) {
            navigationView.onPause()
        }
    }

    override fun onStop() {
        super.onStop()
        navigationView.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (navigationView != null) {
            navigationView.onDestroy()
        }
    }

    override fun onLowMemory() {
        super.onLowMemory()
        if (navigationView != null) {
            navigationView.onLowMemory()
        }
    }

    private fun startNavigation() {
        if (Misc.route == null) {
            return
        }
        val options = NavigationViewOptions.builder()
                .directionsRoute(Misc.route)
                .shouldSimulateRoute(false)
                .navigationListener(this)
                .build()
        navigationView.startNavigation(options)
    }

    override fun onNavigationReady(isRunning: Boolean) {
        startNavigation()
    }

    override fun onCancelNavigation() {
        if (navigationView != null) {
            navigationView.stopNavigation()
        }
        finish()
    }

    override fun onNavigationFinished() {}

    override fun onNavigationRunning() {}

    override fun onBackPressed() {
        if (navigationView != null) {
            navigationView.stopNavigation()
        }
//        Misc.onBackPress(this, Misc.isNavigationBackIntEnabled, object : OnBackPressCallBack {
//            override fun onBackPress() {
//                finish()
//            }
//        })
//        finish()
        super.onBackPressed()
    }
}