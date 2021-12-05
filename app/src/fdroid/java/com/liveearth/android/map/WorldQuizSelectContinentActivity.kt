package com.liveearth.android.map

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.liveearth.android.map.clasess.Misc
import com.liveearth.android.map.interfaces.NativeAdCallBack
import com.liveearth.android.map.interfaces.OnBackPressCallBack
import com.liveearth.android.map.interfaces.StartActivityCallBack

import kotlinx.android.synthetic.fdroid.activity_world_quiz_select_continet.*

class WorldQuizSelectContinentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_world_quiz_select_continet)

        Misc.showNativeAd(
            this@WorldQuizSelectContinentActivity,
            nativeAd,
            Misc.isContinentSelectNativeEnabled,
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
            Misc.startActivity(
                this,
                Misc.isContinentSelectIntEnabled,
                object : StartActivityCallBack {
                    override fun onStart() {
                        Misc.gameContinent = Misc.wholeWorld
                        startActivity(intent)
                    }
                })
        }

        clAsia.setOnClickListener {
            Misc.startActivity(
                this,
                Misc.isContinentSelectIntEnabled,
                object : StartActivityCallBack {
                    override fun onStart() {
                        Misc.gameContinent = Misc.asia
                        startActivity(intent)
                    }
                })
        }

        clEurope.setOnClickListener {
            Misc.startActivity(
                this,
                Misc.isContinentSelectIntEnabled,
                object : StartActivityCallBack {
                    override fun onStart() {
                        Misc.gameContinent = Misc.europe
                        startActivity(intent)
                    }
                })
        }

        clAfrica.setOnClickListener {
            Misc.startActivity(
                this,
                Misc.isContinentSelectIntEnabled,
                object : StartActivityCallBack {
                    override fun onStart() {
                        Misc.gameContinent = Misc.africa
                        startActivity(intent)
                    }
                })
        }

        clAmerica.setOnClickListener {
            Misc.startActivity(
                this,
                Misc.isContinentSelectIntEnabled,
                object : StartActivityCallBack {
                    override fun onStart() {
                        Misc.gameContinent = Misc.america
                        startActivity(intent)
                    }
                })
        }
        clOceania.setOnClickListener {
            Misc.startActivity(
                this,
                Misc.isContinentSelectIntEnabled,
                object : StartActivityCallBack {
                    override fun onStart() {
                        Misc.gameContinent = Misc.oceania
                        startActivity(intent)
                    }
                })
        }
    }

    override fun onBackPressed() {
        Misc.onBackPress(this, Misc.isQuizSelectModeIntEnabled, object : OnBackPressCallBack {
            override fun onBackPress() {
                finish()
            }
        })
    }
}