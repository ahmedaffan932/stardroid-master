package com.liveearth.android.map

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PurchasesUpdatedListener
import com.blongho.country_data.World
import com.google.android.gms.ads.MobileAds
import com.google.firebase.FirebaseApp
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.liveearth.android.map.clasess.Misc
import com.liveearth.android.map.interfaces.NativeAdCallBack
import com.liveearth.android.map.interfaces.StartActivityCallBack
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import kotlinx.android.synthetic.main.activity_splash_screen.*

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : BaseActivity(), PermissionsListener {
    private var isAdRequestSent: Boolean = false
    private lateinit var permissionsManager: PermissionsManager
    private val purchasesUpdatedListener =
        PurchasesUpdatedListener { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                Misc.setPurchasedStatus(this, true)
                Log.d(Misc.logKey, "Ya hooo.....")
                Toast.makeText(this, "Restarting Application.", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, SplashScreenActivity::class.java))
                finish()
            }
        }

    private lateinit var billingClient: BillingClient

    @SuppressLint("LogNotTimber")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        billingClient = BillingClient.newBuilder(this)
            .setListener(purchasesUpdatedListener)
            .enablePendingPurchases()
            .build()


        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {

                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    billingClient.queryPurchaseHistoryAsync(BillingClient.SkuType.INAPP) { p0, p1 ->
                        Log.d(Misc.logKey, p1?.size.toString() + " size ..")
                        if (/*p0.responseCode == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED
                            &&*/ p1 != null
                        ) {
                            for (purchase in p1) {
                                Misc.setPurchasedStatus(this@SplashScreenActivity, true)
                            }
                        }
                    }
                    Log.d(Misc.logKey, "Billing Result Ok")
                }
            }

            override fun onBillingServiceDisconnected() {
                Log.d(Misc.logKey, "Service disconnected")
            }
        })

        FirebaseApp.initializeApp(this)

        getRemoteConfigValues()

        permissionsManager = PermissionsManager(this)
        World.init(this)
        FirebaseApp.initializeApp(applicationContext)

        MobileAds.initialize(this) {}

        btnStart.setOnClickListener {
            start()
        }

        Handler().postDelayed({
            if (btnStart.visibility != View.VISIBLE) {
                Misc.zoomInView(btnStart, this@SplashScreenActivity, 300)
                Misc.zoomOutView(
                    animLoading,
                    this@SplashScreenActivity,
                    300
                )
            }
        }, 3000)


    }

    override fun onExplanationNeeded(permissionsToExplain: MutableList<String>?) {
        Toast.makeText(this, R.string.user_location_permission_explanation, Toast.LENGTH_LONG)
            .show()
    }

    override fun onPermissionResult(granted: Boolean) {
        if (granted) {
            startMainActivity()
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

    fun start() {
        if (!PermissionsManager.areLocationPermissionsGranted(this)) {
            Misc.startActivity(
                this,
                Misc.isSplashIntEnabled,
                object : StartActivityCallBack {
                    override fun onStart() {
                        startActivity(
                            Intent(
                                this@SplashScreenActivity,
                                PermissionsActivity::class.java
                            )
                        )
                    }
                }
            )
        } else {
            startMainActivity()
        }
    }

    fun startMainActivity() {
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
                                MainActivity::class.java
                            )
                        )
                    }
                }
            )
        }
    }

    @SuppressLint("LogNotTimber")
    private fun getRemoteConfigValues(): Boolean {
        return try {
            val mFirebaseRemoteConfig = Firebase.remoteConfig
            val configSettings = remoteConfigSettings {
                minimumFetchIntervalInSeconds = if (BuildConfig.DEBUG) 0 else 3600
            }
            mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings)
            mFirebaseRemoteConfig.fetchAndActivate()
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val updated = task.result
                        Log.d(Misc.logKey, "Fetch Config params updated: $updated")

                        mFirebaseRemoteConfig.activate()

                        Misc.bannerAdId = mFirebaseRemoteConfig.getString("bannerAdId")
                        Misc.nativeAdId = mFirebaseRemoteConfig.getString("nativeAdId")
                        Misc.lsvBannerAdId = mFirebaseRemoteConfig.getString("lsvBannerAdId")
                        Misc.interstitialAdId = mFirebaseRemoteConfig.getString("interstitialAdId")
                        Misc.isSkyMapIntEnabled = mFirebaseRemoteConfig.getBoolean("isSkyMapIntEnabled")
                        Misc.isLSVBannerEnabled = mFirebaseRemoteConfig.getBoolean("isLSVBannerEnabled")
                        Misc.isSplashIntEnabled = mFirebaseRemoteConfig.getBoolean("isSettingIntEnabled")
                        Misc.isSettingIntEnabled = mFirebaseRemoteConfig.getBoolean("isSettingIntEnabled")
                        Misc.isCompassIntEnabled = mFirebaseRemoteConfig.getBoolean("isCompassIntEnabled")
                        Misc.isNoteCamIntEnabled = mFirebaseRemoteConfig.getBoolean("isNoteCamIntEnabled")
                        Misc.isBtnClickIntEnable = mFirebaseRemoteConfig.getBoolean("isBtnClickIntEnable")
                        Misc.isGameBackIntEnabled = mFirebaseRemoteConfig.getBoolean("isGameBackIntEnabled")
                        Misc.isAltitudeIntEnabled = mFirebaseRemoteConfig.getBoolean("isAltitudeIntEnabled")
                        Misc.isProScreenIntEnabled = mFirebaseRemoteConfig.getBoolean("isProScreenIntEnabled")
                        Misc.isLiveEarthIntEnabled = mFirebaseRemoteConfig.getBoolean("isLiveEarthIntEnabled")
                        Misc.isViewWorldIntEnabled = mFirebaseRemoteConfig.getBoolean("isViewWorldIntEnabled")
                        Misc.isStartGameIntEnabled = mFirebaseRemoteConfig.getBoolean("isStartGameIntEnabled")
                        Misc.isSplashNativeEnabled = mFirebaseRemoteConfig.getBoolean("isSplashNativeEnabled")
                        Misc.isSoundMeterIntEnabled = mFirebaseRemoteConfig.getBoolean("isSoundMeterIntEnabled")
                        Misc.isGenerateQRIntEnabled = mFirebaseRemoteConfig.getBoolean("isGenerateQRIntEnabled")
                        Misc.isNavigationIntEnabled = mFirebaseRemoteConfig.getBoolean("isNavigationIntEnabled")
                        Misc.isGPSMapCamsIntEnabled = mFirebaseRemoteConfig.getBoolean("isGPSMapCamsIntEnabled")
                        Misc.isSkyMapBackIntEnabled = mFirebaseRemoteConfig.getBoolean("isSkyMapBackIntEnabled")
                        Misc.isSettingBackIntEnabled = mFirebaseRemoteConfig.getBoolean("isSettingBackIntEnabled")
                        Misc.isCompassBackIntEnabled = mFirebaseRemoteConfig.getBoolean("isCompassBackIntEnabled")
                        Misc.isSpeedometerIntEnabled = mFirebaseRemoteConfig.getBoolean("isSpeedometerIntEnabled")
                        Misc.isPlayGameBackIntEnabled = mFirebaseRemoteConfig.getBoolean("isPlayGameBackIntEnabled")
                        Misc.isAltitudeBackIntEnabled = mFirebaseRemoteConfig.getBoolean("isAltitudeBackIntEnabled")
                        Misc.isDashboardNativeEnabled = mFirebaseRemoteConfig.getBoolean("isDashboardNativeEnabled")
                        Misc.isQuizCompleteIntEnabled = mFirebaseRemoteConfig.getBoolean("isQuizCompleteIntEnabled")
                        Misc.isSoundMeterNativeEnabled = mFirebaseRemoteConfig.getBoolean("isSoundMeterNativeEnabled")
                        Misc.isQuizScreenOneIntEnabled = mFirebaseRemoteConfig.getBoolean("isQuizScreenOneIntEnabled")
                        Misc.isQuizCountriesIntEnabled = mFirebaseRemoteConfig.getBoolean("isQuizCountriesIntEnabled")
                        Misc.isProScreenBackIntEnabled = mFirebaseRemoteConfig.getBoolean("isProScreenBackIntEnabled")
                        Misc.isViewWorldBackIntEnabled = mFirebaseRemoteConfig.getBoolean("isViewWorldBackIntEnabled")
                        Misc.isNoteCamOnBackIntEnabled = mFirebaseRemoteConfig.getBoolean("isNoteCamOnBackIntEnabled")
                        Misc.isStartGameBackIntEnabled = mFirebaseRemoteConfig.getBoolean("isStartGameBackIntEnabled")
                        Misc.isSoundMeterBackIntEnabled = mFirebaseRemoteConfig.getBoolean("isSoundMeterBackIntEnabled")
                        Misc.isQuizSelectModeIntEnabled = mFirebaseRemoteConfig.getBoolean("isQuizSelectModeIntEnabled")
                        Misc.isNavigationBackIntEnabled = mFirebaseRemoteConfig.getBoolean("isNavigationBackIntEnabled")
                        Misc.isQuizCurrenciesIntEnabled = mFirebaseRemoteConfig.getBoolean("isQuizCurrenciesIntEnabled")
                        Misc.isSpeedometerNativeEnabled = mFirebaseRemoteConfig.getBoolean("isSpeedometerNativeEnabled")
                        Misc.isSearchLocationIntEnabled = mFirebaseRemoteConfig.getBoolean("isSearchLocationIntEnabled")
                        Misc.isMainActivityBannerEnabled = mFirebaseRemoteConfig.getBoolean("isMainActivityBannerEnabled")
                        Misc.isContinentSelectIntEnabled = mFirebaseRemoteConfig.getBoolean("isContinentSelectIntEnabled")
                        Misc.isQuizCompleteNativeEnabled = mFirebaseRemoteConfig.getBoolean("isQuizCompleteNativeEnabled")
                        Misc.isQuizActivitySplashEnabled = mFirebaseRemoteConfig.getBoolean("isQuizActivitySplashEnabled")
                        Misc.isSpeedometerBackIntEnabled = mFirebaseRemoteConfig.getBoolean("isSpeedometerBackIntEnabled")
                        Misc.isLiveEarthOnBackIntEnabled = mFirebaseRemoteConfig.getBoolean("isLiveEarthOnBackIntEnabled")
                        Misc.isQuizCompleteBackIntEnabled = mFirebaseRemoteConfig.getBoolean("isQuizCompleteBackIntEnabled")
                        Misc.isQuizScreenOneNativeEnabled = mFirebaseRemoteConfig.getBoolean("isQuizScreenOneNativeEnabled")
                        Misc.isGenerateQrOnBackIntEnabled = mFirebaseRemoteConfig.getBoolean("isGenerateQrOnBackIntEnabled")
                        Misc.isQuizScreenOneBackIntEnabled = mFirebaseRemoteConfig.getBoolean("isQuizScreenOneBackIntEnabled")
                        Misc.isQuizSelectModeNativeEnabled = mFirebaseRemoteConfig.getBoolean("isQuizSelectModeNativeEnabled")
                        Misc.isMainFromProScreenIntEnabled = mFirebaseRemoteConfig.getBoolean("isMainFromProScreenIntEnabled")
                        Misc.isContinentSelectNativeEnabled = mFirebaseRemoteConfig.getBoolean("isContinentSelectNativeEnabled")
                        Misc.isGenerateQrOnBackNativeEnabled = mFirebaseRemoteConfig.getBoolean("isGenerateQrOnBackNativeEnabled")
                        Misc.isContinentSelectBackIntEnabled = mFirebaseRemoteConfig.getBoolean("isContinentSelectBackIntEnabled")


                        if (Misc.nativeAdId != "" && !isAdRequestSent) {
                            isAdRequestSent = true

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
                                                    Misc.showView(
                                                        btnStart,
                                                        this@SplashScreenActivity,
                                                        false
                                                    )
                                                }
                                            }
                                        )
                                    }
                                }
                            )
                            mFirebaseRemoteConfig.reset()
                        }
                    } else {
                        Log.d(Misc.logKey, "Fetch failed")
                    }
                }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

}