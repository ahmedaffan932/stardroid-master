package com.liveearth.android.map

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.liveearth.android.map.clasess.Misc
import com.liveearth.android.map.interfaces.InterstitialCallBack
import com.liveearth.android.map.interfaces.NativeAdCallBack

import kotlinx.android.synthetic.fdroid.activity_world_quiz_completed.*

class WorldQuizCompletedActivity : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_world_quiz_completed)

        Misc.showNativeAd(
            this@WorldQuizCompletedActivity,
            nativeAd,
            Misc.isQuizCompleteNativeEnabled,
            object : NativeAdCallBack {
                override fun onLoad() {
                    nativeAd.visibility = View.VISIBLE
                }
            }
        )

        textCorrectAnswers.text = "Correct Answer: ${intent.getIntExtra(Misc.data, 0)}"
        textTotalLevels.text = "Total levels: ${intent.getIntExtra(Misc.levels, 0)}"
        textFalseAnswer.text = "False Answers: ${
            (intent.getIntExtra(Misc.levels, 0) - intent.getIntExtra(
                Misc.data,
                0
            ))
        }"

        btnBackWorldQuizCompleted.setOnClickListener {
            onBackPressed()
        }

        btnback.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        Misc.showInterstitial(this, Misc.isQuizCompleteBackIntEnabled, object : InterstitialCallBack {
            override fun onDismiss() {
                finish()
            }
        })
    }
}