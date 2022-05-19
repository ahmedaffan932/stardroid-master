package com.liveearth.android.map

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.liveearth.android.map.clasess.Ads
import com.liveearth.android.map.clasess.Misc
import com.liveearth.android.map.interfaces.InterstitialCallBack
import kotlinx.android.synthetic.main.activity_note_cam.*
import kotlinx.android.synthetic.main.note_cam_bottom_sheet.*
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener
import java.io.File
import java.io.IOException
import java.util.*

class NoteCamActivity : BaseActivity() {
    private lateinit var locationCallback: LocationCallback
    private lateinit var imageCapture: ImageCapture
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private lateinit var lastUri: Uri
    var cameraProvider: ProcessCameraProvider? = null
    var isAddNote = false

    @SuppressLint("LogNotTimber", "MissingPermission", "SetTextI18n")
   override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_note_cam)

        getImageForCollection()
        bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.bottomSheetNoteCam))
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        KeyboardVisibilityEvent.setEventListener(this, object : KeyboardVisibilityEventListener {
            override fun onVisibilityChanged(isOpen: Boolean) {
                if (isOpen) {
                    btnCapture.visibility = View.INVISIBLE
                } else {
                    btnCapture.visibility = View.VISIBLE
                }
            }
        })

        btnSavePhotoNote.setOnClickListener {
            hideSoftKeyboard(this)
            if (tvNoteNoteCam.text.toString() == "") {
                Toast.makeText(
                    this@NoteCamActivity,
                    "Please enter some note.",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                tvNoteNoteCam.visibility = View.GONE
                etNoteNoteCam.clearFocus()
                Handler().postDelayed({
                    val bitmap = Bitmap.createBitmap(
                        clPreviewImageView.width,
                        clPreviewImageView.height,
                        Bitmap.Config.ARGB_8888
                    )
                    val canvas = Canvas(bitmap)
                    clPreviewImageView.draw(canvas)

                    val tempUri =
                        Misc.saveImageToExternal(
                            this@NoteCamActivity,
                            bitmap,
                            null
                        )
                    Misc.setLatestUri(tempUri.toString(), this@NoteCamActivity)

                    getImageForCollection()

                    bottomSheetBehavior.state =
                        BottomSheetBehavior.STATE_COLLAPSED
                    tvNoteNoteCam.visibility = View.VISIBLE
                }, 10)
            }
        }


        etPhotoNote.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(
                s: CharSequence?, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                etNoteNoteCam.text = etPhotoNote.text
            }
        })

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    previewImageNoteCam.visibility = View.GONE
                }
            }
        })
        startCamera()
        btnChangeCameraFace.setOnClickListener {
            Misc.setCameraFace(this, !Misc.getCameraFace(this))
            startCamera()
        }

        if (intent.getStringExtra(Misc.data) != null) {
            tvLatitudeNoteCam.visibility = View.VISIBLE
            tvLongitudeNoteCam.visibility = View.VISIBLE
            tvElevationNoteCam.visibility = View.VISIBLE
            tvAccuracyNoteCam.visibility = View.VISIBLE
            tvTimeNoteCam.visibility = View.VISIBLE
            tvAddress.visibility = View.VISIBLE
            tvNoteNoteCam.visibility = View.GONE
            textViewNoteCam.text = "GPS Map Cam"

            btnCapture.setOnClickListener {
//                Ads.showInterstitial(
//                    this,
//                    Misc.isBtnClickIntEnable,
//                    object : InterstitialCallBack {
//                        override fun onDismiss() {

                            val file = File(
                                externalMediaDirs.firstOrNull(),
                                ".GpsToolbox - ${System.currentTimeMillis()}.jpg"
                            )

                            val outPut = ImageCapture.OutputFileOptions.Builder(file).build()
                            imageCapture.takePicture(
                                outPut,
                                ContextCompat.getMainExecutor(this@NoteCamActivity),
                                object : ImageCapture.OnImageCapturedCallback(),
                                    ImageCapture.OnImageSavedCallback {
                                    @SuppressLint("SetTextI18n")
                                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                                        val uri = outputFileResults.savedUri

                                        previewImageNoteCam.setImageURI(uri)
                                        previewImageNoteCam.visibility = View.VISIBLE

                                        Handler().postDelayed({
                                            val bitmap = Bitmap.createBitmap(
                                                clPreviewImageView.width,
                                                clPreviewImageView.height,
                                                Bitmap.Config.ARGB_8888
                                            )
                                            val canvas = Canvas(bitmap)
                                            clPreviewImageView.draw(canvas)

                                            val tempUri =
                                                Misc.saveImageToExternal(
                                                    this@NoteCamActivity,
                                                    bitmap,
                                                    null
                                                )
                                            Misc.setLatestUri(
                                                tempUri.toString(),
                                                this@NoteCamActivity
                                            )

                                            getImageForCollection()

                                            bottomSheetBehavior.state =
                                                BottomSheetBehavior.STATE_COLLAPSED
                                        }, 10)
                                    }
                                }
                            )
//                        }
//                    })
            }

        } else {
            tvNoteNoteCam.visibility = View.VISIBLE
            clNote.setOnClickListener {
                etNoteNoteCam.requestFocus()
                val imm: InputMethodManager =
                    getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(etNoteNoteCam, InputMethodManager.SHOW_IMPLICIT)
            }
            btnCapture.setOnClickListener {
//                Ads.showInterstitial(
//                    this,
//                    Misc.isBtnClickIntEnable,
//                    object : InterstitialCallBack {
//                        override fun onDismiss() {
                            val file = File(
                                externalMediaDirs.firstOrNull(),
                                ".GpsToolbox - ${System.currentTimeMillis()}.jpg"
                            )

                            val outPut = ImageCapture.OutputFileOptions.Builder(file).build()
                            imageCapture.takePicture(
                                outPut,
                                ContextCompat.getMainExecutor(this@NoteCamActivity),
                                object : ImageCapture.OnImageCapturedCallback(),
                                    ImageCapture.OnImageSavedCallback {
                                    @SuppressLint("SetTextI18n")
                                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                                        val uri = outputFileResults.savedUri

                                        previewImageNoteCam.setImageURI(uri)
                                        previewImageNoteCam.visibility = View.VISIBLE
                                        bottomSheetBehavior.state =
                                            BottomSheetBehavior.STATE_EXPANDED
                                        etPhotoNote.text = etNoteNoteCam.text
                                        btnSavePhotoNote.text = "Save"
                                        textViewSavePhoto.text = "Save Photo"

                                    }
                                }
                            )
//                        }
//                    })
            }
        }

        btnCancel.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        btnBackNoteCam.setOnClickListener {
            onBackPressed()
        }

    }

    override fun onBackPressed() {
        if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        } else {
//            super.onBackPressed()
            Ads.showInterstitial(this, Misc.noteCamOnBackIntAm_Al, object : InterstitialCallBack {
                override fun onDismiss() {
                    finish()
                }
            })
        }
    }

    @SuppressLint("MissingPermission")
    override fun onResume() {
        super.onResume()

        val bannerAdFrameLayout = if(Misc.isBannerAdTop){
            bannerAdFrameLayoutTop
        }else{
            bannerAdFrameLayoutBottom
        }


        Ads.showBannerAd(Misc.isNoteCamBannerEnabled, bannerAdFrameLayout)

        locationCallback = object : LocationCallback() {
            @SuppressLint("SetTextI18n", "LogNotTimber")
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)
                Log.d(Misc.logKey, p0.toString())
                val loc = p0.lastLocation
                tvLatitudeNoteCam.text = "Latitude: ${Math.round(loc.latitude * 100.0) / 100.0}"
                tvLongitudeNoteCam.text = "Longitude: ${Math.round(loc.longitude * 100.0) / 100.0}"
                tvElevationNoteCam.text = "Altitude: ${Math.round(loc.altitude * 100.0) / 100.0}"
                tvAccuracyNoteCam.text = "Accuracy: ${loc.accuracy}"
                tvTimeNoteCam.text = "Time: ${Misc.timeMillsToHms(loc.time)}"
                getAddress(loc)
            }
        }

        val locationRequest = LocationRequest.create()
        locationRequest.fastestInterval = 1000
        locationRequest.interval = 5000
        LocationServices.getFusedLocationProviderClient(this)
            .requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())

    }

    override fun onPause() {
        super.onPause()
        LocationServices.getFusedLocationProviderClient(this)
            .removeLocationUpdates(locationCallback)
    }

    private fun getImageForCollection() {
        if (Misc.getLastSavedUri(this) != "o") {
            lastUri = Misc.getLastSavedUri(this).toUri()
            imageViewCollection.setImageURI(lastUri)

            clCollection.visibility = View.VISIBLE
            clCollection.setOnClickListener {
                val builder = VmPolicy.Builder()
                StrictMode.setVmPolicy(builder.build())

                val i: Intent?
                val manager = packageManager
//                try {
//                    i = Intent(manager.getLaunchIntentForPackage("com.google.android.apps.photos"))
//
////                    i = manager.getLaunchIntentForPackage("com.google.android.apps.photos")
////                    if (i == null) throw PackageManager.NameNotFoundException()
//
//                    i.action = Intent.ACTION_VIEW
//                    i.setDataAndType(lastUri, "image/*")
//                    startActivity(i)
//                } catch (e: Exception) {
//                    e.printStackTrace()
                i = Intent()
                i.action = Intent.ACTION_VIEW
                i.setDataAndType(lastUri, "image/*")
                startActivity(i)
//                }
            }
        }

    }

    private fun startCamera() {
        if (cameraProvider != null) {
            cameraProvider?.unbindAll()
        }
        imageCapture = ImageCapture.Builder().build()
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProvider = cameraProviderFuture.get()

        val cameraFacing = if (Misc.getCameraFace(this)) {
            CameraSelector.DEFAULT_BACK_CAMERA
        } else {
            CameraSelector.DEFAULT_FRONT_CAMERA
        }

        val cameraRunnable = Runnable {
            val preview = Preview.Builder().build()
            preview.setSurfaceProvider(cameraView.surfaceProvider)
            imageCapture.flashMode = if (Misc.getFlash(this)) {
                1
            } else {
                0
            }

            try {
                cameraProvider?.bindToLifecycle(
                    this,
                    cameraFacing,
                    preview,
                    imageCapture
                )
            }catch (e: Exception){
                Toast.makeText(this, "There is some problem with came, Unable to start it.",Toast.LENGTH_SHORT).show()
                onBackPressed()
            }
        }
        cameraProviderFuture.addListener(
            cameraRunnable,
            ContextCompat.getMainExecutor(this)
        )

    }

    fun hideSoftKeyboard(activity: Activity) {
        val inputMethodManager = activity.getSystemService(
            Activity.INPUT_METHOD_SERVICE
        ) as InputMethodManager
        if (inputMethodManager.isAcceptingText) {
            inputMethodManager.hideSoftInputFromWindow(
                activity.currentFocus!!.windowToken,
                0
            )
        }
    }

    private fun getAddress(p: Location): String? {
        val geocoder = Geocoder(this, Locale.getDefault())
        val addresses: List<Address>?
        return try {
            addresses = geocoder.getFromLocation(p.latitude, p.longitude, 1)
            if (!(addresses == null || addresses.isEmpty())) {
                tvAddress.text =
                    addresses[0].getAddressLine(0)

                addresses[0].countryName
            } else "null"
        } catch (ignored: IOException) {
            ignored.printStackTrace()
            "Exception"
        }
    }
}
