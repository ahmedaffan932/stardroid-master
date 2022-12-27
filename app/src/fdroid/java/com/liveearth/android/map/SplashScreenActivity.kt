package com.liveearth.android.map

import android.util.Log
import android.os.Bundle
import android.os.Handler
import android.content.Intent
import android.view.WindowManager
import com.blongho.country_data.World
import android.annotation.SuppressLint
import android.os.Looper
import android.view.View
import com.google.firebase.FirebaseApp
import com.google.firebase.ktx.Firebase
import com.liveearth.android.map.clasess.Ads
import com.liveearth.android.map.clasess.Misc
import com.anjlab.android.iab.v3.PurchaseInfo
import com.anjlab.android.iab.v3.BillingProcessor
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.liveearth.android.map.interfaces.NativeAdCallBack
import kotlinx.android.synthetic.main.activity_splash_screen.*
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.gpsnavigation.customnotification.services.FcmFireBaseID
import com.liveearth.android.map.clasess.FacebookAds
import com.liveearth.android.map.interfaces.InterstitialCallBack
import com.liveearth.android.map.interfaces.LoadInterstitialCallBack

@SuppressLint("CustomSplashScreen", "LogNotTimber")
class SplashScreenActivity : BaseActivity() {
    private var handler = Handler()
    private var loadingPercentage = 0
    private lateinit var bp: BillingProcessor
    private var isAdRequestSent: Boolean = false
    private var isAdMobInterstitialLoaded = false

    @SuppressLint("LogNotTimber")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_splash_screen)

        billing()
        FirebaseApp.initializeApp(this)

        FcmFireBaseID.subscribeToTopic()
        getRemoteConfigValues()

        World.init(this)
        FirebaseApp.initializeApp(applicationContext)
        handler.post(runLoadingPercentage)


        btnStart.setOnClickListener {
            start()
        }

        btnStartBottom.setOnClickListener {
            start()
        }

        Handler().postDelayed({
            Log.d(Misc.logKey, "Asd ${Misc.splashNativeAm_Al}")
            if (Misc.isSplashLargeNative) {
                Misc.zoomInView(btnStart, this@SplashScreenActivity, 300)
            } else {
                btnStartBottom.visibility = View.VISIBLE
                Misc.zoomInView(btnStartBottom, this@SplashScreenActivity, 300)
            }

            Misc.zoomOutView(
                animLoading,
                this@SplashScreenActivity,
                300
            )
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
        if (Misc.getPurchasedStatus(this)) {
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            Ads.showInterstitial(this, Misc.isSplashIntAm_al, object : InterstitialCallBack {
                override fun onDismiss() {
                    Misc.adBreakCount = Misc.adBreakLimit
                    if (Misc.isPremiumScreenEnabled) {
                        startActivity(
                            Intent(
                                this@SplashScreenActivity,
                                PremiumScreenActivity::class.java
                            )
                        )
                    } else {
                        startActivity(Intent(this@SplashScreenActivity, MainActivity::class.java))
                    }
                }
            })
        }
    }

    @SuppressLint("LogNotTimber")
    private fun getRemoteConfigValues(): Boolean {
        return try {
            val mFRC = Firebase.remoteConfig
            val configSettings = remoteConfigSettings {
                minimumFetchIntervalInSeconds = if (BuildConfig.DEBUG) 0 else 3600
            }
            mFRC.setConfigSettingsAsync(configSettings)
            mFRC.fetchAndActivate()
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val updated = task.result
                        Log.d(Misc.logKey, "Fetch Config params updated: $updated")
                        mFRC.activate()

                        Misc.isBannerAdTop = mFRC.getBoolean("isBannerAdTop")
                        Misc.isSplashLargeNative =
                            mFRC.getBoolean("isSplashLargeNative")
                        Misc.isSkyMapBannerEnabled =
                            mFRC.getBoolean("isSkyMapBannerEnabled")
                        Misc.isCompassBannerEnabled =
                            mFRC.getBoolean("isCompassBannerEnabled")
                        Misc.isNoteCamBannerEnabled =
                            mFRC.getBoolean("isNoteCamBannerEnabled")
                        Misc.isDashboardBannerEnabled =
                            mFRC.getBoolean("isDashboardBannerEnabled")
                        Misc.isProScreenBannerEnabled =
                            mFRC.getBoolean("isProScreenBannerEnabled")

                        Misc.bannerAdId = mFRC.getString("bannerAdId")
                        Misc.nativeAdIdApplovin =
                            mFRC.getString("nativeAdIdApplovin")
                        Misc.interstitialAdIdApplovin =
                            mFRC.getString("interstitialAdIdApplovin")

                        Misc.nativeAdIdAdMob = mFRC.getString("nativeAdIdAdMob")
                        Misc.interstitialAdIdAdMob =
                            mFRC.getString("interstitialAdIdAdMob")

                        Misc.mRecAdId = mFRC.getString("mRecAdId")
                        Misc.lsvIntAm_al = mFRC.getString("lsvIntAm_al")
                        Misc.yearlyPrice = mFRC.getString("yearlyPrice")
                        Misc.monthlyPrice = mFRC.getString("monthlyPrice")
                        Misc.lifetimePrice = mFRC.getString("lifetimePrice")

                        Misc.yearlySubscriptionId = mFRC.getString("yearlySubscriptionId")
                        Misc.monthlySubscriptionId = mFRC.getString("monthlySubscriptionId")

                        Misc.skyMapIntAm_al = mFRC.getString("skyMapIntAm_al")
                        Misc.gpsCamIntAm_al = mFRC.getString("gpsCamIntAm_al")
                        Misc.isQuitIntAm_Al = mFRC.getString("isQuitIntAm_Al")
                        Misc.compassIntAm_al = mFRC.getString("compassIntAm_al")
                        Misc.noteCamIntAm_al = mFRC.getString("noteCamIntAm_al")
                        Misc.quitNativeAm_Al = mFRC.getString("quitNativeAm_Al")
                        Misc.isSkyMapIntAm_Al = mFRC.getString("isSkyMapIntAm_Al")
                        Misc.altitudeIntAm_al = mFRC.getString("altitudeIntAm_al")
                        Misc.isSplashIntAm_al = mFRC.getString("isSplashIntAm_al")
                        Misc.worldQuizIntAm_al = mFRC.getString("worldQuizIntAm_al")
                        Misc.splashNativeAm_Al = mFRC.getString("splashNativeAm_Al")
                        Misc.startGameIntAm_Al = mFRC.getString("startGameIntAm_Al")
                        Misc.generateQRIntAm_Al = mFRC.getString("generateQRIntAm_Al")
                        Misc.soundMeterIntAm_al = mFRC.getString("soundMeterIntAm_al")
                        Misc.skyMapBackIntAm_Al = mFRC.getString("skyMapBackIntAm_Al")
                        Misc.settingBackIntAm_Al = mFRC.getString("settingBackIntAm_Al")
                        Misc.compassBackIntAm_Al = mFRC.getString("compassBackIntAm_Al")
                        Misc.speedometerIntAm_al = mFRC.getString("speedometerIntAm_al")
                        Misc.createQRNativeAm_Al = mFRC.getString("createQRNativeAm_Al")
                        Misc.dashboardNativeAm_Al = mFRC.getString("dashboardNativeAm_Al")
                        Misc.altitudeBackIntAm_Al = mFRC.getString("altitudeBackIntAm_Al")
                        Misc.quizCompleteIntAm_Al = mFRC.getString("quizCompleteIntAm_Al")
                        Misc.quizCountriesIntAm_Al = mFRC.getString("quizCountriesIntAm_Al")
                        Misc.viewWorldBackIntAm_Al = mFRC.getString("viewWorldBackIntAm_Al")
                        Misc.noteCamOnBackIntAm_Al = mFRC.getString("noteCamOnBackIntAm_Al")
                        Misc.soundMeterNativeAm_Al = mFRC.getString("soundMeterNativeAm_Al")
                        Misc.speedoMeterNativeAm_Al = mFRC.getString("speedoMeterNativeAm_Al")
                        Misc.soundMeterBackIntAm_Al = mFRC.getString("soundMeterBackIntAm_Al")
                        Misc.quizSelectModeIntAm_Al = mFRC.getString("quizSelectModeIntAm_Al")
                        Misc.isPremiumScreenEnabled = mFRC.getBoolean("isPremiumScreenEnabled")
                        Misc.isPremiumScreenIntAm_Al = mFRC.getString("isPremiumScreenIntAm_Al")
                        Misc.quizCompleteNativeAm_Al = mFRC.getString("quizCompleteNativeAm_Al")
                        Misc.worldQuizOnBackIntAm_Al = mFRC.getString("worldQuizOnBackIntAm_Al")
                        Misc.continentSelectIntAm_Al = mFRC.getString("continentSelectIntAm_Al")
                        Misc.liveEarthOnBackIntAm_Al = mFRC.getString("liveEarthOnBackIntAm_Al")
                        Misc.generateQrOnBackIntAm_Al = mFRC.getString("generateQrOnBackIntAm_Al")
                        Misc.quizCompleteBackIntAm_Al = mFRC.getString("quizCompleteBackIntAm_Al")
                        Misc.quizScreenOneNativeAm_Al = mFRC.getString("quizScreenOneNativeAm_Al")
                        Misc.mainFromProScreenIntAm_Al = mFRC.getString("mainFromProScreenIntAm_Al")
                        Misc.quizScreenOneBackIntAm_Al = mFRC.getString("quizScreenOneBackIntAm_Al")
                        Misc.quizSelectModeNativeAm_Al = mFRC.getString("quizSelectModeNativeAm_Al")

                        Ads.nativeAdIdFB = mFRC.getString("nativeAdIdFB")
                        Ads.interstitialAdIdFB = mFRC.getString("interstitialAdIdFB")

                        Misc.continentSelectNativeAm_Al =
                            mFRC.getString("continentSelectNativeAm_Al")
                        Misc.continentSelectBackIntAm_Al =
                            mFRC.getString("continentSelectBackIntAm_Al")
                        Misc.worldQuizActivityNativeAm_Al =
                            mFRC.getString("worldQuizActivityNativeAm_Al")

                        try {
                            Misc.adBreakLimit = mFRC.getString("adBreakLimit").toInt()
                        } catch (e: Exception) {
                            Misc.adBreakLimit = 2
                        }
                        try {
                            Misc.frequencyCappingApplovinLimit =
                                mFRC.getString("frequencyCappingApplovinLimit")
                                    .toInt()
                        } catch (e: Exception) {
                            Misc.frequencyCappingApplovinLimit = 0
                        }
                        try {
                            Misc.frequencyCappingAdMobLimit =
                                mFRC.getString("frequencyCappingAdMobLimit")
                                    .toInt()
                        } catch (e: Exception) {
                            Misc.frequencyCappingAdMobLimit = 2
                        }

                        Misc.adBreakCount = Misc.adBreakLimit

                        if (Misc.nativeAdIdAdMob != "" && !isAdRequestSent) {
                            isAdRequestSent = true

                            Handler(Looper.getMainLooper()).postDelayed({
                                Ads.loadApplovinNativeAd(this, null, Misc.isSplashLargeNative)
                            }, 1000)

//                            Ads.loadAdMobNativeAd(
//                                this@SplashScreenActivity,
//                                object : LoadInterstitialCallBack {
//                                    override fun onLoaded() {
//                                        Ads.showNativeAd(
//                                            this@SplashScreenActivity,
//                                            nativeAdFrameLayout,
//                                            Misc.splashNativeAm_Al,
//                                            object : NativeAdCallBack {
//                                                override fun onLoad() {
//                                                    if (!Misc.isSplashLargeNative) {
//                                                        val p = nativeAdFrameLayout.layoutParams
//                                                        p.height = 700
//                                                        nativeAdFrameLayout.layoutParams = p
//                                                    }
//
//                                                    Misc.zoomInView(
//                                                        nativeAdFrameLayout,
//                                                        this@SplashScreenActivity,
//                                                        250
//                                                    )
//                                                }
//                                            }
//                                        )
//                                    }
//                                }
//                            )

                            FacebookAds.loadNativeAd(this, object : LoadInterstitialCallBack {
                                override fun onLoaded() {
                                    FacebookAds.showNativeAd(
                                        this@SplashScreenActivity,
                                        nativeAdFrameLayout
                                    )
                                    FacebookAds.loadInterstitialAd(
                                        this@SplashScreenActivity,
                                        object : LoadInterstitialCallBack {
                                            override fun onLoaded() {
                                                FacebookAds.showInterstitial(this@SplashScreenActivity)
                                            }
                                        })
                                }

                                override fun onFailed() {
                                    FacebookAds.loadInterstitialAd(
                                        this@SplashScreenActivity,
                                        object : LoadInterstitialCallBack {
                                            override fun onLoaded() {
                                                FacebookAds.showInterstitial(this@SplashScreenActivity)
                                            }
                                        }
                                    )
                                }
                            })



                            if (Misc.isSplashIntAm_al.contains("am")) {
                                Ads.loadAdMobInterstitial(
                                    this,
                                    object : LoadInterstitialCallBack {
                                        override fun onLoaded() {
                                            isAdMobInterstitialLoaded = true
                                            Ads.loadApplovinInterstitial(
                                                this@SplashScreenActivity,
                                                null
                                            )
                                            Ads.loadBannerAd(this@SplashScreenActivity)
                                        }

                                        override fun onFailed() {
                                            Log.d(Misc.logKey, " On Failed.")
                                            Ads.loadApplovinInterstitial(
                                                this@SplashScreenActivity,
                                                null
                                            )
                                            Ads.loadBannerAd(this@SplashScreenActivity)
                                        }
                                    })
                            } else {
                                Ads.loadApplovinInterstitial(
                                    this,
                                    object : LoadInterstitialCallBack {
                                        override fun onLoaded() {
                                            Ads.loadBannerAd(this@SplashScreenActivity)
                                            Ads.loadAdMobInterstitial(
                                                this@SplashScreenActivity,
                                                null
                                            )
                                        }

                                        override fun onFailed() {
                                            Ads.loadBannerAd(this@SplashScreenActivity)
                                            Ads.loadAdMobInterstitial(
                                                this@SplashScreenActivity,
                                                null
                                            )
                                        }
                                    })
                            }
                        }

                        Handler().postDelayed({
                            if (!Misc.splashNativeAm_Al.contains("al") && !Misc.splashNativeAm_Al.contains(
                                    "am"
                                )
                            ) {
                                val intent = Intent(this, MainActivity::class.java)
                                intent.putExtra(Misc.data, Misc.data)
                                startActivity(intent)
                            }
                        }, 5000)
                        mFRC.reset()
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

    private fun billing() {
        bp = BillingProcessor(
            this,
            getString(R.string.billing_id),
            object : BillingProcessor.IBillingHandler {
                override fun onProductPurchased(
                    productId: String,
                    details: PurchaseInfo?
                ) {
                    Misc.setPurchasedStatus(this@SplashScreenActivity, true)
                }

                override fun onPurchaseHistoryRestored() {

                }

                override fun onBillingError(errorCode: Int, error: Throwable?) {
                    Log.e("TAG", "onBillingError: ${error?.message}")
                }

                override fun onBillingInitialized() {
                    Log.e("TAG", "onBillingInitialized: ")
                }

            })

        bp.initialize()
    }

}