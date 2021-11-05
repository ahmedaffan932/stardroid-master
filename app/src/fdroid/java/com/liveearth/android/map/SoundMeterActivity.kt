package com.liveearth.android.map

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
import com.liveearth.android.map.clasess.Misc
import com.liveearth.android.map.interfaces.OnBackPressCallBack
import kotlinx.android.synthetic.fdroid.activity_sound_meter.*

class SoundMeterActivity : AppCompatActivity() {
    private val mRecorder = MediaRecorder()
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
            arr.clear()

            Handler().postDelayed({
                handler.post(runSoundMeter)
            }, 1000)
        }



    }


    fun getAmplitude(): Int {
        return mRecorder.maxAmplitude
    }

    private fun startRecording() {
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
        mRecorder.setOutputFile("${externalCacheDir?.absolutePath}/${System.currentTimeMillis()}.3gp")
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

    override fun onResume() {
        super.onResume()
        startRecording()
        handler.post(runSoundMeter)

    }

    override fun onPause() {
        super.onPause()
        mRecorder.stop()
        handler.removeCallbacks(runSoundMeter)
    }

    override fun onBackPressed() {
        Misc.onBackPress(this, Misc.isSoundMeterBackIntEnabled, object : OnBackPressCallBack {
            override fun onBackPress() {
                finish()
            }
        })
    }
}