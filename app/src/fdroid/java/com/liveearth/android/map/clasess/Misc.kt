package com.liveearth.android.map.clasess

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.util.Log
import android.view.Display
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
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

        var mInterstitialAd: InterstitialAd? = null
        var mNativeAd: com.google.android.gms.ads.nativead.NativeAd? = null

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

        var nativeAdId = "ca-app-pub-3940256099942544/2247696110"
        var interstitialAdId = "ca-app-pub-3940256099942544/1033173712"

        var route: DirectionsRoute? = null

        var inAppKey = if (BuildConfig.DEBUG) {
            "android.test.purchased"
        } else {
            "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAujKAuRSsOBeupTizEzEJprzT8ZBba12bbHN9bS4Fz3S8rmfRLC1VZ0MBb56tkq2JmuqAp1bmMRu2yYJGLCck5ZxV67QthUhYpWLccmEP89cdz6HgUtQvsihRAsO29JUOnaL/Zc+quFvf13+dHR/8tN4ySIFcQ6w29NxACbHdlUfngdEbaxrP/z8dk6bFkbGmNabH4DqDv3+gURWZuaT4OnK86dVLJRzoiGcG6wJJY4nxhj6gCh78O9rGbQkJi+hY4kQ+OMM0evOqTNVn4fSahFAGJmDya5nr57i9xREslI3JT7Y+vzNXDvJmNuqyLvAEzYZvUt/hdKzyy4MearU08QIDAQAB"
        }

//        fun startActivity(
//            activity: Activity,
//            isIntEnabled: Boolean,
//            callBack: StartActivityCallBack?
//        ) {
//            callBack?.onStart()
//            showInterstitial(activity,
//                isIntEnabled,
//                object : InterstitialCallBack {
//                    override fun onDismiss() {
//                        callBack?.onStart()
//                    }
//                })
//        }

//        fun onBackPress(
//            activity: Activity,
//            inIntEnabled: Boolean,
//            callBack: OnBackPressCallBack?
//        ) {
//            showInterstitial(activity, inIntEnabled, object : InterstitialCallBack {
//                override fun onDismiss() {
//                    callBack?.onBackPress()
//                }
//            })
//        }

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

        fun loadInterstitial(
            activity: Activity,
            isEnabled: Boolean,
            callback: LoadInterstitialCallBack?
        ) {
            if (!getPurchasedStatus(activity) && intFailedCount < 3 && checkInternetConnection(activity)) {
                if (isEnabled) {
                    val adRequest = AdRequest.Builder().build()
                    InterstitialAd.load(
                        activity,
                        interstitialAdId,
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
                                mInterstitialAd = null
                                callback?.onFailed()
                            }

                            override fun onAdLoaded(p0: InterstitialAd) {
                                mInterstitialAd = p0
                                intFailedCount = 0
                                Log.d(logKey, "Interstitial Ad loaded.")
                                callback?.onLoaded()
                            }
                        }
                    )
                }
                else {
                    callback?.onFailed()
                }
            } else {
                callback?.onFailed()
            }
        }


        fun showInterstitial(
            activity: Activity,
            callBack: InterstitialCallBack?
        ) {
            if (getPurchasedStatus(activity)) {
                callBack?.onDismiss()
                return
            }
            if (mInterstitialAd != null) {
                mInterstitialAd?.show(activity)
            } else {
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
                }

                override fun onAdShowedFullScreenContent() {
                    Log.d(logKey, "Ad showed fullscreen content.")
                    mInterstitialAd = null
                }
            }

            if (!getPurchasedStatus(activity)) {
                if (mInterstitialAd == null) {
                    callBack?.onDismiss()
                }
            }
        }

        fun showNativeAd(
            activity: Activity,
            nativeAdTemplateView: TemplateView,
            callBack: NativeAdCallBack?
        ) {
            if (!getPurchasedStatus(activity))
                if (mNativeAd != null) {
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
                }
        }

        fun loadNativeAd(
            activity: Activity,
            isEnabled: Boolean,
        ) {
            mNativeAd = null
            if (!getPurchasedStatus(activity) && isEnabled) {
                val adLoader: AdLoader =
                    AdLoader.Builder(activity, nativeAdId)
                        .forNativeAd { nativeAd ->
                            Log.d(logKey, "Native Ad Loaded")

                            nativeFailedCount = 0

                            mNativeAd = nativeAd
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
    }
}