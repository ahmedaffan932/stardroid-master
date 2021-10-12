package com.google.android.stardroid

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
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

import android.webkit.WebView





class AmChartsActivity : AppCompatActivity() {
    private lateinit var webView: WebView

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetJavaScriptEnabled", "JavascriptInterface")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_am_chatrs)

        webView = WebView(this)

        webView.settings.javaScriptEnabled = true
        webView.settings.allowFileAccess = true
        webView.settings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // chromium, enable hardware acceleration
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            // older android version, disable hardware acceleration
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        webView.settings.cacheMode = WebSettings.LOAD_NO_CACHE;

        webView.addJavascriptInterface(WebAppInterface(this), "Android")
        webView.loadUrl("file:///android_asset/Map.html")
        setContentView(webView)

        webView.webViewClient = object : WebViewClient() {
            override fun onReceivedError(
                view: WebView,
                request: WebResourceRequest,
                error: WebResourceError
            ) {
                super.onReceivedError(view, request, error)
                Log.d("TAG", error.description.toString())
                // handler.proceed(); This line wont make a different on API 29, Webview still bank
            }
        }

//        createWebPagePrint(webView)
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }

    fun createWebPagePrint(webView: WebView) {
        /*if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT)
            return;*/
        val printManager = getSystemService(Context.PRINT_SERVICE) as PrintManager
        val printAdapter = webView.createPrintDocumentAdapter()
        val jobName = " Document"
        val builder = PrintAttributes.Builder()
        builder.setMediaSize(PrintAttributes.MediaSize.ISO_A5)
        val printJob: PrintJob = printManager.print(jobName, printAdapter, builder.build())
        if (printJob.isCompleted()) {
            Toast.makeText(applicationContext, "R.string.print_complete", Toast.LENGTH_LONG).show()
        } else if (printJob.isFailed()) {
            Toast.makeText(applicationContext, "R.string.print_failed", Toast.LENGTH_LONG).show()
        }
        // Save the job object for later status checking
    }
}