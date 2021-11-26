package com.liveearth.android.map.clasess

import android.annotation.SuppressLint
import android.app.Activity
import android.content.*
import android.graphics.Bitmap
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.example.qrcodescanner.activities.sdk29AndUp
import com.google.android.ads.nativetemplates.NativeTemplateStyle
import com.google.android.ads.nativetemplates.TemplateView
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.liveearth.android.map.BuildConfig
import com.liveearth.android.map.R
import com.liveearth.android.map.interfaces.*
import com.mapbox.api.directions.v5.models.DirectionsRoute
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class Misc {
    @SuppressLint("LogNotTimber")
    companion object {
        const val appUrl: String =
            "https://play.google.com/store/apps/details?id=com.liveearthmap.liveearthcam.streetview.gps.map.worldmap.satellite.app"
        const val currencies: String = "currencies"
        const val oceania: String = "oceania"
        const val america: String = "america"
        const val africa: String = "africa"
        const val europe: String = "europe"
        const val asia: String = "asia"
        const val wholeWorld: String = "world"
        var gameMode: String = ""
        var gameContinent: String = ""
        var levels: String = "levels"
        var startingTime: Long = 0
        var navigationLimit = 2
        const val data: String = "data"
        var mInterstitialAd: InterstitialAd? = null
        var mNativeAd: com.google.android.gms.ads.nativead.NativeAd? = null
        var intFailedCount = 0

        var nativeFailedCount = 0
        const val logKey: String = "logKey"

        private const val purchasedStatus: String = "purchasedStatus"
        const val flags: String = "flags"

        const val capitals: String = "capitals"
        const val countries: String = "countries"

        private const val flash: String = "flash"
        private const val lastUri: String = "lastUri"
        private const val cameraFace: String = "cameraFace"
        var location: Location? = null
        var isGameIntEnabled: Boolean = false
        var isSkyMapIntEnabled: Boolean = true
        var isSplashIntEnabled: Boolean = true

        var isSettingIntEnabled: Boolean = true
        var isCompassIntEnabled: Boolean = true
        var isNoteCamIntEnabled: Boolean = true
        var isGameBackIntEnabled: Boolean = true
        var isAltitudeIntEnabled: Boolean = true
        var isProScreenIntEnabled: Boolean = true
        var isLiveEarthIntEnabled: Boolean = true
        var isViewWorldIntEnabled: Boolean = true
        var isStartGameIntEnabled: Boolean = true
        var isSplashNativeEnabled: Boolean = true
        var isSoundMeterIntEnabled: Boolean = true
        var isGenerateQRIntEnabled: Boolean = true
        var isNavigationIntEnabled: Boolean = true
        var isGPSMapCamsIntEnabled: Boolean = true
        var isSkyMapBackIntEnabled: Boolean = true
        var isSettingBackIntEnabled: Boolean = true
        var isCompassBackIntEnabled: Boolean = true
        var isSpeedometerIntEnabled: Boolean = true
        var isPlayGameBackIntEnabled: Boolean = true
        var isAltitudeBackIntEnabled: Boolean = true
        var isQuizCompleteIntEnabled: Boolean = true
        var isSoundMeterNativeEnabled: Boolean = true
        var isQuizScreenOneIntEnabled: Boolean = true
        var isQuizCountriesIntEnabled: Boolean = true
        var isProScreenBackIntEnabled: Boolean = true
        var isViewWorldBackIntEnabled: Boolean = true
        var isNoteCamOnBackIntEnabled: Boolean = true
        var isStartGameBackIntEnabled: Boolean = true
        var isSoundMeterBackIntEnabled: Boolean = true
        var isQuizSelectModeIntEnabled: Boolean = true
        var isNavigationBackIntEnabled: Boolean = true
        var isQuizCurrenciesIntEnabled: Boolean = true
        var isSpeedometerNativeEnabled: Boolean = true
        var isSearchLocationIntEnabled: Boolean = true
        var isContinentSelectIntEnabled: Boolean = true
        var isQuizCompleteNativeEnabled: Boolean = true
        var isQuizActivitySplashEnabled: Boolean = true
        var isSpeedometerBackIntEnabled: Boolean = true
        var isLiveEarthOnBackIntEnabled: Boolean = true
        var isQuizCompleteBackIntEnabled: Boolean = true
        var isQuizScreenOneNativeEnabled: Boolean = true
        var isGenerateQrOnBackIntEnabled: Boolean = true
        var isQuizScreenOneBackIntEnabled: Boolean = true
        var isQuizSelectModeNativeEnabled: Boolean = true
        var isMainFromProScreenIntEnabled: Boolean = true
        var isContinentSelectNativeEnabled: Boolean = true
        var isGenerateQrOnBackNativeEnabled: Boolean = true
        var isContinentSelectBackIntEnabled: Boolean = true

        var nativeAdId = "ca-app-pub-3940256099942544/2247696110"
        var interstitialAdId = "ca-app-pub-3940256099942544/1033173712"

        var route: DirectionsRoute? = null

        var inAppKey = if (BuildConfig.DEBUG) {
            "android.test.purchased"
        } else {
            "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAujKAuRSsOBeupTizEzEJprzT8ZBba12bbHN9bS4Fz3S8rmfRLC1VZ0MBb56tkq2JmuqAp1bmMRu2yYJGLCck5ZxV67QthUhYpWLccmEP89cdz6HgUtQvsihRAsO29JUOnaL/Zc+quFvf13+dHR/8tN4ySIFcQ6w29NxACbHdlUfngdEbaxrP/z8dk6bFkbGmNabH4DqDv3+gURWZuaT4OnK86dVLJRzoiGcG6wJJY4nxhj6gCh78O9rGbQkJi+hY4kQ+OMM0evOqTNVn4fSahFAGJmDya5nr57i9xREslI3JT7Y+vzNXDvJmNuqyLvAEzYZvUt/hdKzyy4MearU08QIDAQAB"
        }

        fun startActivity(
                activity: Activity,
                isIntEnabled: Boolean,
                callBack: StartActivityCallBack?
        ) {
            showInterstitial(activity,
                    isIntEnabled,
                    object : InterstitialCallBack {
                        override fun onDismiss() {
                            callBack?.onStart()
                        }
                    })
        }

        fun onBackPress(
                activity: Activity,
                inIntEnabled: Boolean,
                callBack: OnBackPressCallBack?
        ) {
            showInterstitial(activity, inIntEnabled, object : InterstitialCallBack {
                override fun onDismiss() {
                    callBack?.onBackPress()
                }
            })
        }

        fun hideShowView(view: View, activity: Activity, isVisible: Boolean): Boolean {
            if (isVisible) {
                hideView(view, activity, isVisible)
            } else {
                showView(view, activity, isVisible)
            }
            return !isVisible
        }

        fun hideView(view: View, activity: Activity, isVisible: Boolean): Boolean {
            return if (!isVisible) {
                false
            } else {
                zoomOutView(view, activity, 150)
                false
            }
        }

        fun showView(view: View, activity: Activity, isVisible: Boolean): Boolean {
            return if (isVisible) {
                true
            } else {
                view.visibility = View.VISIBLE
                zoomInView(view, activity, 150)
                true
            }
        }

        fun zoomInView(view: View, activity: Activity, duration: Int) {
            val a: Animation =
                    AnimationUtils.loadAnimation(activity, R.anim.zoom_in)
            a.duration = duration.toLong()
            view.startAnimation(a)
        }

        fun zoomOutView(view: View, activity: Activity, duration: Int) {
            val a: Animation =
                    AnimationUtils.loadAnimation(activity, R.anim.zoom_out)
            a.duration = duration.toLong()
            view.startAnimation(a)
        }

        fun getFlash(activity: Activity): Boolean {
            val sharedPreferences: SharedPreferences =
                    activity.getSharedPreferences(flash, AppCompatActivity.MODE_PRIVATE)
            return sharedPreferences.getBoolean(flash, false)
        }


        fun setCameraFace(activity: Activity, boolean: Boolean) {
            val sharedPreferences = activity.getSharedPreferences(
                    cameraFace,
                    AppCompatActivity.MODE_PRIVATE
            )
            val editor = sharedPreferences.edit()
            editor.putBoolean(cameraFace, boolean)
            editor.apply()
        }

        fun getCameraFace(activity: Activity): Boolean {
            val sharedPreferences: SharedPreferences =
                    activity.getSharedPreferences(cameraFace, AppCompatActivity.MODE_PRIVATE)
            return sharedPreferences.getBoolean(cameraFace, true)
        }

        @SuppressLint("DefaultLocale", "SimpleDateFormat")
        fun timeMillsToHms(millis: Long): String {
            val formatter = SimpleDateFormat("dd-MM-yyyy hh:mm")

            val calendar: Calendar = Calendar.getInstance()
            calendar.timeInMillis = millis
            return formatter.format(calendar.time)

        }

        fun saveImageToExternal(
                activity: Activity,
                bitmap: Bitmap,
                onImageSaveCallBack: OnImageSaveCallBack?
        ): Uri? {
            val imageCollection = sdk29AndUp {
                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            } ?: MediaStore.Images.Media.EXTERNAL_CONTENT_URI

            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, "${System.currentTimeMillis()}.jpg")
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                put(MediaStore.Images.Media.WIDTH, bitmap.width)
                put(MediaStore.Images.Media.HEIGHT, bitmap.height)
            }
            return try {
                activity.contentResolver.insert(imageCollection, contentValues)?.also { uri ->
                    activity.contentResolver.openOutputStream(uri).use { outputStream ->
                        if (!bitmap.compress(Bitmap.CompressFormat.JPEG, 95, outputStream)) {
                            throw IOException("Couldn't save bitmap")
                        } else {
                            onImageSaveCallBack?.onImageSaved()
                            return uri
                        }
                    }
                } ?: throw IOException("Couldn't create MediaStore entry")
            } catch (e: IOException) {
                e.printStackTrace()
                null
            }
        }

        fun getLastSavedUri(activity: Activity): String {
            val sharedPreferences: SharedPreferences =
                    activity.getSharedPreferences(lastUri, AppCompatActivity.MODE_PRIVATE)
            return sharedPreferences.getString(lastUri, "o").toString()
        }

        fun setLatestUri(uri: String, activity: Activity) {
            val sharedPreferences =
                    activity.getSharedPreferences(lastUri, AppCompatActivity.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString(lastUri, uri)
            editor.apply()
        }

        fun setPurchasedStatus(activity: Activity, boolean: Boolean) {
            val sharedPreferences = activity.getSharedPreferences(
                    purchasedStatus,
                    AppCompatActivity.MODE_PRIVATE
            )
            val editor = sharedPreferences.edit()
            editor.putBoolean(purchasedStatus, boolean)
            editor.apply()
        }

        fun getPurchasedStatus(activity: Activity?): Boolean {
            val sharedPreferences =
                    activity!!.getSharedPreferences(purchasedStatus, Context.MODE_PRIVATE)
            return sharedPreferences.getBoolean(purchasedStatus, false)
        }

        fun loadInterstitial(activity: Activity, id: String) {
            if (!getPurchasedStatus(activity) && intFailedCount < 3 && checkInternetConnection(
                            activity
                    )
            ) {
                val adRequest = AdRequest.Builder().build()
                InterstitialAd.load(
                        activity,
//                    test
//                    "ca-app-pub-4113492848151023/5138055389",
                        id,
                        adRequest,
                        object : InterstitialAdLoadCallback() {
                            override fun onAdFailedToLoad(adError: LoadAdError) {
                                Log.d(logKey, adError.message)
                                val clipboard: ClipboardManager =
                                        activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip =
                                        ClipData.newPlainText("Camera Translator", adError.message)
                                clipboard.setPrimaryClip(clip)
                                Log.e(logKey, adError.message)
                                intFailedCount++
                                mInterstitialAd = null
                            }

                            override fun onAdLoaded(p0: InterstitialAd) {
                                mInterstitialAd = p0
                                intFailedCount = 0
                                Log.d(logKey, "Interstitial Ad loaded.")
                            }
                        })
            }
        }

        fun showInterstitial(
                activity: Activity,
                isEnabled: Boolean,
                callBack: InterstitialCallBack?
        ) {
            if (getPurchasedStatus(activity)) {
                callBack?.onDismiss()
                return
            }
            if (isEnabled) {
                if (mInterstitialAd != null) {
                    mInterstitialAd?.show(activity)
                } else {
                    loadInterstitial(activity, interstitialAdId)
                    callBack?.onDismiss()
                    Log.d("TAG", "The interstitial ad wasn't ready yet.")
                    return
                }

                mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        callBack?.onDismiss()
                    }

                    override fun onAdFailedToShowFullScreenContent(adError: AdError?) {
                        callBack?.onDismiss()
                        loadInterstitial(activity, interstitialAdId)
                    }

                    override fun onAdShowedFullScreenContent() {
                        Log.d(logKey, "Ad showed fullscreen content.")
                        mInterstitialAd = null
                        loadInterstitial(activity, interstitialAdId)
                    }
                }
            } else {
                callBack?.onDismiss()
            }
            if (!getPurchasedStatus(activity)) {
                if (mInterstitialAd == null) {
                    callBack?.onDismiss()
                    loadInterstitial(activity, interstitialAdId)
                }
            }
        }

        fun loadNativeAd(
                activity: Activity,
                id: String,
                callBack: NativeAdCallBack?
        ) {
            mNativeAd = null
            if (!getPurchasedStatus(activity) && nativeFailedCount < 3 && checkInternetConnection(activity)) {
                val adLoader: AdLoader =
                        AdLoader.Builder(activity, /* "ca-app-pub-3940256099942544/2247696110" */ id)
                                .forNativeAd { nativeAd ->
//                                    val typedValue = TypedValue()
//                                    activity.theme.resolveAttribute(
//                                            com.google.android.gms.ads.R.attr.colorPrimary,
//                                            typedValue,
//                                            true
//                                    )
//                                    val color = typedValue.data
//                                    colorPrimary = color
                                    Log.d(logKey, "Native Ad Loaded")

                                    nativeFailedCount = 0

                                    mNativeAd = nativeAd
                                    callBack?.onLoad()
                                    if (activity.isDestroyed) {
                                        nativeAd.destroy()
                                    }

                                }.withAdListener(object : AdListener() {
                                    override fun onAdFailedToLoad(adError: LoadAdError) {
                                        val clipboard: ClipboardManager =
                                                activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        val clip =
                                                ClipData.newPlainText("Camera Translator", adError.message)
                                        clipboard.setPrimaryClip(clip)
                                        nativeFailedCount++
                                        loadNativeAd(activity, nativeAdId, null)
                                        Log.e(logKey, adError.message)
                                    }
                                })
                                .build()
                adLoader.loadAd(AdRequest.Builder().build())
            }
        }

        fun showNativeAd(
                activity: Activity,
                nativeAdTemplateView: TemplateView,
                isEnabled: Boolean,
                callBack: NativeAdCallBack?
        ) {
            if (!getPurchasedStatus(activity))
                if (mNativeAd != null) {
                    if (isEnabled) {
                        val styles =
                                NativeTemplateStyle.Builder()
                                        .withMainBackgroundColor(ColorDrawable())
                                        .build()
                        nativeAdTemplateView.setStyles(styles)
                        nativeAdTemplateView.setNativeAd(mNativeAd)

                        if (mNativeAd?.mediaContent?.hasVideoContent() == true) {
                            mNativeAd?.mediaContent?.videoController?.play()
                        }

                        callBack?.onLoad()
                        Log.d(logKey, "Native Ad displayed.")
                        loadNativeAd(activity, nativeAdId, null)
                    }
                } else {
                    loadNativeAd(activity, nativeAdId, null)
                }
        }

        fun checkInternetConnection(activity: Activity): Boolean {
            //Check internet connection:
            val connectivityManager: ConnectivityManager? =
                    activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?

            //Means that we are connected to a network (mobile or wi-fi)
            return connectivityManager!!.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)!!.state === NetworkInfo.State.CONNECTED ||
                    connectivityManager!!.getNetworkInfo(ConnectivityManager.TYPE_WIFI)!!.state === NetworkInfo.State.CONNECTED
        }
        fun getNavigationCount(activity: Activity?): Int {
            val sharedPreferences =
                activity!!.getSharedPreferences("navLimit", Context.MODE_PRIVATE)
            return sharedPreferences.getInt("navLimit", 0)
        }

        fun manageNavigationLimit(activity: Activity): Boolean{
            val sharedPreferences = activity.getSharedPreferences(
                    "navLimit",
                    AppCompatActivity.MODE_PRIVATE
            )
            val editor = sharedPreferences.edit()
            editor.putInt("navLimit", getNavigationCount(activity) + 1)
            editor.apply()

            return getNavigationCount(activity) < navigationLimit
        }

    }

}