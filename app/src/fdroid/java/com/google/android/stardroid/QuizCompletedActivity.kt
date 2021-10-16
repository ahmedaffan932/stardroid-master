package com.google.android.stardroid

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.stardroid.clasess.Misc
import kotlinx.android.synthetic.fdroid.activity_quiz_completed.*

class QuizCompletedActivity : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_completed)

        textCorrectAnswers.text = "Correct Answer: ${intent.getIntExtra(Misc.data, 0)}"
        textTotalLevels.text = "Total levels: ${intent.getIntExtra(Misc.levels, 0)}"
        textFalseAnswer.text = "False Answers: ${(intent.getIntExtra(Misc.levels, 0) - intent.getIntExtra(Misc.data, 0))}"

        btnBackWorldQuizCompleted.setOnClickListener {
            onBackPressed()
        }

        btnback.setOnClickListener {
            onBackPressed()
        }
    }
}