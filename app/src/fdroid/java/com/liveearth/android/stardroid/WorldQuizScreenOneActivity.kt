package com.liveearth.android.stardroid

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.liveearth.android.stardroid.clasess.Misc
import com.liveearth.android.stardroid.interfaces.StartActivityCallBack

import kotlinx.android.synthetic.fdroid.activity_world_quiz_screen_one.*

class WorldQuizScreenOneActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_world_quiz_screen_one)

        btnBackWorldQuizScreenOne.setOnClickListener {
            onBackPressed()
        }
        val intent = Intent(this@WorldQuizScreenOneActivity, WorldQuizSelectContinentActivity::class.java)

        clCountries.setOnClickListener {
            Misc.startActivity(this, Misc.isQuizCountriesIntEnabled, object : StartActivityCallBack {
                override fun onStart() {
                    Misc.gameMode = Misc.countries
                    startActivity(intent)
                }
            })
        }

        clFlags.setOnClickListener {
            Misc.startActivity(this, Misc.isQuizCountriesIntEnabled, object : StartActivityCallBack {
                override fun onStart() {
                    Misc.gameMode = Misc.flags
                    startActivity(intent)
                }
            })
        }

        clCapitals.setOnClickListener {
            Misc.startActivity(this, Misc.isQuizCountriesIntEnabled, object : StartActivityCallBack {
                override fun onStart() {
                    Misc.gameMode = Misc.capitals
                    startActivity(intent)
                }
            })
        }

        clCurrencies.setOnClickListener {
            Misc.startActivity(
                this,
                Misc.isQuizCurrenciesIntEnabled,
                object : StartActivityCallBack {
                    override fun onStart() {
                        Misc.gameMode = Misc.currencies
                        startActivity(intent)
                    }
                })
        }

    }
}