package com.google.android.stardroid

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.OvershootInterpolator
import android.webkit.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.blongho.country_data.Country
import com.blongho.country_data.World
import com.google.android.stardroid.clasess.Misc
import com.google.android.stardroid.interfaces.CountryListInterface
import com.google.android.stardroid.interfaces.StartActivityCallBack
import com.google.android.stardroid.interfaces.WebAppInterface
import kotlinx.android.synthetic.fdroid.activity_quiz_countries.*
import kotlinx.android.synthetic.fdroid.activity_quiz_countries.clCountryInfo
import kotlinx.android.synthetic.fdroid.activity_quiz_countries.countryAreaInfo
import kotlinx.android.synthetic.fdroid.activity_quiz_countries.countryCapitalInfo
import kotlinx.android.synthetic.fdroid.activity_quiz_countries.countryCurrencyInfo
import kotlinx.android.synthetic.fdroid.activity_quiz_countries.countryNameInfo
import kotlinx.android.synthetic.fdroid.activity_quiz_countries.countryPopulationInfo
import kotlinx.android.synthetic.fdroid.activity_quiz_countries.flagCountryInfo
import kotlinx.android.synthetic.fdroid.activity_quiz_countries.webView

class QuizCountriesActivity : AppCompatActivity() {
    var currentLevel = 0
    var levels = 0
    var selectedCountry: Country? = null
    var isCountrySelected = false
    private val arrCountries = ArrayList<Country>()
    var isCompleted = false
    var numberOfCorrectAnswers = 0

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("SetJavaScriptEnabled", "LogNotTimber", "SetTextI18n", "ObsoleteSdkInt")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_countries)

        webView.settings.javaScriptEnabled = true
        webView.settings.allowFileAccess = true
        webView.settings.setRenderPriority(WebSettings.RenderPriority.HIGH)
        webView.setBackgroundColor(getColor(R.color.background_color))

        btnConfirm.setOnClickListener {
            if(!isCompleted){
                if(selectedCountry == arrCountries[currentLevel]){
                    textResult.text = "Correct !"
                    textResult.visibility = View.VISIBLE
                    numberOfCorrectAnswers++
                }else{
                    textResult.text = "Wrong !"
                    textResult.visibility = View.VISIBLE
                    textCorrectCountry.text = "You selected ${selectedCountry?.name}"
                    textCorrectCountry.visibility = View.VISIBLE
                }

                setCountryData(arrCountries[currentLevel])
                btnConfirmText.text = "Next"
                webView.loadUrl("javascript:selectCountry('${arrCountries[currentLevel].alpha2}');")
                blockView.visibility = View.VISIBLE
                isCompleted = true
            }else{
                currentLevel++
                getCurrentLevel()
                btnConfirmText.text = "Confirm"
                btnConfirm.visibility = View.INVISIBLE
                textResult.visibility = View.INVISIBLE
                textCorrectCountry.visibility = View.INVISIBLE
                hideCountryInfo()
                blockView.visibility = View.GONE
                isCompleted = false
            }
        }


        initGame()
        getCurrentLevel()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        } else {
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        }
        webView.settings.cacheMode = WebSettings.LOAD_NO_CACHE;

        webView.addJavascriptInterface(WebAppInterface(this, object : CountryListInterface {
            @SuppressLint("SetTextI18n")
            override fun onCountryClick(country: Country) {
                runOnUiThread{
                    selectCountry(country)
                }
//                setCountryData(country)
            }
        }), "Android")

        webView.loadUrl("file:///android_asset/world/Map.html")

        webView.webViewClient = object : WebViewClient() {
            override fun onReceivedError(
                view: WebView,
                request: WebResourceRequest,
                error: WebResourceError
            ) {
                super.onReceivedError(view, request, error)
                Log.d("TAG", error.description.toString())
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setCountryData(country: Country) {
        runOnUiThread {
            flagCountryInfo.setImageResource(World.getFlagOf(country.alpha2))
            countryNameInfo.text = country.name
            countryCapitalInfo.text = "Capital: ${country.capital}"
            countryPopulationInfo.text = "Population: ${country.population}"
            countryAreaInfo.text = "Area: ${country.area} km²"
            countryCurrencyInfo.text =
                "Currency: ${country.currency.name} (${country.currency.symbol})"
            clCountryInfo.visibility = View.VISIBLE
            val a: Animation =
                AnimationUtils.loadAnimation(
                    this,
                    R.anim.slide_from_left_to_right
                )
            a.interpolator = OvershootInterpolator()
            a.duration = 300
            clCountryInfo.startAnimation(a)
        }

    }

    fun selectCountry(country: Country){
        selectedCountry = country
        isCountrySelected = true
        btnConfirm.visibility = View.VISIBLE
    }

    @SuppressLint("SetTextI18n")
    fun getCurrentLevel(){
        try {
            findCountry.text = "Find: ${arrCountries[currentLevel].name}"
            textLevel.text = "Level: ${currentLevel + 1}/${levels}"
        }catch (e: Exception){
            Misc.startActivity(this, Misc.isQuizCompeletedIntEnabled, object : StartActivityCallBack{
                override fun onStart() {
                    finish()
                    val intent = Intent(this@QuizCountriesActivity, QuizCompletedActivity::class.java)
                    intent.putExtra(Misc.levels, levels)
                    intent.putExtra(Misc.data, numberOfCorrectAnswers)
                    startActivity(intent)
                }
            })
        }
    }

    fun hideCountryInfo(){
        val a: Animation =
            AnimationUtils.loadAnimation(
                this,
                R.anim.slide_from_right_to_left
            )
        a.duration = 300
        clCountryInfo.startAnimation(a)
        Handler().postDelayed({
            clCountryInfo.visibility = View.INVISIBLE
        },300)
    }

    @SuppressLint("SetTextI18n")
    fun initGame(){
        levels = intent.getIntExtra(Misc.data, 0)

        val arr = World.getAllCountries()
        var i = 0
        while (i < levels) {
            val tempCountry = arr.random()
            if(!arrCountries.contains(tempCountry)){
                arrCountries.add(tempCountry)
                i++
            }
        }

        textLevel.text = "Level: ${currentLevel + 1}/${levels}"
    }
}