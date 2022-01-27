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
import kotlinx.android.synthetic.main.activity_splash_screen.*

@SuppressLint("CustomSplashScreen", "LogNotTimber")
class SplashScreenActivity : BaseActivity() {
    private var isAdRequestSent: Boolean = false

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

        World.init(this)
        FirebaseApp.initializeApp(applicationContext)

        btnStart.setOnClickListener {
//            if (Misc.isSplashIntEnabled) {
//                Misc.showInterstitial(this, object : InterstitialCallBack {
//                    override fun onDismiss() {
//                        start()
//                    }
//                })
//            } else {
                start()
//            }
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
        }, 5000)


    }

    fun start() {
        startActivity(
            Intent(
                this@SplashScreenActivity,
                MainActivity::class.java
            )
        )
        finish()
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

                        Misc.nativeAdId = mFirebaseRemoteConfig.getString("nativeAdId")
                        Misc.interstitialAdId = mFirebaseRemoteConfig.getString("interstitialAdId")

                        Misc.isSplashIntEnabled =
                            mFirebaseRemoteConfig.getBoolean("isSplashIntEnabled")
                        Misc.isDashboardIntEnabled =
                            mFirebaseRemoteConfig.getBoolean("isDashboardIntEnabled")
                        Misc.isDashboardNativeEnabled =
                            mFirebaseRemoteConfig.getBoolean("isDashboardNativeEnabled")

                        if (Misc.nativeAdId != "" && !isAdRequestSent) {
                            isAdRequestSent = true
                            Misc.loadInterstitial(this, Misc.isSplashIntEnabled, object : LoadInterstitialCallBack{
                                override fun onLoaded() {
                                    val objCustomDialog = CustomDialog(this@SplashScreenActivity)
                                    objCustomDialog.show()

                                    val window: Window = objCustomDialog.window!!
                                    window.setLayout(
                                        WindowManager.LayoutParams.FILL_PARENT,
                                        WindowManager.LayoutParams.WRAP_CONTENT
                                    )
                                    window.setBackgroundDrawableResource(R.color.nothing)
                                    objCustomDialog.setCancelable(false)
                                    Handler().postDelayed({
                                        objCustomDialog.dismiss()
                                        Misc.showInterstitial(this@SplashScreenActivity, null)
                                    },1000)
                                }

                                override fun onFailed() {
                                    Log.e(Misc.logKey, "Interstitial Ad loading failed.")
                                }

                            })

                            Misc.loadNativeAd(this, Misc.isDashboardNativeEnabled)
                            mFirebaseRemoteConfig.reset()
                        }
                    } else {
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