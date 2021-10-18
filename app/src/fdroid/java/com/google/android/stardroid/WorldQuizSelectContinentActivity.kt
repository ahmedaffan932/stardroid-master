package com.google.android.stardroid

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.stardroid.clasess.Misc
import com.google.android.stardroid.interfaces.StartActivityCallBack
import kotlinx.android.synthetic.fdroid.activity_world_quiz_select_continet.*

class WorldQuizSelectContinentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_world_quiz_select_continet)

        btnBackWorldQuizContinent.setOnClickListener {
            onBackPressed()
        }

        val intent = Intent(this@WorldQuizSelectContinentActivity, WordQuizModeSelectorActivity::class.java)
        clWholeWorld.setOnClickListener {
            Misc.startActivity(this, Misc.isViewWorldIntEnabled, object : StartActivityCallBack {
                override fun onStart() {
                    Misc.gameContinents = Misc.wholeWorld
                    startActivity(intent)
                }
            })
        }

        clAsia.setOnClickListener {
            Misc.startActivity(this, Misc.isViewWorldIntEnabled, object : StartActivityCallBack {
                override fun onStart() {
                    Misc.gameContinents = Misc.asia
                    startActivity(intent)
                }
            })
        }

        clEurope.setOnClickListener {
            Misc.startActivity(this, Misc.isViewWorldIntEnabled, object : StartActivityCallBack {
                override fun onStart() {
                    Misc.gameContinents = Misc.europe
                    startActivity(intent)
                }
            })
        }

        clAfrica.setOnClickListener {
            Misc.startActivity(this, Misc.isViewWorldIntEnabled, object : StartActivityCallBack {
                override fun onStart() {
                    Misc.gameContinents = Misc.africa
                    startActivity(intent)
                }
            })
        }

        clAmerica.setOnClickListener {
            Misc.startActivity(this, Misc.isViewWorldIntEnabled, object : StartActivityCallBack {
                override fun onStart() {
                    Misc.gameContinents = Misc.america
                    startActivity(intent)
                }
            })
        }
        clOceania.setOnClickListener {
            Misc.startActivity(this, Misc.isViewWorldIntEnabled, object : StartActivityCallBack {
                override fun onStart() {
                    Misc.gameContinents = Misc.oceania
                    startActivity(intent)
                }
            })
        }
    }
}