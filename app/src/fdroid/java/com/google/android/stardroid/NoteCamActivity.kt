package com.google.android.stardroid

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.google.android.stardroid.clasess.Misc
import com.google.android.stardroid.interfaces.ActivityOnBackPress
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.activity_note_cam.*
import kotlinx.android.synthetic.main.note_cam_bottom_sheet.*
import java.io.File

class NoteCamActivity : BaseActivity() {
    private lateinit var locationCallback: LocationCallback
    private lateinit var imageCapture: ImageCapture
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private lateinit var lastUri: Uri
    var cameraProvider: ProcessCameraProvider? = null

    @SuppressLint("LogNotTimber", "MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_cam)

        getImageForCollection()
        bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.bottomSheetNoteCam))
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    previewImageNoteCam.visibility = View.GONE
                    tvNoteNoteCam.visibility = View.GONE
                }
            }
        })
        startCamera()
        btnChangeCameraFace.setOnClickListener{
            Misc.setCameraFace(this,!Misc.getCameraFace(this))
            startCamera()
        }

        if (intent.getStringExtra(Misc.data) != null) {
            tvLatitudeNoteCam.visibility = View.VISIBLE
            tvLongitudeNoteCam.visibility = View.VISIBLE
            tvElevationNoteCam.visibility = View.VISIBLE
            tvAccuracyNoteCam.visibility = View.VISIBLE
            tvTimeNoteCam.visibility = View.VISIBLE
            tvNoteNoteCam.visibility = View.GONE

            btnCapture.setOnClickListener {

                val file = File(
                    externalMediaDirs.firstOrNull(),
                    ".GpsToolbox - ${System.currentTimeMillis()}.jpg"
                )

                val outPut = ImageCapture.OutputFileOptions.Builder(file).build()
                imageCapture.takePicture(
                    outPut,
                    ContextCompat.getMainExecutor(this),
                    object : ImageCapture.OnImageCapturedCallback(),
                        ImageCapture.OnImageSavedCallback {
                        @SuppressLint("SetTextI18n")
                        override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                            val uri = outputFileResults.savedUri

                            previewImageNoteCam.setImageURI(uri)
                            previewImageNoteCam.visibility = View.VISIBLE
//                            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

                            Handler().postDelayed({
                                val bitmap = Bitmap.createBitmap(
                                    clPreviewImageView.width,
                                    clPreviewImageView.height,
                                    Bitmap.Config.ARGB_8888
                                )
                                val canvas = Canvas(bitmap)
                                clPreviewImageView.draw(canvas)

                                val tempUri =
                                    Misc.saveImageToExternal(this@NoteCamActivity, bitmap, null)
                                Misc.setLatestUri(tempUri.toString(), this@NoteCamActivity)

                                getImageForCollection()

                                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                            }, 10)
                        }
                    }
                )
            }

        } else {
            tvNoteNoteCam.visibility = View.VISIBLE
            btnCapture.setOnClickListener {
                val file = File(
                    externalMediaDirs.firstOrNull(),
                    ".GpsToolbox - ${System.currentTimeMillis()}.jpg"
                )

                val outPut = ImageCapture.OutputFileOptions.Builder(file).build()
                imageCapture.takePicture(
                    outPut,
                    ContextCompat.getMainExecutor(this),
                    object : ImageCapture.OnImageCapturedCallback(),
                        ImageCapture.OnImageSavedCallback {
                        @SuppressLint("SetTextI18n")
                        override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                            val uri = outputFileResults.savedUri

                            previewImageNoteCam.setImageURI(uri)
                            previewImageNoteCam.visibility = View.VISIBLE
                            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

                            btnSavePhotoNote.setOnClickListener {
                                if (etPhotoNote.text.toString() == "") {
                                    Toast.makeText(
                                        this@NoteCamActivity,
                                        "Please enter some note.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {

                                    tvNoteNoteCam.text = "Note: ${etPhotoNote.text}"
                                    tvNoteNoteCam.visibility = View.VISIBLE

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
                                    }, 10)
                                }
                            }
                        }
                    }
                )
            }

        }


        btnCancel.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        btnNoteCam.setOnClickListener {
            onBackPressed()
        }

    }

    override fun onBackPressed() {
        Misc.backActivity(this, Misc.isNoteCamOnBackIntEnabled, object : ActivityOnBackPress {
            override fun onBackPress() {
                finish()
            }
        })
    }

    @SuppressLint("MissingPermission")
    override fun onResume() {
        super.onResume()
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
                try {
                    i = manager.getLaunchIntentForPackage("com.google.android.apps.photos")
                    if (i == null) throw PackageManager.NameNotFoundException()
                    i.addCategory(Intent.CATEGORY_LAUNCHER)
                    i.setDataAndType(lastUri, "image/*")
                    startActivity(i)
                } catch (e: PackageManager.NameNotFoundException) {
                    e.printStackTrace()
                    val intent = Intent()
                    intent.action = Intent.ACTION_VIEW
                    intent.setDataAndType(lastUri, "image/*")
                    startActivity(intent)
                }


            }
        }

    }

    private fun startCamera(){
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

            cameraProvider?.bindToLifecycle(
                this,
                cameraFacing,
                preview,
                imageCapture
            )

        }
        cameraProviderFuture.addListener(
            cameraRunnable,
            ContextCompat.getMainExecutor(this)
        )

    }
}
