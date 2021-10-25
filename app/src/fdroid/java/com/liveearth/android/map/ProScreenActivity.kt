package com.liveearth.android.map

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.Toast
import com.android.billingclient.api.*
import com.liveearth.android.map.clasess.Misc
import kotlinx.android.synthetic.fdroid.activity_pro_screen.*
import kotlinx.coroutines.*

@SuppressLint("LogNotTimber")
class ProScreenActivity : AppCompatActivity() {

    private var isBillingClientConnected = false

    private val purchasesUpdatedListener =
        PurchasesUpdatedListener { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                Misc.setPurchasedStatus(this, true)
                Log.d(Misc.logKey, "Ya hooo.....")
                Toast.makeText(this, "Restarting Application.", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, SplashScreenActivity::class.java))
                finish()
            }
        }

    private lateinit var billingClient: BillingClient

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pro_screen)

        btnUnlock.setOnClickListener {
            if (isBillingClientConnected) {
                GlobalScope.launch {
                    querySkuDetails()
                }
            } else {
                Toast.makeText(
                    this,
                    "Please check your internet connection and try again.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        val timer = object: CountDownTimer(5000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                textContinueCounter.text = millisUntilFinished.toString()[0].toString()
            }

            override fun onFinish() {
                textContinueCounter.text = ""

                btnContinue.setOnClickListener {
                    startActivity(Intent(this@ProScreenActivity, MainActivity::class.java))
                }
            }
        }
        timer.start()


        billingClient = BillingClient.newBuilder(this)
            .setListener(purchasesUpdatedListener)
            .enablePendingPurchases()
            .build()

        billingClient.startConnection(object : BillingClientStateListener {

            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    isBillingClientConnected = true
                    Log.d(Misc.logKey, "Billing Result Ok")
                }
            }

            override fun onBillingServiceDisconnected() {
                Log.d(Misc.logKey, "Service disconnected")
            }
        })
    }
    private suspend fun querySkuDetails() {
        try {
            val skuList = ArrayList<String>()
            skuList.add(Misc.inAppKey)

            val params = SkuDetailsParams.newBuilder()
            params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP)

            // leverage querySkuDetails Kotlin extension function
            val skuDetailsResult = withContext(Dispatchers.IO) {
                billingClient.querySkuDetails(params.build())
            }

            val flowParams = skuDetailsResult.skuDetailsList?.get(0)?.let {
                BillingFlowParams.newBuilder()
                    .setSkuDetails(it)
                    .build()
            }
            flowParams?.let {
                billingClient.launchBillingFlow(
                    this,
                    it
                ).responseCode
            }

            // Process the result.
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Not available yet.", Toast.LENGTH_SHORT).show()
        }
    }

}