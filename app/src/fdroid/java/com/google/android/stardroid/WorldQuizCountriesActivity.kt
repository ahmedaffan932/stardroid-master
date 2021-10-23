package com.google.android.stardroid

import android.annotation.SuppressLint
import android.content.Context
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
import com.google.firebase.FirebaseApp
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.fdroid.activity_world_quiz_countries.*
import kotlinx.android.synthetic.fdroid.activity_world_quiz_countries.clCountryInfo
import kotlinx.android.synthetic.fdroid.activity_world_quiz_countries.countryAreaInfo
import kotlinx.android.synthetic.fdroid.activity_world_quiz_countries.countryCapitalInfo
import kotlinx.android.synthetic.fdroid.activity_world_quiz_countries.countryCurrencyInfo
import kotlinx.android.synthetic.fdroid.activity_world_quiz_countries.countryNameInfo
import kotlinx.android.synthetic.fdroid.activity_world_quiz_countries.countryPopulationInfo
import kotlinx.android.synthetic.fdroid.activity_world_quiz_countries.flagCountryInfo
import kotlinx.android.synthetic.fdroid.activity_world_quiz_countries.webView
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


class WorldQuizCountriesActivity : AppCompatActivity() {
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
        setContentView(R.layout.activity_world_quiz_countries)

        webView.settings.javaScriptEnabled = true
        webView.settings.allowFileAccess = true
        webView.settings.setRenderPriority(WebSettings.RenderPriority.HIGH)
        webView.setBackgroundColor(getColor(R.color.background_color))



        btnConfirm.setOnClickListener {
            if (!isCompleted) {
                getResult()

                setCountryData(arrCountries[currentLevel])
                btnConfirmText.text = "Next"
                webView.loadUrl("javascript:selectCountry('${arrCountries[currentLevel].alpha2}');")
                blockView.visibility = View.VISIBLE
                isCompleted = true
            } else {
                webView.loadUrl("javascript:zoomOutByCountryId('${arrCountries[currentLevel].alpha2}');")
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

        blockView.setOnClickListener {
            webView.loadUrl("javascript:zoomOutByCountryId('${arrCountries[currentLevel].alpha2}');")
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
                runOnUiThread {
                    selectCountry(country)
                }
//                setCountryData(country)
            }
        }), "Android")


        Misc.wholeWorld
        webView.loadUrl("file:///android_asset/world/${Misc.gameContinent}.html")

        GlobalScope.launch {
//            getMapHtml(Misc.gameContinent)
//            when (Misc.gameContinent) {
//                Misc.africa -> {
//                    getMapHtml(Misc.africa)
//                    webView.loadUrl("file:///android_asset/world/${Misc.gameContinent}.html")
//                }
//                Misc.asia -> {
////                    getMapHtml(Misc.africa)
//                    webView.loadUrl("file:///android_asset/world/asia.html")
//                }
//                Misc.oceania -> {
////                    getMapHtml(Misc.oceania)
//                    webView.loadUrl("file:///android_asset/world/oceania.html")
//                }
//                Misc.america -> {
////                    getMapHtml(Misc.america)
//                    webView.loadUrl("file:///android_asset/world/america.html")
//                }
//                Misc.europe -> {
////                    getMapHtml(Misc.europe)
//                    webView.loadUrl("file:///android_asset/world/europe.html")
//                }
//                else -> {
////                    getMapHtml("world")
//                    webView.loadUrl("file:///android_asset/world/Map.html")
//                }
//            }
        }

        webView.webViewClient = object : WebViewClient() {
            override fun onReceivedError(
                view: WebView,
                request: WebResourceRequest,
                error: WebResourceError
            ) {
                super.onReceivedError(view, request, error)
                Log.d("TAG", error.description.toString())
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                llPBGame.visibility = View.GONE
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

    fun selectCountry(country: Country) {
        selectedCountry = country
        isCountrySelected = true
        btnConfirm.visibility = View.VISIBLE
    }

    @SuppressLint("SetTextI18n")
    fun getCurrentLevel() {
        try {
            textLevel.text = "Level: ${currentLevel + 1}/${levels}"
            when (Misc.gameMode) {
                Misc.countries -> {
                    findCountry.text = "Find: ${arrCountries[currentLevel].name}"
                }
                Misc.capitals -> {
                    findCountry.text = "${arrCountries[currentLevel].capital} is capital of ..."
                }
                Misc.currencies -> {
                    findCountry.text =
                        "${arrCountries[currentLevel].currency.name} (${arrCountries[currentLevel].currency.symbol}) is currency of ..."
                }
                Misc.flags -> {
                    flagCountryGame.visibility = View.VISIBLE
                    flagCountryGame.setImageResource(arrCountries[currentLevel].flagResource)
                    findCountry.text = "This is Flag of ..."
                }
            }
        } catch (e: Exception) {
            Misc.startActivity(
                this,
                Misc.isQuizCompeletedIntEnabled,
                object : StartActivityCallBack {
                    override fun onStart() {
                        finish()
                        val intent =
                            Intent(
                                this@WorldQuizCountriesActivity,
                                WorldQuizCompletedActivity::class.java
                            )
                        intent.putExtra(Misc.levels, levels)
                        intent.putExtra(Misc.data, numberOfCorrectAnswers)
                        startActivity(intent)
                    }
                })
        }
    }

    @SuppressLint("SetTextI18n")
    fun getResult() {
        if (selectedCountry == arrCountries[currentLevel]) {
            textResult.text = "Correct !"
            textResult.visibility = View.VISIBLE
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                textResult.setTextColor(getColor(R.color.green))
            }
            numberOfCorrectAnswers++
        } else {
            textResult.text = "Wrong !"
            textResult.visibility = View.VISIBLE
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                textResult.setTextColor(getColor(R.color.red))
            }
            textCorrectCountry.text = "You selected ${selectedCountry?.name}"
            textCorrectCountry.visibility = View.VISIBLE
        }
        if (Misc.gameMode == Misc.capitals) {
            findCountry.text =
                "${arrCountries[currentLevel].capital} is capital of ${arrCountries[currentLevel].name}"
        } else if (Misc.gameMode == Misc.flags) {
            findCountry.text =
                "his is flag of ${arrCountries[currentLevel].name}"
        } else if (Misc.gameMode == Misc.currencies) {
            findCountry.text =
                "${arrCountries[currentLevel].currency.name} (${arrCountries[currentLevel].currency.symbol}) is currency of ${arrCountries[currentLevel].name}"
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
//                    arr.drop(i)
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
    private suspend fun getMapHtml(mapName: String): String? {
        return try {
            val sharedPref = getSharedPreferences("SavedLanguages", Context.MODE_PRIVATE)

            var valueString = sharedPref?.getString(mapName, null)

            if (valueString != null) {
                Log.d("Getting Language", "Getting value from SP")
                return valueString
            }

//            FirebaseApp.initializeApp(applicationContext)
            val storage: FirebaseStorage =
                FirebaseStorage.getInstance()


            val islandRef = storage.reference.child("/$mapName.html")
            val fiftyKBs: Long = 1024 * 50
            Log.d("Getting Language", "Getting value from FB")
            valueString = String(islandRef.getBytes(fiftyKBs).await())
            sharedPref?.edit()?.putString(mapName, valueString)?.apply()

            valueString.let { webView.loadUrl(it) }
            valueString
        } catch (e: Exception) {
            e.printStackTrace()
            "Unable to fetch value, please check your internet."
        }
    }

}