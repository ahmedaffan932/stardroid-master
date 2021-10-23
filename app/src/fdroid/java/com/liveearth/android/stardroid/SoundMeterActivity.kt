package com.google.android.stardroid

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.SpannableStringBuilder
import android.text.style.ImageSpan
import android.util.Log
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.liveearth.android.stardroid.R
import kotlinx.android.synthetic.fdroid.activity_sound_meter.*

class SoundMeterActivity : AppCompatActivity() {
    private val mRecorder = MediaRecorder()
    private val micPermissionRequest = 1032
    private val handler: Handler = Handler()
    private val arr = ArrayList<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sound_meter)

        btnBackSoundMeter.setOnClickListener {
            onBackPressed()
        }

        btnInfo.setOnClickListener {


            Toast.makeText(
                this,
                "Example:\n20 dB-Whisper\n40 dB-Quite library\n60 dB-Conversation\n80 dB-Loud Music\n100 dB-Motorcycle\n120 dB-Threshold of pain",
                Toast.LENGTH_LONG
            ).show()

        }

        btnReset.setOnClickListener {
            handler.removeCallbacks(runSoundMeter)
            speedAnalog.speedTo(0.toFloat(), 100)
            textAvg.text = "0"
            textMax.text = "0"
            textMin.text = "0"

            Handler().postDelayed({
                handler.post(runSoundMeter)
            }, 1000)
        }

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), micPermissionRequest)
            } else {
                startRecording()
                handler.post(runSoundMeter)
            }
        }

    }


    fun getAmplitude(): Int {
        return mRecorder.maxAmplitude
    }

    private fun startRecording() {
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
        mRecorder.setOutputFile("/dev/null")
        mRecorder.prepare()
        mRecorder.start()

    }

    private val runSoundMeter: Runnable by lazy {
        return@lazy object : Runnable {
            @SuppressLint("LogNotTimber")
            override fun run() {
                val value = (20 * kotlin.math.log10(getAmplitude().toDouble())).toInt()
                Log.d("logKey", value.toString())
                if (value > 0) {
                    speedAnalog.speedTo(value.toFloat(), 100)
                    arr.add(value)
                    textAvg.text = arr.average().toInt().toString()
                    textMax.text = arr.maxOrNull().toString()
                    textMin.text = arr.minOrNull().toString()
                }

                handler.postDelayed(this, 10)
            }
        }
    }

    @SuppressLint("ShowToast")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == micPermissionRequest) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startRecording()
                handler.post(runSoundMeter)
            } else {

                val builder = SpannableStringBuilder()
                builder.append(" ").append(" ")
                builder.setSpan(
                    ImageSpan(this, R.drawable.ic_baseline_close_24),
                    builder.length - 1,
                    builder.length,
                    0
                )
                builder.append("")

                val sb = Snackbar.make(
                    clParent,
                    "Recording Permission is required for Sound Meter",
                    Snackbar.LENGTH_INDEFINITE
                )
                sb.setAction(builder) {
                    sb.dismiss()
                }
                sb.show()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        mRecorder.stop()
        handler.removeCallbacks(runSoundMeter)
    }

}