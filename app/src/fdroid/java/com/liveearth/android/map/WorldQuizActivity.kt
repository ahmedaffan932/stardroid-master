package com.liveearth.android.map

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.liveearth.android.map.clasess.Misc
import com.liveearth.android.map.interfaces.InterstitialCallBack
import com.liveearth.android.map.interfaces.NativeAdCallBack
import kotlinx.android.synthetic.fdroid.activity_world_quiz.*

class WorldQuizActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_world_quiz)

        Misc.showNativeAd(this, nativeAd, Misc.isWordlQuizActivityNativeEnabled, object: NativeAdCallBack{
            override fun onLoad() {
                nativeAd.visibility = View.VISIBLE
            }
        })

        btnCloseGame.setOnClickListener {
            onBackPressed()
        }

        btnViewWorld.setOnClickListener {
            val intent = Intent(this@WorldQuizActivity, AmChartsActivity::class.java)
            startActivity(intent)
        }

        btnPlayGame.setOnClickListener {
            val intent =
                Intent(this@WorldQuizActivity, WorldQuizScreenOneActivity::class.java)
            intent.putExtra(Misc.data, "sda")
            startActivity(intent)
        }

    }

    override fun onBackPressed() {
        Misc.showInterstitial(this, Misc.isWorldQuizOnBackIntEnabled, object : InterstitialCallBack {
            override fun onDismiss() {
                finish()
            }
        })
    }
}