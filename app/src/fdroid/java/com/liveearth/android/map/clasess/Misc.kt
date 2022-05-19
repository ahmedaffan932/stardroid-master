package com.liveearth.android.map.clasess

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.applovin.mediation.MaxAd
import com.applovin.mediation.ads.MaxInterstitialAd
import com.applovin.mediation.nativeAds.MaxNativeAdLoader
import com.applovin.mediation.nativeAds.MaxNativeAdView
import com.liveearth.android.map.BuildConfig
import com.liveearth.android.map.R
import com.liveearth.android.map.interfaces.OnImageSaveCallBack
import com.liveearth.android.map.interfaces.StoragePermissionInterface
import com.mapbox.api.directions.v5.models.DirectionsRoute
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class Misc {
    @SuppressLint("LogNotTimber")
    companion object {
        var adBreakLimit = 2
        var frequencyCappingAdMobLimit: Int = 2
        var frequencyCappingApplovinLimit: Int = 2

        var adBreakCount: Int = 0
        var frequencyCappingAdMobCount = 0
        var frequencyCappingApplovinCount: Int = 0

        var isBannerAdTop: Boolean = true
        var isSplashLargeNative: Boolean = true
        var isSkyMapBannerEnabled: Boolean = true
        var isCompassBannerEnabled: Boolean = true
        var isNoteCamBannerEnabled: Boolean = true
        var isDashboardBannerEnabled: Boolean = true
        var isProScreenBannerEnabled: Boolean = true


        var lsvIntAm_al: String = "am_al"
        var skyMapIntAm_al: String = "am_al"
        var gpsCamIntAm_al: String = "am_al"
        var isQuitIntAm_Al: String = "am_al"
        var compassIntAm_al: String = "am_al"
        var noteCamIntAm_al: String = "am_al"
        var quitNativeAm_Al: String = "am_al"
        var isSkyMapIntAm_Al: String = "am_al"
        var altitudeIntAm_al: String = "am_al"
        var isSplashIntAm_al: String = "am_al"
        var worldQuizIntAm_al: String = "am_al"
        var splashNativeAm_Al: String = "am_al"
        var startGameIntAm_Al: String = "am_al"
        var generateQRIntAm_Al: String = "am_al"
        var soundMeterIntAm_al: String = "am_al"
        var skyMapBackIntAm_Al: String = "am_al"
        var settingBackIntAm_Al: String = "am_al"
        var compassBackIntAm_Al: String = "am_al"
        var speedometerIntAm_al: String = "am_al"
        var createQRNativeAm_Al: String = "am_al"
        var dashboardNativeAm_Al: String = "am_al"
        var altitudeBackIntAm_Al: String = "am_al"
        var quizCompleteIntAm_Al: String = "am_al"
        var quizCountriesIntAm_Al: String = "am_al"
        var viewWorldBackIntAm_Al: String = "am_al"
        var noteCamOnBackIntAm_Al: String = "am_al"
        var soundMeterNativeAm_Al: String = "am_al"
        var speedoMeterNativeAm_Al: String = "am_al"
        var soundMeterBackIntAm_Al: String = "am_al"
        var quizSelectModeIntAm_Al: String = "am_al"
        var quizCompleteNativeAm_Al: String = "am_al"
        var worldQuizOnBackIntAm_Al: String = "am_al"
        var continentSelectIntAm_Al: String = "am_al"
        var liveEarthOnBackIntAm_Al: String = "am_al"
        var generateQrOnBackIntAm_Al: String = "am_al"
        var quizCompleteBackIntAm_Al: String = "am_al"
        var quizScreenOneNativeAm_Al: String = "am_al"
        var mainFromProScreenIntAm_Al: String = "am_al"
        var quizScreenOneBackIntAm_Al: String = "am_al"
        var quizSelectModeNativeAm_Al: String = "am_al"
        var continentSelectNativeAm_Al: String = "am_al"
        var continentSelectBackIntAm_Al: String = "am_al"
        var worldQuizActivityNativeAm_Al: String = "am_al"

        var bannerAdId: String = "zsdf"
        var nativeAdIdApplovin: String = "abcd"
        var interstitialAdIdApplovin: String = "okasd"

        var nativeAdIdAdMob: String = "ca-app-pub-3940256099942544/2247696110"
        var interstitialAdIdAdMob: String = "ca-app-pub-3940256099942544/1033173712"

        //Not Required.
        var mRecAdId = "dsadf"

        @SuppressLint("StaticFieldLeak")
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


        const val flags: String = "flags"
        const val capitals: String = "capitals"

        const val countries: String = "countries"
        private const val flash: String = "flash"

        private const val lastUri: String = "lastUri"
        private const val cameraFace: String = "cameraFace"

        var location: Location? = null


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
            return try {
                //Check internet connection:
                val connectivityManager: ConnectivityManager? =
                    activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?

                //Means that we are connected to a network (mobile or wi-fi)
                connectivityManager!!.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)!!.state === NetworkInfo.State.CONNECTED ||
                        connectivityManager!!.getNetworkInfo(ConnectivityManager.TYPE_WIFI)!!.state === NetworkInfo.State.CONNECTED
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
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


    }
}