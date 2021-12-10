package com.liveearth.android.map

import android.annotation.SuppressLint
import android.content.Intent
import android.os.*
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.webkit.*
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.blongho.country_data.Country
import com.blongho.country_data.World
import com.google.firebase.FirebaseApp
import com.liveearth.android.map.clasess.Misc
import com.liveearth.android.map.interfaces.StartActivityCallBack
import kotlinx.android.synthetic.fdroid.activity_world_quiz_flag.*

class WorldQuizFlagActivity : AppCompatActivity() {
    var currentLevel = 0
    var levels = 0
    var isCountrySelected = false
    private val arrCountries = ArrayList<Country>()
    var isCompleted = false
    var numberOfCorrectAnswers = 0

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("SetJavaScriptEnabled", "LogNotTimber", "SetTextI18n", "ObsoleteSdkInt")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_world_quiz_flag)

        FirebaseApp.initializeApp(this)

        initGame()
        getCurrentLevel()

    }


    @SuppressLint("SetTextI18n")
    fun getCurrentLevel() {
        enableAnswersClickListeners(true)
        btnNext.visibility = View.INVISIBLE
        try {
            textLevel.text = "Level: ${currentLevel + 1}/${levels}"
            flagCountryGame.visibility = View.VISIBLE
            flagCountryGame.setImageResource(arrCountries[currentLevel].flagResource)
            findCountry.text = "This is Flag of ..."
            val arr = ArrayList<Country>()
            for (country in arrCountries) {
                arr.add(country)
            }
            arr.remove(arrCountries[currentLevel])
            arr.shuffle()
            flagAnswerOne.text = arr[0].name
            flagAnswerTwo.text = arr[1].name
            flagAnswerThree.text = arr[2].name
            flagAnswerFour.text = arr[3].name

            when ((1..4).random()) {
                1 -> {
                    flagAnswerOne.text = arrCountries[currentLevel].name
                }
                2 -> {
                    flagAnswerTwo.text = arrCountries[currentLevel].name
                }
                3 -> {
                    flagAnswerThree.text = arrCountries[currentLevel].name
                }
                else -> {
                    flagAnswerFour.text = arrCountries[currentLevel].name
                }
            }

        } catch (e: Exception) {
            Misc.startActivity(
                this,
                Misc.isQuizCompleteIntEnabled,
                object : StartActivityCallBack {
                    override fun onStart() {
                        finish()
                        val intent =
                            Intent(
                                this@WorldQuizFlagActivity,
                                WorldQuizCompletedActivity::class.java
                            )
                        intent.putExtra(Misc.levels, levels)
                        intent.putExtra(Misc.data, numberOfCorrectAnswers)
                        startActivity(intent)
                    }
                })
        }
    }

    @SuppressLint("SetTextI18n", "ServiceCast")
    fun getResult(selectedCountry: String) {
        enableAnswersClickListeners(false)
        if (selectedCountry == arrCountries[currentLevel].name) {
            numberOfCorrectAnswers++
            animResult.visibility = View.VISIBLE
            animResult.setAnimation(R.raw.ok_anim)
            animResult.playAnimation()
            Handler().postDelayed({
                animResult.visibility = View.GONE
            }, 1500)
        } else {
            animResult.visibility = View.VISIBLE
            val v: Vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
            // Vibrate for 500 milliseconds
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                //deprecated in API 26
                v.vibrate(500)
            }
            animResult.setAnimation(R.raw.not_ok_anim)
            animResult.playAnimation()
            Handler().postDelayed({
                animResult.visibility = View.GONE
            }, 1500)

        }
        findCountry.text =
            "This is flag of ${arrCountries[currentLevel].name}"

        btnNext.visibility = View.VISIBLE
        btnNext.setOnClickListener {
            currentLevel++
            getCurrentLevel()
        }

    }

    fun hideCountryInfo() {
        val a: Animation =
            AnimationUtils.loadAnimation(
                this,
                R.anim.slide_from_right_to_left
            )
        a.duration = 300
        clCountryInfo.startAnimation(a)
        Handler().postDelayed({
            clCountryInfo.visibility = View.INVISIBLE
        }, 300)
    }

    @SuppressLint("SetTextI18n", "LogNotTimber")
    fun initGame() {
        try {
            arrCountries.clear()
            levels = intent.getIntExtra(Misc.data, 0)

            World.init(this)
            var arr = if (Misc.gameContinent != Misc.wholeWorld) {
                World.getAllCountries()
                    .filter { con -> con.continent.contains(Misc.gameContinent, ignoreCase = true) }
            } else {
                World.getAllCountries()
            }
            Log.d(Misc.logKey, arr.size.toString())
            if (levels < 120)
                arr = arr.sortedByDescending { it.area }
            for (a in arr) {
                Log.d(Misc.logKey, a.area.toString())
            }
            var i = 0
            while (arrCountries.size < levels) {
                val tempCountry = arr[i]
                if (tempCountry.alpha2 != "xx") {
                    arrCountries.add(tempCountry)
                    i++
                } else {
                    i++
                }
            }
            Log.d(Misc.logKey, arrCountries.toString())
            arrCountries.shuffle()

            textLevel.text = "Level: ${currentLevel + 1}/${levels}"
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("LogNotTimber")
    fun checkSelection(selectedCountry: String) {
        if (selectedCountry == arrCountries[currentLevel].name) {
            Log.d(Misc.logKey, "Correct")
            currentLevel++
            getCurrentLevel()
        } else {
            Log.e(Misc.logKey, "False")
            currentLevel++
            getCurrentLevel()
        }
//        getResult()

    }

    private fun enableAnswersClickListeners(isEnable: Boolean){
        if(isEnable) {
            flagAnswerOne.setOnClickListener {
                getResult((it as TextView).text.toString())
            }

            flagAnswerTwo.setOnClickListener {
                getResult((it as TextView).text.toString())
            }

            flagAnswerThree.setOnClickListener {
                getResult((it as TextView).text.toString())
            }

            flagAnswerFour.setOnClickListener {
                getResult((it as TextView).text.toString())
            }
        }else{
            flagAnswerOne.setOnClickListener {
            }

            flagAnswerTwo.setOnClickListener {
            }

            flagAnswerThree.setOnClickListener {
            }

            flagAnswerFour.setOnClickListener {
            }
        }
    }

}