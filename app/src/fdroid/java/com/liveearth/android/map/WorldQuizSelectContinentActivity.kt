package com.liveearth.android.map

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import com.liveearth.android.map.clasess.Ads
import com.liveearth.android.map.clasess.Misc
import com.liveearth.android.map.interfaces.InterstitialCallBack
import com.liveearth.android.map.interfaces.NativeAdCallBack

import kotlinx.android.synthetic.fdroid.activity_world_quiz_select_continet.*

class WorldQuizSelectContinentActivity : AppCompatActivity() {
   override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_world_quiz_select_continet)

        Ads.showNativeAd(
            this@WorldQuizSelectContinentActivity,
            nativeAd,
            Misc.continentSelectNativeAm_Al,
            object : NativeAdCallBack {
                override fun onLoad() {
                    nativeAd.visibility = View.VISIBLE
                }
            }
        )

        btnBackWorldQuizContinent.setOnClickListener {
            onBackPressed()
        }

        val intent =
            Intent(this@WorldQuizSelectContinentActivity, WordQuizModeSelectorActivity::class.java)
        clWholeWorld.setOnClickListener {
            Ads.showInterstitial(
                this,
                Misc.continentSelectIntAm_Al,
                object : InterstitialCallBack {
                    override fun onDismiss() {
                        Misc.gameContinent = Misc.wholeWorld
                        startActivity(intent)
                    }
                })
        }

        clAsia.setOnClickListener {
            Ads.showInterstitial(
                this,
                Misc.continentSelectIntAm_Al,
                object : InterstitialCallBack {
                    override fun onDismiss() {
                        Misc.gameContinent = Misc.asia
                        startActivity(intent)
                    }
                })
        }

        clEurope.setOnClickListener {
            Ads.showInterstitial(
                this,
                Misc.continentSelectIntAm_Al,
                object : InterstitialCallBack {
                    override fun onDismiss() {
                        Misc.gameContinent = Misc.europe
                        startActivity(intent)
                    }
                })
        }

        clAfrica.setOnClickListener {
            Ads.showInterstitial(
                this,
                Misc.continentSelectIntAm_Al,
                object : InterstitialCallBack {
                    override fun onDismiss() {
                        Misc.gameContinent = Misc.africa
                        startActivity(intent)
                    }
                })
        }

        clAmerica.setOnClickListener {
            Ads.showInterstitial(
                this,
                Misc.continentSelectIntAm_Al,
                object : InterstitialCallBack {
                    override fun onDismiss() {
                        Misc.gameContinent = Misc.america
                        startActivity(intent)
                    }
                })
        }
        clOceania.setOnClickListener {
            Ads.showInterstitial(
                this,
                Misc.continentSelectIntAm_Al,
                object : InterstitialCallBack {
                    override fun onDismiss() {
                        Misc.gameContinent = Misc.oceania
                        startActivity(intent)
                    }
                })
        }
    }

    override fun onBackPressed() {
        Ads.showInterstitial(this, Misc.quizSelectModeIntAm_Al, object : InterstitialCallBack {
            override fun onDismiss() {
                finish()
            }
        })
    }
}