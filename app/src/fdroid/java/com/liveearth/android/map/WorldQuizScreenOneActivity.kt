package com.liveearth.android.map

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.liveearth.android.map.clasess.Misc
import com.liveearth.android.map.interfaces.InterstitialCallBack
import com.liveearth.android.map.interfaces.NativeAdCallBack

import kotlinx.android.synthetic.fdroid.activity_world_quiz_screen_one.*

class WorldQuizScreenOneActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_world_quiz_screen_one)

        Misc.showNativeAd(
            this@WorldQuizScreenOneActivity,
            nativeAd,
            Misc.isQuizScreenOneNativeEnabled,
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
            Misc.showInterstitial(
                this,
                Misc.isQuizCountriesIntEnabled,
                object : InterstitialCallBack {
                    override fun onDismiss() {
                        Misc.gameMode = Misc.countries
                        startActivity(intent)
                    }
                })
        }

        clFlags.setOnClickListener {
            Misc.showInterstitial(
                this,
                Misc.isQuizCountriesIntEnabled,
                object : InterstitialCallBack {
                    override fun onDismiss() {
                        Misc.gameMode = Misc.flags
                        startActivity(intent)
                    }
                })
        }

        clCapitals.setOnClickListener {
            Misc.showInterstitial(
                this,
                Misc.isQuizCountriesIntEnabled,
                object : InterstitialCallBack {
                    override fun onDismiss() {
                        Misc.gameMode = Misc.capitals
                        startActivity(intent)
                    }
                })
        }

        clCurrencies.setOnClickListener {
            Misc.showInterstitial(
                this,
                Misc.isQuizCountriesIntEnabled,
                object : InterstitialCallBack {
                    override fun onDismiss() {
                        Misc.gameMode = Misc.currencies
                        startActivity(intent)
                    }
                })
        }
    }

    override fun onBackPressed() {
        Misc.showInterstitial(
            this,
            Misc.isQuizScreenOneBackIntEnabled,
            object : InterstitialCallBack {
                override fun onDismiss() {
                    finish()
                }
            })
    }
}