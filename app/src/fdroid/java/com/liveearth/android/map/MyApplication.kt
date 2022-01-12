package com.liveearth.android.map

import android.app.Application
import android.content.Context
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.initialization.InitializationStatus
import com.liveearth.android.map.clasess.AppOpenManager

class AdsApp : Application() {
    var appOpenManager: AppOpenManager? = null
    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
        MobileAds.initialize(
            this
        ) { initializationStatus: InitializationStatus? -> }
        appOpenManager = AppOpenManager(this)
    }

    companion object {
        var appContext: Context? = null
            private set
    }
}