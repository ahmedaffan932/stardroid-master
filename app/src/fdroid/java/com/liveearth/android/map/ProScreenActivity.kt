package com.liveearth.android.map

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.Toast
import com.android.billingclient.api.*
import com.liveearth.android.map.clasess.Misc
import com.liveearth.android.map.interfaces.OnBackPressCallBack
import com.liveearth.android.map.interfaces.StartActivityCallBack
import kotlinx.android.synthetic.fdroid.activity_pro_screen.*
import kotlinx.coroutines.*

@SuppressLint("LogNotTimber")
class ProScreenActivity : AppCompatActivity() {

    private var isBillingClientConnected = false
    private lateinit var billingClient: BillingClient
    private val purchasesUpdatedListener =
        PurchasesUpdatedListener { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                Misc.setPurchasedStatus(this, true)
                for (p in purchases){
                    Log.d(Misc.logKey, p.orderId)
                }
                GlobalScope.launch {
                    handlePurchase(purchases[0])
                }
                Log.d(Misc.logKey, "Ya hooo.....")
                Toast.makeText(this, "Restarting Application.", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, SplashScreenActivity::class.java))
                finish()
            }
        }


    @OptIn(DelicateCoroutinesApi::class)
    public override fun onCreate(savedInstanceState: Bundle?) {
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

        if (BuildConfig.DEBUG) {
            btnContinue.setOnClickListener {
                startActivity(Intent(this@ProScreenActivity, MainActivity::class.java))
            }
        }

        privacyPolicy.setOnClickListener {
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://sites.google.com/view/liveearthmapxtreamapps/home")
            )
            startActivity(intent)
        }
        if(intent.getStringExtra(Misc.data) == null) {
            val timer = object : CountDownTimer(3000, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                }

                override fun onFinish() {
                    btnContinue.visibility = View.VISIBLE
                    btnContinue.setOnClickListener {
                        Misc.startActivity(this@ProScreenActivity, Misc.isMainFromProScreenIntEnabled, object : StartActivityCallBack{
                            override fun onStart() {
                                startActivity(Intent(this@ProScreenActivity, MainActivity::class.java))
                            }
                        })
                    }
                }
            }
            timer.start()
        }else{
            btnContinue.visibility = View.VISIBLE

            btnContinue.setOnClickListener {
                onBackPressed()
            }
        }


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

    override fun onBackPressed() {
        if (intent.getStringExtra(Misc.data) != null){
            Misc.onBackPress(this, Misc.isProScreenBackIntEnabled, object : OnBackPressCallBack {
                override fun onBackPress() {
                    finish()
                }
            })
//            super.onBackPressed()
        }

    }

    private suspend fun handlePurchase(purchase: Purchase) {
        val consumeParams =
            ConsumeParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .build()
        val consumeResult = withContext(Dispatchers.IO) {
            billingClient.consumePurchase(consumeParams)
        }

        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED)
            if (!purchase.isAcknowledged) {
                val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                val ackPurchaseResult = withContext(Dispatchers.IO) {
                    billingClient.acknowledgePurchase(acknowledgePurchaseParams.build())
                }
            }
    }

}