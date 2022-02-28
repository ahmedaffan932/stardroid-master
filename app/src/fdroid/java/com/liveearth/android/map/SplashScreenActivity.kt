package com.liveearth.android.map

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PurchasesUpdatedListener
import com.blongho.country_data.World
import com.google.firebase.FirebaseApp
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.liveearth.android.map.clasess.CustomDialog
import com.liveearth.android.map.clasess.Misc
import com.liveearth.android.map.interfaces.InterstitialCallBack
import com.liveearth.android.map.interfaces.LoadInterstitialCallBack
import com.liveearth.android.map.interfaces.NativeAdCallBack
import kotlinx.android.synthetic.main.activity_splash_screen.*

@SuppressLint("CustomSplashScreen", "LogNotTimber")
class SplashScreenActivity : BaseActivity() {
    private var isAdRequestSent: Boolean = false
    private var isAdMobInterstitialLoaded = false

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

//        Misc.loadInterstitial(this, null)

        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    billingClient.queryPurchaseHistoryAsync(BillingClient.SkuType.INAPP) { p0, p1 ->
                        Log.d(Misc.logKey, p1?.size.toString() + " size ..")
                        if (/*p0.responseCode == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED &&*/ p1 != null) {
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

        World.init(this)
        FirebaseApp.initializeApp(applicationContext)

        btnStart.setOnClickListener {
            start()
        }

//        Handler().postDelayed({
//            if (btnStart.visibility != View.VISIBLE) {
//                Misc.zoomInView(btnStart, this@SplashScreenActivity, 300)
//                Misc.zoomOutView(
//                    animLoading,
//                    this@SplashScreenActivity,
//                    300
//                )
//            }
//        }, 5000)

        Handler().postDelayed({
            if (!Misc.isSplashNativeEnabled){
                if(Misc.isDashboardIntEnabled){
                    if(isAdMobInterstitialLoaded){
                        Misc.showAdMobInterstitial(this,object : InterstitialCallBack{
                            override fun onDismiss() {
                                startActivity(Intent(this@SplashScreenActivity, MainActivity::class.java))
                            }
                        })
                    } else{
                        Misc.showInterstitial(this, Misc.isDashboardIntEnabled, object : InterstitialCallBack{
                            override fun onDismiss() {
                                startActivity(Intent(this@SplashScreenActivity, MainActivity::class.java))
                            }
                        })
                    }
                }else{
                    startActivity(Intent(this@SplashScreenActivity, MainActivity::class.java))
                }
            }else{
//                btnStart.animate().translationY(0F)
//                spin_kit.visibility = View.INVISIBLE
                Misc.zoomInView(btnStart, this@SplashScreenActivity, 300)
                Misc.zoomOutView(
                    animLoading,
                    this@SplashScreenActivity,
                    300
                )
            }
        }, 7000)


    }

    fun start() {
        if(isAdMobInterstitialLoaded){
            Misc.showAdMobInterstitial(this,object : InterstitialCallBack{
                override fun onDismiss() {
                    startActivity(Intent(this@SplashScreenActivity, MainActivity::class.java))
                }
            })
        } else{
            Misc.showInterstitial(this, Misc.isDashboardIntEnabled, object : InterstitialCallBack{
                override fun onDismiss() {
                    startActivity(Intent(this@SplashScreenActivity, MainActivity::class.java))
                }
            })
        }
//        startActivity(
//            Intent(
//                this@SplashScreenActivity,
//                MainActivity::class.java
//            )
//        )
//        finish()
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

                        Misc.nativeAdIdAdMob = mFirebaseRemoteConfig.getString("nativeAdIdAdMob")
                        Misc.nativeAdId = mFirebaseRemoteConfig.getString("nativeAdId")
                        Misc.interstitialAdId = mFirebaseRemoteConfig.getString("interstitialAdId")
                        Misc.interstitialAdIdAdMob = mFirebaseRemoteConfig.getString("interstitialAdIdAdMob")
                        Misc.isSplashIntEnabled = mFirebaseRemoteConfig.getBoolean("isSplashIntEnabled")
                        Misc.isDashboardIntEnabled = mFirebaseRemoteConfig.getBoolean("isDashboardIntEnabled")
                        Misc.isContinentSelectIntEnabled = mFirebaseRemoteConfig.getBoolean("isContinentSelectIntEnabled")
                        Misc.isStartGameIntEnabled = mFirebaseRemoteConfig.getBoolean("isStartGameIntEnabled")
                        Misc.isMainFromProScreenIntEnabled = mFirebaseRemoteConfig.getBoolean("isMainFromProScreenIntEnabled")
                        Misc.isGenerateQRIntEnabled = mFirebaseRemoteConfig.getBoolean("isGenerateQRIntEnabled")
                        Misc.isQuizCompleteIntEnabled = mFirebaseRemoteConfig.getBoolean("isQuizCompleteIntEnabled")
                        Misc.isSkyMapIntEnabled = mFirebaseRemoteConfig.getBoolean("isSkyMapIntEnabled")
                        Misc.isQuizCountriesIntEnabled = mFirebaseRemoteConfig.getBoolean("isQuizCountriesIntEnabled")
                        Misc.isQuizScreenOneNativeEnabled = mFirebaseRemoteConfig.getBoolean("isQuizScreenOneNativeEnabled")
                        Misc.isQuizScreenOneBackIntEnabled = mFirebaseRemoteConfig.getBoolean("isQuizScreenOneBackIntEnabled")
                        Misc.isQuizSelectModeIntEnabled = mFirebaseRemoteConfig.getBoolean("isQuizSelectModeIntEnabled")
                        Misc.isContinentSelectNativeEnabled = mFirebaseRemoteConfig.getBoolean("isContinentSelectNativeEnabled")
                        Misc.isQuizCompleteBackIntEnabled = mFirebaseRemoteConfig.getBoolean("isQuizCompleteBackIntEnabled")
                        Misc.isWorldQuizOnBackIntEnabled = mFirebaseRemoteConfig.getBoolean("isWorldQuizOnBackIntEnabled")
                        Misc.isQuizCompleteNativeEnabled = mFirebaseRemoteConfig.getBoolean("isQuizCompleteNativeEnabled")
                        Misc.isWordlQuizActivityNativeEnabled = mFirebaseRemoteConfig.getBoolean("isWordlQuizActivityNativeEnabled")
                        Misc.isContinentSelectBackIntEnabled = mFirebaseRemoteConfig.getBoolean("isContinentSelectBackIntEnabled")
                        Misc.isQuizSelectModeNativeEnabled = mFirebaseRemoteConfig.getBoolean("isQuizSelectModeNativeEnabled")
                        Misc.isSoundMeterBackIntEnabled = mFirebaseRemoteConfig.getBoolean("isSoundMeterBackIntEnabled")
                        Misc.isSoundMeterNativeEnabled = mFirebaseRemoteConfig.getBoolean("isSoundMeterNativeEnabled")
                        Misc.isSkyMapBackIntEnabled = mFirebaseRemoteConfig.getBoolean("isSkyMapBackIntEnabled")
                        Misc.isSettingBackIntEnabled = mFirebaseRemoteConfig.getBoolean("isSettingBackIntEnabled")
                        Misc.isGenerateQrOnBackIntEnabled = mFirebaseRemoteConfig.getBoolean("isGenerateQrOnBackIntEnabled")
                        Misc.isLiveEarthOnBackIntEnabled = mFirebaseRemoteConfig.getBoolean("isLiveEarthOnBackIntEnabled")
                        Misc.isCompassBackIntEnabled = mFirebaseRemoteConfig.getBoolean("isCompassBackIntEnabled")
                        Misc.isViewWorldBackIntEnabled = mFirebaseRemoteConfig.getBoolean("isViewWorldBackIntEnabled")
                        Misc.isSplashNativeEnabled = mFirebaseRemoteConfig.getBoolean("isSplashNativeEnabled")
                        Misc.isAltitudeBackIntEnabled = mFirebaseRemoteConfig.getBoolean("isAltitudeBackIntEnabled")
                        Misc.isDashboardMRecEnabled = mFirebaseRemoteConfig.getBoolean("isDashboardMRecEnabled")
                        Misc.mRecAdId = mFirebaseRemoteConfig.getString("mRecAdId")

                        if (Misc.nativeAdIdAdMob != "" && !isAdRequestSent) {
                            isAdRequestSent = true
                            Misc.loadAdMobNativeAd(this, Misc.isDashboardNativeEnabled)

//                            val objCustomDialog = CustomDialog(this@SplashScreenActivity)
//                            objCustomDialog.show()
//
//                            val window: Window = objCustomDialog.window!!
//                            window.setLayout(
//                                WindowManager.LayoutParams.FILL_PARENT,
//                                WindowManager.LayoutParams.WRAP_CONTENT
//                            )
//                            window.setBackgroundDrawableResource(R.color.nothing)
//
//                            objCustomDialog.setCancelable(false)
//                            Misc.loadAdMobInterstitial(this, Misc.isSplashIntEnabled, object : LoadInterstitialCallBack{
//                                override fun onLoaded() {
//                                    Handler().postDelayed({
//                                        objCustomDialog.dismiss()
//                                        Misc.showAdMobInterstitial(this@SplashScreenActivity, object : InterstitialCallBack{
//                                            override fun onDismiss() {
//                                                start()
//                                            }
//
//                                        })
//                                    },1000)
//                                }
//
//                                override fun onFailed() {
//                                    Log.e(Misc.logKey, "Interstitial Ad loading failed.")
//                                    start()
//                                }
//
//                            })
                            Misc.loadNativeAd(this, object : NativeAdCallBack {
                                override fun onLoad() {
                                    Misc.showNativeAd(
                                        this@SplashScreenActivity,
                                        nativeAdFrameLayout,
                                        Misc.isSplashNativeEnabled,
                                        object : NativeAdCallBack {
                                            override fun onLoad() {
                                                Misc.zoomInView(nativeAdFrameLayout, this@SplashScreenActivity, 250)
                                            }
                                        })
                                }
                            })

                            Misc.loadAdMobInterstitial(
                                this,
                                Misc.isDashboardIntEnabled,
                                object : LoadInterstitialCallBack {
                                    override fun onLoaded() {
                                        isAdMobInterstitialLoaded = true
                                        Misc.loadInterstitial(this@SplashScreenActivity, null)
                                    }

                                    override fun onFailed() {
                                        Misc.loadInterstitial(this@SplashScreenActivity, null)
                                    }

                                })

                            mFirebaseRemoteConfig.reset()
                        }
                    } else {
                        start()
                        Log.e(Misc.logKey, "Fetch failed")
                    }
                }
            true
        } catch (e: Exception) {
//            start()
            e.printStackTrace()
            false
        }
    }
}