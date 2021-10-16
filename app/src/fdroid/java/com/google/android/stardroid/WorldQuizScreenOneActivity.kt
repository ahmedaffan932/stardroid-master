package com.google.android.stardroid

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.stardroid.clasess.Misc
import com.google.android.stardroid.interfaces.StartActivityCallBack
import kotlinx.android.synthetic.fdroid.activity_world_quiz_screen_one.*

class WorldQuizScreenOneActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_world_quiz_screen_one)

        clCountries.setOnClickListener {
            Misc.startActivity(this, Misc.isQuizCountriesIntEnabled, object : StartActivityCallBack {
                override fun onStart() {
                    val intent = Intent(this@WorldQuizScreenOneActivity, WorldQuizSelectContinetActivity::class.java)
                    startActivity(intent)
                }
            })
        }

    }
}