package com.liveearth.android.map

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.liveearth.android.map.clasess.Misc
import com.liveearth.android.map.interfaces.OnImageSaveCallBack
import com.google.zxing.WriterException
import com.liveearth.android.map.clasess.Ads
import com.liveearth.android.map.interfaces.InterstitialCallBack
import com.liveearth.android.map.interfaces.NativeAdCallBack
import kotlinx.android.synthetic.main.activity_qrgenrated.*
import kotlinx.android.synthetic.main.activity_qrgenrated.nativeAd

class QRGeneratedActivity : AppCompatActivity() {
    private val storageReadPermissionRequest = 101
    private lateinit var qrBitmap: Bitmap

   override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_qrgenrated)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getStoragePermission()
        }


        Ads.showNativeAd(
            this,
            nativeAd,
            Misc.createQRNativeAm_Al,
            object : NativeAdCallBack {
                override fun onLoad() {
                    nativeAd.visibility = View.VISIBLE
                }
            }
        )

        val qrgEncoder =
            QRGEncoder(intent.getStringExtra(Misc.data), null, QRGContents.Type.TEXT, 600)
        qrgEncoder.colorBlack = Color.WHITE
        qrgEncoder.colorWhite = R.color.blue

        try {
            if (qrgEncoder.bitmap == null){
                Toast.makeText(this@QRGeneratedActivity, "Sorry some error occurred in QR creation.", Toast.LENGTH_SHORT).show()
                finish()
                return
            }
            qrBitmap = qrgEncoder.bitmap
            qrImage.setImageBitmap(qrBitmap)
        } catch (e: WriterException) {
            e.printStackTrace()
            Toast.makeText(this@QRGeneratedActivity, "Sorry some error occurred in QR creation.", Toast.LENGTH_SHORT).show()
            onBackPressed()
        }

        btnSaveQRCode.setOnClickListener {
            Misc.saveImageToExternal(this, qrBitmap, object : OnImageSaveCallBack {
                override fun onImageSaved() {
                    Toast.makeText(this@QRGeneratedActivity, "Your QR is saved in gallery.", Toast.LENGTH_SHORT).show()
                }
            })
        }

        btnBackQR.setOnClickListener {
            onBackPressed()
        }

        btnShareQRCode.setOnClickListener {
            shareQRCode()
        }


    }


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == storageReadPermissionRequest && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            finish()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun getStoragePermission() {
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                storageReadPermissionRequest
            )
        }
    }

    private fun shareQRCode() {
        val bitmap = Bitmap.createBitmap(qrImage.width, qrImage.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        qrImage.draw(canvas)
        val bitmapPath =
            MediaStore.Images.Media.insertImage(contentResolver, bitmap, "title", null)
        val bitmapUri: Uri = Uri.parse(bitmapPath)

        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "image/jpeg"
        intent.putExtra(Intent.EXTRA_STREAM, bitmapUri)
        startActivity(Intent.createChooser(intent, "Share QR"))
    }

    override fun onBackPressed() {
        Ads.showInterstitial(this, Misc.generateQrOnBackIntAm_Al, object : InterstitialCallBack {
            override fun onDismiss() {
                finish()
            }
        })
    }

//    override fun onResume() {
//        super.onResume()
//        Ads.showNativeAd(
//            this,
//            nativeAdViewQRGenerated,
//            Misc.isGenerateQrOnBackNativeEnabled,
//            object : NativeAdCallBack {
//                override fun onLoad() {
//                    nativeAdViewQRGenerated.visibility = View.VISIBLE
//                }
//            }
//        )
//    }
}