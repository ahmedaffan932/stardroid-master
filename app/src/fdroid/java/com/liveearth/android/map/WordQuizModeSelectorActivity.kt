package com.liveearth.android.map

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import com.blongho.country_data.World
import com.liveearth.android.map.clasess.Ads
import com.liveearth.android.map.clasess.Misc
import com.liveearth.android.map.interfaces.InterstitialCallBack
import com.liveearth.android.map.interfaces.NativeAdCallBack

import kotlinx.android.synthetic.fdroid.activity_word_quiz_mode_selector.*

class WordQuizModeSelectorActivity : AppCompatActivity() {
    var easyLevel = 10
    var mediumLevel = 25
    var hardLevel = 0

    @SuppressLint("LogNotTimber", "SetTextI18n")
   override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_word_quiz_mode_selector)

        Ads.showNativeAd(
            this@WordQuizModeSelectorActivity,
            nativeAd,
            Misc.quizSelectModeNativeAm_Al,
            object : NativeAdCallBack {
                override fun onLoad() {
                    nativeAd.visibility = View.VISIBLE
                }
            }
        )

        World.init(this)
        val arr = World.getAllCountries()
        if (Misc.gameContinent != Misc.wholeWorld) {
            clPro.visibility = View.GONE
            val a =
                arr.filter { con -> con.continent.contains(Misc.gameContinent, ignoreCase = true) }
            hardLevel = a.size
            textEasy.text = "0 / $easyLevel"
            textMedium.text = "0 / $mediumLevel"
            textHard.text = "0 / $hardLevel"

        } else {
            easyLevel = 30
            mediumLevel = 70
            hardLevel = 150
        }

        if (Misc.gameContinent == Misc.oceania) {
            clHard.visibility = View.GONE
        }

        clEasy.setOnClickListener {
            startGame(easyLevel)
        }
        clMedium.setOnClickListener {
            startGame(mediumLevel)
        }
        clHard.setOnClickListener {
            startGame(hardLevel)
        }
        clPro.setOnClickListener {
            startGame(236)
        }

        btnBackWorldQizModeSelector.setOnClickListener {
            onBackPressed()
        }

    }

    private fun startGame(levels: Int) {
        if (Misc.gameMode == Misc.flags) {
            val intent = Intent(this, WorldQuizFlagActivity::class.java)
            intent.putExtra(Misc.data, levels)
            Ads.showInterstitial(this, Misc.startGameIntAm_Al, object : InterstitialCallBack {
                override fun onDismiss() {
                    startActivity(intent)
                }
            })
        } else {
            val intent = Intent(this, WorldQuizCountriesActivity::class.java)
            intent.putExtra(Misc.data, levels)
            Ads.showInterstitial(this, Misc.startGameIntAm_Al, object : InterstitialCallBack {
                override fun onDismiss() {
                    startActivity(intent)
                }
            })
        }
    }

    override fun onBackPressed() {
        Ads.showInterstitial(this, Misc.continentSelectBackIntAm_Al, object : InterstitialCallBack {
            override fun onDismiss() {
                finish()
            }
        })
    }
}