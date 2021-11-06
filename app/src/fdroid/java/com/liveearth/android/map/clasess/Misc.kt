package com.liveearth.android.map.clasess

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.location.Location
import android.net.Uri
import android.provider.MediaStore
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.example.qrcodescanner.activities.sdk29AndUp
import com.liveearth.android.map.BuildConfig
import com.liveearth.android.map.R
import com.liveearth.android.map.interfaces.ActivityOnBackPress
import com.liveearth.android.map.interfaces.OnImageSaveCallBack
import com.liveearth.android.map.interfaces.StartActivityCallBack
import com.mapbox.api.directions.v5.models.DirectionsRoute
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class Misc {
    companion object {

        const val appUrl: String =
            "https://play.google.com/store/apps/details?id=com.guru.translate.translator.translation.learn.language"
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
        var navigationLimit = 4
        const val data: String = "data"

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
        var isSkyMapIntEnabled: Boolean = false
        var isSplashIntEnabled: Boolean = false
        var isSettingIntEnabled: Boolean = false
        var isCompassIntEnabled: Boolean = false
        var isNoteCamIntEnabled: Boolean = false
        var isAltitudeIntEnabled: Boolean = false
        var isProScreenIntEnabled: Boolean = false
        var isLiveEarthIntEnabled: Boolean = false
        var isViewWorldIntEnabled: Boolean = false
        var isStartGameIntEnabled: Boolean = false
        var isSoundMeterIntEnabled: Boolean = false
        var isNavigationIntEnabled: Boolean = false
        var isGPSMapCamsIntEnabled: Boolean = false
        var isSpeedometerIntEnabled: Boolean = false
        var isQuizCountriesIntEnabled: Boolean = false
        var isNoteCamOnBackIntEnabled: Boolean = false
        var isQuizCurrenciesIntEnabled: Boolean = false
        var isQuizCompeletedIntEnabled: Boolean = false
        var isLiveEarthOnBackIntEnabled: Boolean = false
        var isGenerateQrOnBackIntEnabled: Boolean = false

        var route: DirectionsRoute? = null

        var inAppKey = if (BuildConfig.DEBUG) {
            "android.test.purchased"
        } else {
            ""
        }

        fun startActivity(
            activity: Activity,
            isIntEnabled: Boolean,
            callBack: StartActivityCallBack?
        ) {
            callBack?.onStart()
        }

        fun backActivity(
            activity: Activity,
            inIntEnabled: Boolean,
            callBack: ActivityOnBackPress?
        ) {

            callBack?.onBackPress()
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