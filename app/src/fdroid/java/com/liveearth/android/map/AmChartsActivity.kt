package com.liveearth.android.map

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.print.PrintAttributes
import android.print.PrintJob
import android.print.PrintManager
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.OvershootInterpolator
import android.view.inputmethod.InputMethodManager
import android.webkit.*
import android.widget.SearchView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.blongho.country_data.Country
import com.blongho.country_data.World
import com.liveearth.android.map.R
import com.liveearth.android.map.adapters.CountryAdapter
import com.liveearth.android.map.clasess.Misc
import com.liveearth.android.map.interfaces.CountryListInterface
import com.liveearth.android.map.interfaces.WebAppInterface
import kotlinx.android.synthetic.main.activity_am_chatrs.*
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent.setEventListener
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener

open class AmChartsActivity : BaseActivity(), SearchView.OnQueryTextListener {

    lateinit var adapter: CountryAdapter
    var isCountrySelected = false

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetJavaScriptEnabled", "JavascriptInterface", "ObsoleteSdkInt", "LogNotTimber")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_am_chatrs)

        btnBackViewWorld.setOnClickListener {
            onBackPressed()
        }

        setEventListener(this, object : KeyboardVisibilityEventListener {
            override fun onVisibilityChanged(isOpen: Boolean) {
                if (isOpen) {
                    Misc.hideView(
                        clCountryInfo,
                        this@AmChartsActivity,
                        clCountryInfo.visibility == View.VISIBLE
                    )
                }
            }
        })

        recyclerViewCountryList.layoutManager = LinearLayoutManager(this)
        World.init(this)
        adapter = CountryAdapter(World.getAllCountries(), this, object : CountryListInterface {
            @SuppressLint("SetTextI18n")
            override fun onCountryClick(country: Country) {
                webView.loadUrl("javascript:selectCountry('${country.alpha2}');")
                setCountryData(country)

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

        webView.addJavascriptInterface(WebAppInterface(this, object : CountryListInterface {
            @SuppressLint("SetTextI18n")
            override fun onCountryClick(country: Country) {
                setCountryData(country)
            }
        }), "Android")

        webView.loadUrl("file:///android_asset/world/${Misc.wholeWorld}.html")

        webView.webViewClient = object : WebViewClient() {
            override fun onReceivedError(
                view: WebView,
                request: WebResourceRequest,
                error: WebResourceError
            ) {
                super.onReceivedError(view, request, error)
                Log.d("TAG", error.description.toString())
            }

            override fun shouldOverrideUrlLoading(view: WebView, url: String?): Boolean {
                // TODO Auto-generated method stub
                view.loadUrl(url!!)
                return true
            }
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                Log.d(Misc.logKey, "Page loaded.")
                llPBViewWorld.visibility = View.GONE
            }
        }

        checkBoxCountryList.setOnCheckedChangeListener { compoundButton, b ->
            if(compoundButton.isChecked){
                simpleSearchView.visibility = View.VISIBLE
                recyclerViewCountryList.visibility = View.VISIBLE
            }else {
                simpleSearchView.visibility = View.GONE
                recyclerViewCountryList.visibility = View.GONE
            }
        }
        simpleSearchView.setOnQueryTextListener(this)
    }

    override fun onBackPressed() {
        if (isCountrySelected) {
            webView.loadUrl("javascript:goToHome();")
            isCountrySelected = false
            val a: Animation =
                AnimationUtils.loadAnimation(
                    this@AmChartsActivity,
                    R.anim.slide_from_right_to_left
                )
            a.duration = 300
            a.interpolator = OvershootInterpolator()
            clCountryInfo.startAnimation(a)
            Handler().postDelayed({
                clCountryInfo.visibility = View.GONE
            }, 300)
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

    @SuppressLint("SetTextI18n", "LogNotTimber")
    private fun setCountryData(country: Country) {

        Log.d(Misc.logKey, "Country selected.")
        isCountrySelected = true

        runOnUiThread {
            flagCountryInfo.setImageResource(World.getFlagOf(country.alpha2))
            countryNameInfo.text = country.name
            countryCapitalInfo.text = "Capital: ${country.capital}"
            countryPopulationInfo.text = "Population: ${country.population}"
            countryAreaInfo.text = "Area: ${country.area} km²"
            countryCurrencyInfo.text =
                "Currency: ${country.currency.name} (${country.currency.symbol})"
            clCountryInfo.visibility = View.VISIBLE
        }
        val a: Animation =
            AnimationUtils.loadAnimation(
                this@AmChartsActivity,
                R.anim.slide_from_left_to_right
            )
        a.duration = 300
        a.interpolator = OvershootInterpolator()
        clCountryInfo.startAnimation(a)
    }


}