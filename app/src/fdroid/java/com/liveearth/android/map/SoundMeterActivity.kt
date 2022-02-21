package com.liveearth.android.map

import android.annotation.SuppressLint
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.liveearth.android.map.clasess.Misc
import com.liveearth.android.map.interfaces.InterstitialCallBack
import com.liveearth.android.map.interfaces.NativeAdCallBack
import kotlinx.android.synthetic.fdroid.activity_sound_meter.*

class SoundMeterActivity : AppCompatActivity() {
    private val mRecorder = MediaRecorder()
    private val handler: Handler = Handler()
    private val arr = ArrayList<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sound_meter)


        Misc.showNativeAd(
            this@SoundMeterActivity,
            nativeAd,
            Misc.isSoundMeterNativeEnabled,
            object : NativeAdCallBack {
                override fun onLoad() {
                    nativeAd.visibility = View.VISIBLE
                }
            }
        )

        btnBackSoundMeter.setOnClickListener {
            onBackPressed()
        }

        btnInfo.setOnClickListener {
//            Misc.showInterstitial(this, Misc.isBtnClickIntEnable, object : InterstitialCallBack {
//                override fun onDismiss() {
                    AlertDialog.Builder(this@SoundMeterActivity)
                        .setTitle("Example:")
                        .setMessage(
                            "20 dB-Whisper\n" +
                                    "40 dB-Quite library\n" +
                                    "60 dB-Conversation\n" +
                                    "80 dB-Loud Music\n" +
                                    "100 dB-Motorcycle\n" +
                                    "120 dB-Threshold of pain"
                        )
                        .setPositiveButton("Ok")
                        { dialog, which ->
                            dialog.dismiss()
                        }
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .show()
//                }
//            })
        }

        btnReset.setOnClickListener{
//            Misc.showInterstitial(this, Misc.isBtnClickIntEnable, object : InterstitialCallBack {
//                override fun onDismiss() {
                    handler.removeCallbacks(runSoundMeter)
                    speedAnalog.speedTo(0.toFloat(), 100)
                    textAvg.text = "0"
                    textMax.text = "0"
                    textMin.text = "0"
                    arr.clear()

                    Handler().postDelayed({
                        handler.post(runSoundMeter)
                    }, 1000)
//                }
//            })
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
        Misc.showInterstitial(this, Misc.isSoundMeterBackIntEnabled, object : InterstitialCallBack {
            override fun onDismiss() {
                finish()
            }
        })
    }
}