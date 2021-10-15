package com.google.android.stardroid

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.webkit.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.android.stardroid.interfaces.WebAppInterface
import android.widget.Toast

import android.print.PrintAttributes

import android.print.PrintJob

import android.print.PrintManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.Interpolator
import android.view.animation.OvershootInterpolator
import android.view.inputmethod.InputMethodManager

import android.webkit.WebView
import android.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.blongho.country_data.Country
import com.blongho.country_data.World
import com.google.android.stardroid.adapters.CountryAdapter
import com.google.android.stardroid.clasess.Misc
import com.google.android.stardroid.interfaces.CountryListInterface
import kotlinx.android.synthetic.main.activity_am_chatrs.*


class AmChartsActivity : AppCompatActivity(), SearchView.OnQueryTextListener {

    lateinit var adapter: CountryAdapter

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetJavaScriptEnabled", "JavascriptInterface", "ObsoleteSdkInt", "LogNotTimber")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_am_chatrs)

        btnBackViewWorld.setOnClickListener {
            onBackPressed()
        }

        recyclerViewCountryList.layoutManager = LinearLayoutManager(this)
        adapter = CountryAdapter(World.getAllCountries(), this, object : CountryListInterface {
            @SuppressLint("SetTextI18n")
            override fun onCountryClick(country: Country) {
                webView.loadUrl("javascript:selectCountry('${country.alpha2}');")
                val a: Animation =
                    AnimationUtils.loadAnimation(
                        this@AmChartsActivity,
                        R.anim.slide_from_left_to_right
                    )
                a.duration = 300
                a.interpolator  = OvershootInterpolator()
                clCountryInfo.startAnimation(a)
                clCountryInfo.visibility = View.VISIBLE
                flagCountryInfo.setImageResource(World.getFlagOf(country.alpha2))
                countryNameInfo.text = country.name
                countryCapitalInfo.text = "Capital: ${country.capital}"
                countryPopulationInfo.text = "Population: ${country.population}"
                countryAreaInfo.text = "Area: ${country.area} km²"
                countryCurrencyInfo.text =
                    "Currency: ${country.currency.name} (${country.currency.symbol})"

                val inputManager: InputMethodManager =
                    getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

                if (currentFocus != null) {
                    inputManager.hideSoftInputFromWindow(
                        currentFocus!!.windowToken,
                        InputMethodManager.HIDE_NOT_ALWAYS
                    )
                }
            }
        })
        recyclerViewCountryList.adapter = adapter

        webView.settings.javaScriptEnabled = true
        webView.settings.allowFileAccess = true
        webView.settings.setRenderPriority(WebSettings.RenderPriority.HIGH)
        webView.setBackgroundColor(getColor(R.color.background_color))

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // chromium, enable hardware acceleration
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        } else {
            // older android version, disable hardware acceleration
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        }
        webView.settings.cacheMode = WebSettings.LOAD_NO_CACHE;

        webView.addJavascriptInterface(WebAppInterface(this), "Android")

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

        simpleSearchView.setOnQueryTextListener(this)
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }

    private fun createWebPagePrint(webView: WebView) {
        /*if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT)
            return;*/
        val printManager = getSystemService(Context.PRINT_SERVICE) as PrintManager
        val printAdapter = webView.createPrintDocumentAdapter()
        val jobName = " Document"
        val builder = PrintAttributes.Builder()
        builder.setMediaSize(PrintAttributes.MediaSize.ISO_A5)
        val printJob: PrintJob = printManager.print(jobName, printAdapter, builder.build())
        if (printJob.isCompleted) {
            Toast.makeText(applicationContext, "R.string.print_complete", Toast.LENGTH_LONG).show()
        } else if (printJob.isFailed) {
            Toast.makeText(applicationContext, "R.string.print_failed", Toast.LENGTH_LONG).show()
        }
    }

    override fun onQueryTextSubmit(p0: String?): Boolean {
        val inputManager: InputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        if (currentFocus != null) {
            inputManager.hideSoftInputFromWindow(
                currentFocus!!.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
        return true
    }

    @SuppressLint("LogNotTimber")
    override fun onQueryTextChange(p0: String?): Boolean {
        adapter.filter.filter(p0)
        recyclerViewCountryList.adapter = adapter
        Log.d(Misc.logKey, "onQueryTextChange")
        return true
    }
}