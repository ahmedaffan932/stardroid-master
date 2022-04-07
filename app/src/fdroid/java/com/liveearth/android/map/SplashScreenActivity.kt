package com.liveearth.android.map

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PurchasesUpdatedListener
import com.blongho.country_data.World
import com.google.firebase.FirebaseApp
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.liveearth.android.map.clasess.Misc
import com.liveearth.android.map.interfaces.InterstitialCallBack
import com.liveearth.android.map.interfaces.LoadInterstitialCallBack
import com.liveearth.android.map.interfaces.NativeAdCallBack
import kotlinx.android.synthetic.main.activity_splash_screen.*

@SuppressLint("CustomSplashScreen", "LogNotTimber")
class SplashScreenActivity : BaseActivity() {
    private var isAdRequestSent: Boolean = false
    private var isAdMobInterstitialLoaded = false
    private var handler = Handler()
    private var loadingPercentage = 0

    private val purchasesUpdatedListener =
        PurchasesUpdatedListener { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                Misc.setPurchasedStatus(this, true)
                Log.d(Misc.logKey, "Ya hooo.....")
                startActivity(Intent(this, SplashScreenActivity::class.java))
                finish()
            }
        }

    private lateinit var billingClient: BillingClient
    private var isStartBtnVisible = false

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
                    billingClient.queryPurchaseHistoryAsync(BillingClient.SkuType.INAPP) { _, p1 ->
                        Log.d(Misc.logKey, p1?.size.toString() + " size ..")
                        if (p1 != null) {
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
        handler.post(runLoadingPercentage)

        btnStart.setOnClickListener {
            start()
        }

        Handler().postDelayed({
            if (!Misc.isSplashNativeEnabled) {
                if (Misc.isDashboardIntEnabled == "am" || Misc.isDashboardIntEnabled == "al" || Misc.isDashboardIntEnabled == "am_al") {
                    if (isAdMobInterstitialLoaded) {
                        Misc.showAdMobInterstitial(this, object : InterstitialCallBack {
                            override fun onDismiss() {
                                startActivity(
                                    Intent(
                                        this@SplashScreenActivity,
                                        MainActivity::class.java
                                    )
                                )
                            }
                        })
                    } else {
                        Misc.showInterstitial(this, true, object : InterstitialCallBack {
                            override fun onDismiss() {
                                startActivity(
                                    Intent(
                                        this@SplashScreenActivity,
                                        MainActivity::class.java
                                    )
                                )
                            }
                        })
                    }
                } else {
                    startActivity(Intent(this@SplashScreenActivity, MainActivity::class.java))
                }
            } else {
                if (!isStartBtnVisible) {
                    Misc.zoomInView(btnStart, this@SplashScreenActivity, 300)
                    Misc.zoomOutView(
                        animLoading,
                        this@SplashScreenActivity,
                        300
                    )
                }
            }
        }, 8000)

    }


    private val runLoadingPercentage: Runnable by lazy {
        return@lazy object : Runnable {
            @SuppressLint("LogNotTimber", "SetTextI18n")
            override fun run() {
                if (loadingPercentage < 100)
                    loadingPercentage += 1
                tvLoadingPercentage.text = "$loadingPercentage%"
                handler.postDelayed(this, 80)
            }
        }
    }

    fun start() {
        if (isAdMobInterstitialLoaded) {
            Misc.showAdMobInterstitial(this, object : InterstitialCallBack {
                override fun onDismiss() {
                    startActivity(Intent(this@SplashScreenActivity, MainActivity::class.java))
                }
            })
        } else {
            val bool = Misc.isDashboardIntEnabled == "al" || Misc.isDashboardIntEnabled == "am_al"
            Misc.showInterstitial(this, bool, object : InterstitialCallBack {
                override fun onDismiss() {
                    startActivity(Intent(this@SplashScreenActivity, MainActivity::class.java))
                }
            })
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

                        Misc.nativeAdIdAdMob = mFirebaseRemoteConfig.getString("nativeAdIdAdMob")
                        Misc.nativeAdId = mFirebaseRemoteConfig.getString("nativeAdId")
                        Misc.interstitialAdId = mFirebaseRemoteConfig.getString("interstitialAdId")
                        Misc.interstitialAdIdAdMob = mFirebaseRemoteConfig.getString("interstitialAdIdAdMob")
                        Misc.isDashboardIntEnabled = mFirebaseRemoteConfig.getString("isDashboardIntEnabled")
                        Misc.isContinentSelectIntEnabled = mFirebaseRemoteConfig.getBoolean("isContinentSelectIntEnabled")
                        Misc.isStartGameIntEnabled = mFirebaseRemoteConfig.getBoolean("isStartGameIntEnabled")
                        Misc.isDashboardItemIntEnabled = mFirebaseRemoteConfig.getBoolean("isDashboardItemIntEnabled")
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
                        Misc.isDashboardNativeEnabled = mFirebaseRemoteConfig.getString("isDashboardNativeEnabled")

                        Misc.isCompassBannerEnabled = mFirebaseRemoteConfig.getBoolean("isCompassBannerEnabled")
                        Misc.isNoteCamBannerEnabled = mFirebaseRemoteConfig.getBoolean("isNoteCamBannerEnabled")
                        Misc.isQuitNativeEnabled = mFirebaseRemoteConfig.getBoolean("isQuitNativeEnabled")
                        Misc.isQuitIntEnabled = mFirebaseRemoteConfig.getBoolean("isQuitIntEnabled")
                        Misc.bannerAdId = mFirebaseRemoteConfig.getString("bannerAdId")
                        Misc.isSkyMapBannerEnabled = mFirebaseRemoteConfig.getBoolean("isSkyMapBannerEnabled")
                        Misc.isDashboardBannerEnabled = mFirebaseRemoteConfig.getBoolean("isDashboardBannerEnabled")
                        Misc.isCreateQRNativeEnabled = mFirebaseRemoteConfig.getBoolean("isCreateQRNativeEnabled")
                        Misc.isBannerAdTop = mFirebaseRemoteConfig.getBoolean("isBannerAdTop")
                        Misc.isProScreenBannerEnabled = mFirebaseRemoteConfig.getBoolean("isProScreenBannerEnabled")
                        Misc.isSplashLargeNative = mFirebaseRemoteConfig.getBoolean("isSplashLargeNative")

                        Misc.mRecAdId = mFirebaseRemoteConfig.getString("mRecAdId")

                        if (Misc.nativeAdIdAdMob != "" && !isAdRequestSent) {
                            isAdRequestSent = true
                            Misc.loadAdMobNativeAd(this, Misc.isDashboardNativeEnabled)
                            Misc.loadNativeAd(this, object : NativeAdCallBack {
                                override fun onLoad() {
                                    Misc.showNativeAd(
                                        this@SplashScreenActivity,
                                        nativeAdFrameLayout,
                                        Misc.isSplashNativeEnabled,
                                        object : NativeAdCallBack {
                                            override fun onLoad() {
                                                if (!Misc.isSplashLargeNative) {
                                                    val p = nativeAdFrameLayout.layoutParams
                                                    p.height = 800
                                                    nativeAdFrameLayout.layoutParams = p
                                                } else {
                                                    Misc.zoomOutView(
                                                        icSplash,
                                                        this@SplashScreenActivity,
                                                        250
                                                    )
                                                }
                                                Misc.zoomInView(
                                                    nativeAdFrameLayout,
                                                    this@SplashScreenActivity,
                                                    250
                                                )
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
                                        Log.d(Misc.logKey, " On Failed.")
                                        Misc.loadInterstitial(this@SplashScreenActivity, null)
                                    }

                                })

                            Misc.loadBannerAd(this)
                            mFirebaseRemoteConfig.reset()
                        }
                    } else {
                        start()
                        Log.e(Misc.logKey, "Fetch failed")
                    }
                }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}