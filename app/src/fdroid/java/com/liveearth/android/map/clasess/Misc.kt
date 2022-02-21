package com.liveearth.android.map.clasess

import java.util.*
import android.Manifest
import android.net.Uri
import android.os.Build
import android.util.Log
import android.content.*
import android.view.View
import java.io.IOException
import android.app.Activity
import android.graphics.Bitmap
import android.net.NetworkInfo
import android.location.Location
import java.text.SimpleDateFormat
import android.provider.MediaStore
import com.liveearth.android.map.R
import com.google.android.gms.ads.*
import android.annotation.SuppressLint
import android.net.ConnectivityManager
import android.view.animation.Animation
import android.content.pm.PackageManager
import android.view.animation.AnimationUtils
import com.liveearth.android.map.BuildConfig
import com.liveearth.android.map.interfaces.*
import android.graphics.drawable.ColorDrawable
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.ads.MaxInterstitialAd
import com.applovin.mediation.nativeAds.MaxNativeAdListener
import com.applovin.mediation.nativeAds.MaxNativeAdLoader
import com.applovin.mediation.nativeAds.MaxNativeAdView
import com.applovin.mediation.nativeAds.MaxNativeAdViewBinder
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.google.android.ads.nativetemplates.TemplateView
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.ads.nativetemplates.NativeTemplateStyle
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

class Misc {
    @SuppressLint("LogNotTimber")
    companion object {
        var isContinentSelectIntEnabled: Boolean = true
        var isStartGameIntEnabled: Boolean = true
        var isMainFromProScreenIntEnabled: Boolean = true
        var isGenerateQRIntEnabled: Boolean = true
        var isQuizCompleteIntEnabled: Boolean = true
        var isSkyMapIntEnabled: Boolean = true
        var isQuizCountriesIntEnabled: Boolean = true
        var isQuizScreenOneNativeEnabled: Boolean = true
        var isQuizScreenOneBackIntEnabled: Boolean = true
        var isQuizSelectModeIntEnabled: Boolean = true
        var isContinentSelectNativeEnabled: Boolean = true
        var isQuizCompleteBackIntEnabled: Boolean = true
        var isWorldQuizOnBackIntEnabled: Boolean = true
        var isQuizCompleteNativeEnabled: Boolean = true
        var isWordlQuizActivityNativeEnabled: Boolean = true
        var isContinentSelectBackIntEnabled: Boolean = true
        var isQuizSelectModeNativeEnabled: Boolean = true
        var isSoundMeterBackIntEnabled: Boolean = true
        var isSoundMeterNativeEnabled: Boolean = true
        var isSkyMapBackIntEnabled: Boolean = true
        var isSettingBackIntEnabled: Boolean = true
        var isGenerateQrOnBackIntEnabled: Boolean = true
        var isLiveEarthOnBackIntEnabled: Boolean = true
        var isCompassBackIntEnabled: Boolean = true
        var isViewWorldBackIntEnabled: Boolean = true
        var isSplashNativeEnabled: Boolean = true
        var isAltitudeBackIntEnabled: Boolean = true
        lateinit var nativeAdLoader: MaxNativeAdLoader

        @SuppressLint("StaticFieldLeak")
        var nativeAdView: MaxNativeAdView? = null
        var nativeAdId: String = "abcd"
        var mNativeAd: MaxAd? = null
        var mInterstitialAd: MaxInterstitialAd? = null
        const val appUrl: String =
            "https://play.google.com/store/apps/details?id=com.liveearth.android.map.liveearthmap.liveearthcam.streetview.gps.map.worldmap.satellite.app"
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
        var navigationLimit = 3
        const val data: String = "data"
        var intFailedCount = 0
        var nativeFailedCount = 0
        var isRemoteConfigFetched = false
        const val logKey: String = "logKey"
        private const val purchasedStatus: String = "purchasedStatus"

        var mInterstitialAdAdMob: InterstitialAd? = null
        var mNativeAdAdMob: com.google.android.gms.ads.nativead.NativeAd? = null
        var interstitialAdId = "asdlk"

        const val flags: String = "flags"
        const val capitals: String = "capitals"

        const val countries: String = "countries"
        private const val flash: String = "flash"

        private const val lastUri: String = "lastUri"
        private const val cameraFace: String = "cameraFace"

        var location: Location? = null
        var isSplashIntEnabled: Boolean = true
        var isDashboardIntEnabled: Boolean = true
        var isDashboardNativeEnabled: Boolean = true

        var nativeAdIdAdMob = "ca-app-pub-3940256099942544/2247696110"
        var interstitialAdIdAdMob = "ca-app-pub-3940256099942544/1033173712"

        var route: DirectionsRoute? = null

        var inAppKey = if (BuildConfig.DEBUG) {
            "android.test.purchased"
        } else {
            "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAujKAuRSsOBeupTizEzEJprzT8ZBba12bbHN9bS4Fz3S8rmfRLC1VZ0MBb56tkq2JmuqAp1bmMRu2yYJGLCck5ZxV67QthUhYpWLccmEP89cdz6HgUtQvsihRAsO29JUOnaL/Zc+quFvf13+dHR/8tN4ySIFcQ6w29NxACbHdlUfngdEbaxrP/z8dk6bFkbGmNabH4DqDv3+gURWZuaT4OnK86dVLJRzoiGcG6wJJY4nxhj6gCh78O9rGbQkJi+hY4kQ+OMM0evOqTNVn4fSahFAGJmDya5nr57i9xREslI3JT7Y+vzNXDvJmNuqyLvAEzYZvUt/hdKzyy4MearU08QIDAQAB"
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

        fun manageNavigationLimit(activity: Activity): Boolean {
            val sharedPreferences = activity.getSharedPreferences(
                "navLimit",
                AppCompatActivity.MODE_PRIVATE
            )
            val editor = sharedPreferences.edit()
            editor.putInt("navLimit", getNavigationCount(activity) + 1)
            editor.apply()

            return getNavigationCount(activity) < navigationLimit
        }

        fun getStoragePermission(
            activity: Activity,
            storageReadPermissionRequest: Int,
            storagePermissionInterface: StoragePermissionInterface
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    activity.requestPermissions(
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        storageReadPermissionRequest
                    )
                } else {
                    storagePermissionInterface.onPermissionGranted()
                }
            } else {
                storagePermissionInterface.onPermissionGranted()
            }
        }

        fun loadAdMobInterstitial(
            activity: Activity,
            isEnabled: Boolean,
            callback: LoadInterstitialCallBack?
        ) {
            if (!getPurchasedStatus(activity) && intFailedCount < 3 && checkInternetConnection(activity)) {
                if (isEnabled) {
                    val adRequest = AdRequest.Builder().build()
                    InterstitialAd.load(
                        activity,
                        interstitialAdIdAdMob,
                        adRequest,
                        object : InterstitialAdLoadCallback() {
                            override fun onAdFailedToLoad(adError: LoadAdError) {
                                Log.d(logKey, adError.message)
                                val clipboard: ClipboardManager =
                                    activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip =
                                    ClipData.newPlainText("Camera Translator", adError.message)
                                clipboard.setPrimaryClip(clip)
                                Log.e(logKey, "Interstitial ad load failed.")
                                intFailedCount++
                                mInterstitialAdAdMob = null
                                callback?.onFailed()
                            }

                            override fun onAdLoaded(p0: InterstitialAd) {
                                mInterstitialAdAdMob = p0
                                intFailedCount = 0
                                Log.d(logKey, "Interstitial Ad loaded.")
                                callback?.onLoaded()
                            }
                        }
                    )
                } else {
                    callback?.onFailed()
                }
            } else {
                callback?.onFailed()
            }
        }


        fun showAdMobInterstitial(activity: Activity, callBack: InterstitialCallBack?) {
            if (getPurchasedStatus(activity)) {
                callBack?.onDismiss()
                return
            }
            if (mInterstitialAdAdMob != null) {
                mInterstitialAdAdMob?.show(activity)
            } else {
                callBack?.onDismiss()
                Log.d("TAG", "The interstitial ad wasn't ready yet.")
                return
            }

            mInterstitialAdAdMob?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    callBack?.onDismiss()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError?) {
                    callBack?.onDismiss()
                }

                override fun onAdShowedFullScreenContent() {
                    Log.d(logKey, "Ad showed fullscreen content.")
                    mInterstitialAdAdMob = null
                }
            }

            if (!getPurchasedStatus(activity)) {
                if (mInterstitialAdAdMob == null) {
                    callBack?.onDismiss()
                }
            }
        }

        fun showAdMobNativeAd(
            activity: Activity,
            nativeAdTemplateView: TemplateView,
            callBack: NativeAdCallBack?
        ) {
            if (!getPurchasedStatus(activity))
                if (mNativeAdAdMob != null) {
                    val styles =
                        NativeTemplateStyle.Builder()
                            .withMainBackgroundColor(ColorDrawable())
                            .build()
                    nativeAdTemplateView.setStyles(styles)
                    nativeAdTemplateView.setNativeAd(mNativeAdAdMob)

                    if (mNativeAdAdMob?.mediaContent?.hasVideoContent() == true) {
                        mNativeAdAdMob?.mediaContent?.videoController?.play()
                    }

                    callBack?.onLoad()
                    Log.d(logKey, "Native Ad displayed.")
                }
        }

        fun loadAdMobNativeAd(
            activity: Activity,
            isEnabled: Boolean,
        ) {
            mNativeAdAdMob = null
            if (!getPurchasedStatus(activity) && isEnabled) {
                val adLoader: AdLoader =
                    AdLoader.Builder(activity, nativeAdIdAdMob)
                        .forNativeAd { nativeAd ->
                            Log.d(logKey, "Native Ad Loaded")

                            nativeFailedCount = 0

                            mNativeAdAdMob = nativeAd
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
                                Log.e(logKey, adError.message)
                            }
                        })
                        .build()
                adLoader.loadAd(AdRequest.Builder().build())
            }
        }


        fun loadInterstitial(activity: Activity, callback: LoadInterstitialCallBack?) {
            if (!getPurchasedStatus(activity) && interstitialAdId != "" && checkInternetConnection(
                    activity
                )
            ) {
                try {
                    mInterstitialAd = MaxInterstitialAd(interstitialAdId, activity)
                    mInterstitialAd?.loadAd()
                    callback?.onLoaded()
                } catch (e: Exception) {
                    callback?.onFailed()
                    e.printStackTrace()
                }
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
                    if (mInterstitialAd?.isReady == true) {
                        mInterstitialAd?.showAd()
                    } else {
                        callBack?.onDismiss()
                    }
                } else {
                    loadInterstitial(activity, null)
                    callBack?.onDismiss()
                    Log.d(logKey, "The interstitial ad wasn't ready yet.")
                    return
                }

                val maxAdListener: MaxAdListener = object : MaxAdListener {
                    override fun onAdLoaded(ad: MaxAd) {
                        intFailedCount = 0
                        Log.d(logKey, "Interstitial Ad loaded.")
                    }

                    override fun onAdDisplayed(ad: MaxAd) {
                        Log.d(logKey, "Ad showed fullscreen content.")
                        mInterstitialAd = null
//                        callBack?.onDismiss()
                        loadInterstitial(activity, null)
                    }

                    override fun onAdHidden(ad: MaxAd) {
                        callBack?.onDismiss()
                    }

                    override fun onAdClicked(ad: MaxAd) {}
                    override fun onAdLoadFailed(adUnitId: String, error: MaxError) {
                        Log.d(logKey, error.message)
                        val clipboard: ClipboardManager =
                            activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip =
                            ClipData.newPlainText("Camera Translator", error.message)
                        clipboard.setPrimaryClip(clip)
                        Log.e(logKey, error.message)
                        intFailedCount++
                        callBack?.onDismiss()
                        mInterstitialAd = null
                    }

                    override fun onAdDisplayFailed(ad: MaxAd, error: MaxError) {
                        callBack?.onDismiss()
                        loadInterstitial(activity, null)
//                        mInterstitialAd?.loadAd()
                    }
                }
                mInterstitialAd?.setListener(maxAdListener)

            } else {
                callBack?.onDismiss()
            }
            if (!getPurchasedStatus(activity)) {
                if (mInterstitialAd == null) {
                    callBack?.onDismiss()
                    loadInterstitial(activity, null)
                }
            }
        }


        fun showNativeAd(
            activity: Activity,
            adFrameLayout: FrameLayout,
            isEnabled: Boolean,
            callBack: NativeAdCallBack?
        ) {
            try {
                if (!getPurchasedStatus(activity))
                    if (mNativeAd != null) {
                        if (isEnabled) {
                            callBack?.onLoad()
                            Log.d(logKey, "Native Ad displayed.")
                            adFrameLayout.removeAllViews()
                            adFrameLayout.addView(nativeAdView)
                            adFrameLayout.visibility = View.VISIBLE
                            loadNativeAd(activity, null)
                        }
                    } else {
                        loadNativeAd(activity, null)
                    }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fun loadNativeAd(activity: Activity, callBack: NativeAdCallBack?) {
            try {
                if (!getPurchasedStatus(activity) && nativeAdId != "" && checkInternetConnection(
                        activity
                    )
                ) {
                    nativeAdLoader = MaxNativeAdLoader(nativeAdId, activity)
                    nativeAdLoader.setNativeAdListener(object : MaxNativeAdListener() {

                        override fun onNativeAdLoaded(nativeAdView: MaxNativeAdView, ad: MaxAd) {
//                            if (mNativeAd != null) {
//                                nativeAdLoader.destroy(mNativeAd)
//                            }

                            mNativeAd = ad
                            Companion.nativeAdView = nativeAdView
                            nativeFailedCount = 0
                            callBack?.onLoad()
                        }

                        override fun onNativeAdLoadFailed(adUnitId: String, error: MaxError) {
                            Log.d(logKey, "Applovin Native ad load failed.")
                            val clipboard: ClipboardManager =
                                activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip =
                                ClipData.newPlainText("Camera Translator", error.message)
                            clipboard.setPrimaryClip(clip)
                            nativeFailedCount++
                            loadNativeAd(activity, null)
                        }

                        override fun onNativeAdClicked(ad: MaxAd) {
                            // Optional click callback
                            Log.d(logKey, "Applovin native ad clicked.")
                        }
                    })

                    nativeAdLoader.loadAd(createNativeAdView(activity))
                } else {
                    Log.d(logKey, "Native ad Id = null")
                }
            } catch (e: Exception) {
                Log.d(logKey, "Exception")
                e.printStackTrace()
            }
        }

        private fun createNativeAdView(activity: Activity): MaxNativeAdView {
            val binder: MaxNativeAdViewBinder =
                MaxNativeAdViewBinder.Builder(R.layout.applovin_native)
                    .setTitleTextViewId(R.id.title_text_view)
//                    .setBodyTextViewId(R.id.body_text_view)
                    .setAdvertiserTextViewId(R.id.advertiser_textView)
                    .setIconImageViewId(R.id.icon_image_view)
//                    .setMediaContentViewGroupId(R.id.media_view_container)
                    .setMediaContentViewGroupId(R.id.media_view_container)
                    .setOptionsContentViewGroupId(R.id.options_view)
                    .setCallToActionButtonId(R.id.cta_button)
                    .build()
            return MaxNativeAdView(binder, activity)
        }

//        fun onBackPress(activity: Activity, isEnabled: Boolean, callBack: InterstitialCallBack?){
//            showInterstitial(activity, isEnabled, callBack)
//        }

    }
}