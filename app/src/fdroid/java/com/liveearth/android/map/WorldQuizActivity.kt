package com.liveearth.android.map

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.liveearth.android.map.clasess.Misc
import com.liveearth.android.map.interfaces.NativeAdCallBack
import com.liveearth.android.map.interfaces.OnBackPressCallBack
import com.liveearth.android.map.interfaces.StartActivityCallBack
import kotlinx.android.synthetic.fdroid.activity_world_quiz.*

class WorldQuizActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_world_quiz)

        btnCloseGame.setOnClickListener {
            onBackPressed()
        }

        btnViewWorld.setOnClickListener{
            Misc.startActivity(this, Misc.isViewWorldIntEnabled, object : StartActivityCallBack {
                override fun onStart() {
                    val intent = Intent(this@WorldQuizActivity, AmChartsActivity::class.java)
                    startActivity(intent)
                }
            })
        }

        btnPlayGame.setOnClickListener{
            Misc.startActivity(this, Misc.isQuizScreenOneIntEnabled, object : StartActivityCallBack {
                override fun onStart() {
                    val intent = Intent(this@WorldQuizActivity, WorldQuizScreenOneActivity::class.java)
                    intent.putExtra(Misc.data, "sda")
                    startActivity(intent)
                }
            })
        }
    }

    override fun onBackPressed() {
        Misc.onBackPress(this, Misc.isGameBackIntEnabled, object : OnBackPressCallBack {
            override fun onBackPress() {
                finish()
            }
        })
    }

    override fun onResume() {
        super.onResume()

        Misc.loadNativeAd(
            this,
            Misc.nativeAdId,
            object : NativeAdCallBack {
                override fun onLoad() {
                    Misc.showNativeAd(
                        this@WorldQuizActivity,
                        nativeAd,
                        Misc.isQuizActivitySplashEnabled,
                        object : NativeAdCallBack {
                            override fun onLoad() {
                                nativeAd.visibility = View.VISIBLE
                            }
                        }
                    )
//                        showStartButton()
                }
            }
        )
    }
}