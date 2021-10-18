package com.google.android.stardroid

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.stardroid.clasess.Misc
import com.google.android.stardroid.interfaces.StartActivityCallBack
import kotlinx.android.synthetic.fdroid.activity_word_quiz_mode_selector.*

class WordQuizModeSelectorActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_word_quiz_mode_selector)

        clEasy.setOnClickListener {
            startGame(3)
        }
        clMedium.setOnClickListener {
            startGame(70)
        }
        clHard.setOnClickListener {
            startGame(150)
        }
        clPro.setOnClickListener {
            startGame(236)
        }

        btnBackWorldQizModeSelector.setOnClickListener {
            onBackPressed()
        }

    }

    private fun startGame(levels: Int){
        val intent = Intent(this, QuizCountriesActivity::class.java)
        intent.putExtra(Misc.data, levels)
        Misc.startActivity(this, Misc.isStartGameIntEnabled, object : StartActivityCallBack {
            override fun onStart() {
                startActivity(intent)
            }
        })
    }
}