package com.liveearth.android.map

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.liveearth.android.map.clasess.Misc
import com.liveearth.android.map.interfaces.NativeAdCallBack
import com.liveearth.android.map.interfaces.OnBackPressCallBack
import com.liveearth.android.map.interfaces.StartActivityCallBack

import kotlinx.android.synthetic.fdroid.activity_world_quiz_screen_one.*

class WorldQuizScreenOneActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_world_quiz_screen_one)

//        Misc.showNativeAd(
//            this@WorldQuizScreenOneActivity,
//            nativeAd,
//            Misc.isQuizScreenOneNativeEnabled,
//            object : NativeAdCallBack {
//                override fun onLoad() {
//                    nativeAd.visibility = View.VISIBLE
//                }
//            }
//        )

        btnBackWorldQuizScreenOne.setOnClickListener {
            onBackPressed()
        }
        val intent = Intent(
            this@WorldQuizScreenOneActivity,
            WorldQuizSelectContinentActivity::class.java
        )

        clCountries.setOnClickListener {
//            Misc.startActivity(
//                this,
//                Misc.isQuizCountriesIntEnabled,
//                object : StartActivityCallBack {
//                    override fun onStart() {
                        Misc.gameMode = Misc.countries
                        startActivity(intent)
//                    }
//                })
        }

        clFlags.setOnClickListener {
//            Misc.startActivity(
//                this,
//                Misc.isQuizCountriesIntEnabled,
//                object : StartActivityCallBack {
//                    override fun onStart() {
                        Misc.gameMode = Misc.flags
                        startActivity(intent)
//                    }
//                })
        }

        clCapitals.setOnClickListener {
//            Misc.startActivity(
//                this,
//                Misc.isQuizCountriesIntEnabled,
//                object : StartActivityCallBack {
//                    override fun onStart() {
                        Misc.gameMode = Misc.capitals
                        startActivity(intent)
//                    }
//                })
        }

        clCurrencies.setOnClickListener {
//            Misc.startActivity(
//                this,
//                Misc.isQuizCurrenciesIntEnabled,
//                object : StartActivityCallBack {
//                    override fun onStart() {
                        Misc.gameMode = Misc.currencies
                        startActivity(intent)
//                    }
//                })
        }
    }

//    override fun onBackPressed() {
//        Misc.onBackPress(
//            this,
//            Misc.isQuizScreenOneBackIntEnabled,
//            object : OnBackPressCallBack {
//                override fun onBackPress() {
//                    finish()
//                }
//            })
//    }
}