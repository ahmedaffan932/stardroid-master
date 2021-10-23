package com.google.android.stardroid.clasess

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.location.Location
import android.net.Uri
import android.os.Handler
import android.provider.MediaStore
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.google.android.stardroid.interfaces.ActivityOnBackPress
import com.google.android.stardroid.interfaces.OnImageSaveCallBack
import com.google.android.stardroid.interfaces.StartActivityCallBack
import com.example.qrcodescanner.activities.sdk29AndUp
import com.google.android.stardroid.R
import com.google.firebase.storage.FirebaseStorage
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class Misc {
    companion object {
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
        const val data: String = "data"

        const val logKey: String = "logKey"

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
        var isCompassIntEnabled: Boolean = false
        var isNoteCamIntEnabled: Boolean = false
        var isAltitudeIntEnabled: Boolean = false
        var isLiveEarthIntEnabled: Boolean = false
        var isViewWorldIntEnabled: Boolean = false
        var isStartGameIntEnabled: Boolean = false
        var isSoundMeterIntEnabled: Boolean = false
        var isGPSMapCamsIntEnabled: Boolean = false
        var isSpeedometerIntEnabled: Boolean = false
        var isQuizCountriesIntEnabled: Boolean = false
        var isNoteCamOnBackIntEnabled: Boolean = false
        var isQuizCurrenciesIntEnabled: Boolean = false
        var isQuizCompeletedIntEnabled: Boolean = false
        var isLiveEarthOnBackIntEnabled: Boolean = false
        var isGenerateQrOnBackIntEnabled: Boolean = false

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
    }

}