package com.liveearth.android.map

import android.util.Log
import android.os.Bundle
import android.os.Handler
import android.content.Intent
import android.view.WindowManager
import com.blongho.country_data.World
import android.annotation.SuppressLint
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
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

                        Misc.isBannerAdTop = mFirebaseRemoteConfig.getBoolean("isBannerAdTop")
                        Misc.isSplashLargeNative =
                            mFirebaseRemoteConfig.getBoolean("isSplashLargeNative")
                        Misc.isSkyMapBannerEnabled =
                            mFirebaseRemoteConfig.getBoolean("isSkyMapBannerEnabled")
                        Misc.isCompassBannerEnabled =
                            mFirebaseRemoteConfig.getBoolean("isCompassBannerEnabled")
                        Misc.isNoteCamBannerEnabled =
                            mFirebaseRemoteConfig.getBoolean("isNoteCamBannerEnabled")
                        Misc.isDashboardBannerEnabled =
                            mFirebaseRemoteConfig.getBoolean("isDashboardBannerEnabled")
                        Misc.isProScreenBannerEnabled =
                            mFirebaseRemoteConfig.getBoolean("isProScreenBannerEnabled")

                        Misc.bannerAdId = mFirebaseRemoteConfig.getString("bannerAdId")
                        Misc.nativeAdIdApplovin =
                            mFirebaseRemoteConfig.getString("nativeAdIdApplovin")
                        Misc.interstitialAdIdApplovin =
                            mFirebaseRemoteConfig.getString("interstitialAdIdApplovin")

                        Misc.nativeAdIdAdMob = mFirebaseRemoteConfig.getString("nativeAdIdAdMob")
                        Misc.interstitialAdIdAdMob =
                            mFirebaseRemoteConfig.getString("interstitialAdIdAdMob")

                        Misc.isPremiumScreenEnabled =
                            mFirebaseRemoteConfig.getBoolean("isPremiumScreenEnabled")
                        Misc.isPremiumScreenIntAm_Al =
                            mFirebaseRemoteConfig.getString("isPremiumScreenIntAm_Al")
                        Misc.lsvIntAm_al = mFirebaseRemoteConfig.getString("lsvIntAm_al")
                        Misc.lifetimePrice = mFirebaseRemoteConfig.getString("lifetimePrice")
                        Misc.mRecAdId = mFirebaseRemoteConfig.getString("mRecAdId")
                        Misc.yearlyPrice = mFirebaseRemoteConfig.getString("yearlyPrice")
                        Misc.monthlyPrice = mFirebaseRemoteConfig.getString("monthlyPrice")
                        Misc.skyMapIntAm_al = mFirebaseRemoteConfig.getString("skyMapIntAm_al")
                        Misc.gpsCamIntAm_al = mFirebaseRemoteConfig.getString("gpsCamIntAm_al")
                        Misc.isQuitIntAm_Al = mFirebaseRemoteConfig.getString("isQuitIntAm_Al")
                        Misc.compassIntAm_al = mFirebaseRemoteConfig.getString("compassIntAm_al")
                        Misc.noteCamIntAm_al = mFirebaseRemoteConfig.getString("noteCamIntAm_al")
                        Misc.quitNativeAm_Al = mFirebaseRemoteConfig.getString("quitNativeAm_Al")
                        Misc.isSkyMapIntAm_Al = mFirebaseRemoteConfig.getString("isSkyMapIntAm_Al")
                        Misc.altitudeIntAm_al = mFirebaseRemoteConfig.getString("altitudeIntAm_al")
                        Misc.isSplashIntAm_al = mFirebaseRemoteConfig.getString("isSplashIntAm_al")
                        Misc.worldQuizIntAm_al =
                            mFirebaseRemoteConfig.getString("worldQuizIntAm_al")
                        Misc.splashNativeAm_Al =
                            mFirebaseRemoteConfig.getString("splashNativeAm_Al")
                        Misc.startGameIntAm_Al =
                            mFirebaseRemoteConfig.getString("startGameIntAm_Al")
                        Misc.generateQRIntAm_Al =
                            mFirebaseRemoteConfig.getString("generateQRIntAm_Al")
                        Misc.soundMeterIntAm_al =
                            mFirebaseRemoteConfig.getString("soundMeterIntAm_al")
                        Misc.skyMapBackIntAm_Al =
                            mFirebaseRemoteConfig.getString("skyMapBackIntAm_Al")
                        Misc.settingBackIntAm_Al =
                            mFirebaseRemoteConfig.getString("settingBackIntAm_Al")
                        Misc.compassBackIntAm_Al =
                            mFirebaseRemoteConfig.getString("compassBackIntAm_Al")
                        Misc.speedometerIntAm_al =
                            mFirebaseRemoteConfig.getString("speedometerIntAm_al")
                        Misc.createQRNativeAm_Al =
                            mFirebaseRemoteConfig.getString("createQRNativeAm_Al")
                        Misc.dashboardNativeAm_Al =
                            mFirebaseRemoteConfig.getString("dashboardNativeAm_Al")
                        Misc.altitudeBackIntAm_Al =
                            mFirebaseRemoteConfig.getString("altitudeBackIntAm_Al")
                        Misc.quizCompleteIntAm_Al =
                            mFirebaseRemoteConfig.getString("quizCompleteIntAm_Al")
                        Misc.quizCountriesIntAm_Al =
                            mFirebaseRemoteConfig.getString("quizCountriesIntAm_Al")
                        Misc.viewWorldBackIntAm_Al =
                            mFirebaseRemoteConfig.getString("viewWorldBackIntAm_Al")
                        Misc.noteCamOnBackIntAm_Al =
                            mFirebaseRemoteConfig.getString("noteCamOnBackIntAm_Al")
                        Misc.soundMeterNativeAm_Al =
                            mFirebaseRemoteConfig.getString("soundMeterNativeAm_Al")
                        Misc.speedoMeterNativeAm_Al =
                            mFirebaseRemoteConfig.getString("speedoMeterNativeAm_Al")
                        Misc.soundMeterBackIntAm_Al =
                            mFirebaseRemoteConfig.getString("soundMeterBackIntAm_Al")
                        Misc.quizSelectModeIntAm_Al =
                            mFirebaseRemoteConfig.getString("quizSelectModeIntAm_Al")
                        Misc.quizCompleteNativeAm_Al =
                            mFirebaseRemoteConfig.getString("quizCompleteNativeAm_Al")
                        Misc.worldQuizOnBackIntAm_Al =
                            mFirebaseRemoteConfig.getString("worldQuizOnBackIntAm_Al")
                        Misc.continentSelectIntAm_Al =
                            mFirebaseRemoteConfig.getString("continentSelectIntAm_Al")
                        Misc.liveEarthOnBackIntAm_Al =
                            mFirebaseRemoteConfig.getString("liveEarthOnBackIntAm_Al")
                        Misc.generateQrOnBackIntAm_Al =
                            mFirebaseRemoteConfig.getString("generateQrOnBackIntAm_Al")
                        Misc.quizCompleteBackIntAm_Al =
                            mFirebaseRemoteConfig.getString("quizCompleteBackIntAm_Al")
                        Misc.quizScreenOneNativeAm_Al =
                            mFirebaseRemoteConfig.getString("quizScreenOneNativeAm_Al")
                        Misc.mainFromProScreenIntAm_Al =
                            mFirebaseRemoteConfig.getString("mainFromProScreenIntAm_Al")
                        Misc.quizScreenOneBackIntAm_Al =
                            mFirebaseRemoteConfig.getString("quizScreenOneBackIntAm_Al")
                        Misc.quizSelectModeNativeAm_Al =
                            mFirebaseRemoteConfig.getString("quizSelectModeNativeAm_Al")
                        Misc.continentSelectNativeAm_Al =
                            mFirebaseRemoteConfig.getString("continentSelectNativeAm_Al")
                        Misc.continentSelectBackIntAm_Al =
                            mFirebaseRemoteConfig.getString("continentSelectBackIntAm_Al")
                        Misc.worldQuizActivityNativeAm_Al =
                            mFirebaseRemoteConfig.getString("worldQuizActivityNativeAm_Al")

                        try {
                            Misc.adBreakLimit =
                                mFirebaseRemoteConfig.getString("adBreakLimit").toInt()
                        } catch (e: Exception) {
                            Misc.adBreakLimit = 2
                        }
                        try {
                            Misc.frequencyCappingApplovinLimit =
                                mFirebaseRemoteConfig.getString("frequencyCappingApplovinLimit")
                                    .toInt()
                        } catch (e: Exception) {
                            Misc.frequencyCappingApplovinLimit = 0
                        }
                        try {
                            Misc.frequencyCappingAdMobLimit =
                                mFirebaseRemoteConfig.getString("frequencyCappingAdMobLimit")
                                    .toInt()
                        } catch (e: Exception) {
                            Misc.frequencyCappingAdMobLimit = 2
                        }

                        Misc.adBreakCount = Misc.adBreakLimit

                        if (Misc.nativeAdIdAdMob != "" && !isAdRequestSent) {
                            isAdRequestSent = true
                            Ads.loadApplovinNativeAd(this, object : LoadInterstitialCallBack {
                                override fun onLoaded() {
                                    if (Misc.splashNativeAm_Al.contains("al"))
                                        Misc.splashNativeAm_Al = "al"
                                    Ads.showNativeAd(
                                        this@SplashScreenActivity,
                                        nativeAdFrameLayout,
                                        Misc.splashNativeAm_Al,
                                        object : NativeAdCallBack {
                                            override fun onLoad() {
                                                if (!Misc.isSplashLargeNative) {
                                                    val p = nativeAdFrameLayout.layoutParams
                                                    p.height = 700
                                                    nativeAdFrameLayout.layoutParams = p
                                                }

                                                Misc.zoomInView(
                                                    nativeAdFrameLayout,
                                                    this@SplashScreenActivity,
                                                    250
                                                )
                                            }
                                        }
                                    )
                                }

                                override fun onFailed() {

                                }
                            }, Misc.isSplashLargeNative)

                            Ads.loadAdMobNativeAd(
                                this@SplashScreenActivity,
                                null
                            )

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
                            if (!Misc.splashNativeAm_Al.contains("al")) {
                                val intent = Intent(this, MainActivity::class.java)
                                intent.putExtra(Misc.data, Misc.data)
                                startActivity(intent)
                            }
                        }, 5000)
                        mFirebaseRemoteConfig.reset()
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