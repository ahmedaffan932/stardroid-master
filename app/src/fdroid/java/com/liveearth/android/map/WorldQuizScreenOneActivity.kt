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

import kotlinx.android.synthetic.fdroid.activity_world_quiz_screen_one.*

class WorldQuizScreenOneActivity : AppCompatActivity() {
   override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_world_quiz_screen_one)

        Ads.showNativeAd(
            this@WorldQuizScreenOneActivity,
            nativeAd,
            Misc.quizScreenOneNativeAm_Al,
            object : NativeAdCallBack {
                override fun onLoad() {
                    nativeAd.visibility = View.VISIBLE
                }
            }
        )

        btnBackWorldQuizScreenOne.setOnClickListener {
            onBackPressed()
        }
        val intent = Intent(
            this@WorldQuizScreenOneActivity,
            WorldQuizSelectContinentActivity::class.java
        )

        clCountries.setOnClickListener {
            Ads.showInterstitial(
                this,
                Misc.quizCountriesIntAm_Al,
                object : InterstitialCallBack {
                    override fun onDismiss() {
                        Misc.gameMode = Misc.countries
                        startActivity(intent)
                    }
                })
        }

        clFlags.setOnClickListener {
            Ads.showInterstitial(
                this,
                Misc.quizCountriesIntAm_Al,
                object : InterstitialCallBack {
                    override fun onDismiss() {
                        Misc.gameMode = Misc.flags
                        startActivity(intent)
                    }
                })
        }

        clCapitals.setOnClickListener {
            Ads.showInterstitial(
                this,
                Misc.quizCountriesIntAm_Al,
                object : InterstitialCallBack {
                    override fun onDismiss() {
                        Misc.gameMode = Misc.capitals
                        startActivity(intent)
                    }
                })
        }

        clCurrencies.setOnClickListener {
            Ads.showInterstitial(
                this,
                Misc.quizCountriesIntAm_Al,
                object : InterstitialCallBack {
                    override fun onDismiss() {
                        Misc.gameMode = Misc.currencies
                        startActivity(intent)
                    }
                })
        }
    }

    override fun onBackPressed() {
        Ads.showInterstitial(
            this,
            Misc.quizScreenOneBackIntAm_Al,
            object : InterstitialCallBack {
                override fun onDismiss() {
                    finish()
                }
            })
    }
}