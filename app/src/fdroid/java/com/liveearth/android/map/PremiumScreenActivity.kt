package com.liveearth.android.map

import android.os.Build
import android.util.Log
import android.view.View
import android.os.Bundle
import android.widget.Toast
import android.content.Intent
import android.content.Context
import android.app.AlarmManager
import android.app.PendingIntent
import android.os.CountDownTimer
import kotlin.system.exitProcess
import androidx.core.content.ContextCompat
import com.anjlab.android.iab.v3.PurchaseInfo
import com.liveearth.android.map.clasess.Misc
import androidx.appcompat.app.AppCompatActivity
import com.anjlab.android.iab.v3.BillingProcessor
import com.liveearth.android.map.clasess.Ads
import com.liveearth.android.map.databinding.ActivityPremiumScreenBinding
import com.liveearth.android.map.interfaces.InterstitialCallBack

class PremiumScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPremiumScreenBinding
    private lateinit var bp: BillingProcessor
    private var monthly = true
    private var yearly = false
    private var lifetime = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPremiumScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        billing()
        setListeners()

        binding.lifetimePrice.text =
            if(Misc.lifetimePrice != ""){
                Misc.lifetimePrice
            }else{
                "$99.00"
            }
        binding.yearlyPrice.text =
            if(Misc.yearlyPrice != ""){
                Misc.yearlyPrice
            }else{
                "$79.99"
            }
        binding.monthlyPrice.text =
            if(Misc.monthlyPrice != ""){
                Misc.monthlyPrice
            }else{
                "$39.99"
            }

    }

    private fun setListeners() {
        object : CountDownTimer(
            2500,
            1000
        ) {
            override fun onTick(p0: Long) {}
            override fun onFinish() {
                binding.close.visibility = View.VISIBLE
            }
        }.start()

        binding.purchaseButton.setOnClickListener {
            premiumButtonClick()
        }

        binding.monthly.setOnClickListener {
            monthly = true
            yearly = false
            lifetime = false

            binding.monthly.background =
                ContextCompat.getDrawable(this, R.drawable.selected_price_tag)
            binding.yearly.background =
                ContextCompat.getDrawable(this, R.drawable.unselected_price_tag)
            binding.lifetime.background =
                ContextCompat.getDrawable(this, R.drawable.unselected_price_tag)
        }

        binding.yearly.setOnClickListener {
            monthly = false
            yearly = true
            lifetime = false

            binding.monthly.background =
                ContextCompat.getDrawable(this, R.drawable.unselected_price_tag)
            binding.yearly.background =
                ContextCompat.getDrawable(this, R.drawable.selected_price_tag)
            binding.lifetime.background =
                ContextCompat.getDrawable(this, R.drawable.unselected_price_tag)
        }

        binding.lifetime.setOnClickListener {
            monthly = false
            yearly = false
            lifetime = true

            binding.monthly.background =
                ContextCompat.getDrawable(this, R.drawable.unselected_price_tag)
            binding.yearly.background =
                ContextCompat.getDrawable(this, R.drawable.unselected_price_tag)
            binding.lifetime.background =
                ContextCompat.getDrawable(this, R.drawable.selected_price_tag)
        }


        binding.close.setOnClickListener {
            Ads.showInterstitial(this, Misc.isPremiumScreenIntAm_Al, object : InterstitialCallBack {
                override fun onDismiss() {
                    if (intent.getStringExtra(Misc.data) != null) {
                        finish()
                    } else {
                        finish()
                        startActivity(Intent(this@PremiumScreenActivity, MainActivity::class.java))
                        Misc.adBreakCount = Misc.adBreakLimit
                    }
                }
            })
        }
    }

    private fun billing() {
        bp = BillingProcessor(
            this,
            getString(R.string.billing_id),
            object : BillingProcessor.IBillingHandler {
                override fun onProductPurchased(
                    productId: String,
                    details: PurchaseInfo?
                ) {
                    Misc.setPurchasedStatus(this@PremiumScreenActivity, true)
                    restartApplication(this@PremiumScreenActivity)
                }

                override fun onPurchaseHistoryRestored() {
                }

                override fun onBillingError(errorCode: Int, error: Throwable?) {
                    Log.e("TAG", "onBillingError: ${error?.message}")
                }

                override fun onBillingInitialized() {
                    Log.e("TAG", "onBillingInitialized: ")
                }
            })

        bp.initialize()
    }

    private fun premiumButtonClick() {
        if (!Misc.getPurchasedStatus(this)) {
            when {
                monthly -> {
                    bp.subscribe(this, "subscription_id_monthly")
                }
                yearly -> {
                    bp.subscribe(this, "subscription_id_yearly")
                }
                lifetime -> {
                    bp.purchase(this, Misc.inAppKey)
                }
            }
        } else {
            Toast.makeText(this, "You have already Purchased", Toast.LENGTH_SHORT).show()
        }
    }

    fun restartApplication(context: Context) {
        val intent = Intent(context.applicationContext, SplashScreenActivity::class.java)
        val pendingIntentId = 1122
        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.getActivity(
                context,
                pendingIntentId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        } else {
            PendingIntent.getActivity(
                context,
                pendingIntentId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
        val alarmManager =
            context.applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager[AlarmManager.RTC, System.currentTimeMillis() + 100] = pendingIntent
        exitProcess(0)
    }

//    private fun textGradient() {
//        val textPaint: TextPaint = binding.text1.paint
//        val width: Float = textPaint.measureText(binding.text1.text.toString())
//
//        val shaderText: Shader = LinearGradient(
//            0f, 0f, width, binding.text1.textSize, intArrayOf(
//                Color.parseColor("#FF2F49"), Color.parseColor("#FF0BB8")
//            ), null, Shader.TileMode.CLAMP
//        )
//        binding.text1.paint.shader = shaderText
//    }

    override fun onDestroy() {
        super.onDestroy()
        bp.release()
    }

    override fun onBackPressed() {
        if (intent.getStringExtra(Misc.data) != null) {
            Ads.showInterstitial(this, Misc.isPremiumScreenIntAm_Al, object : InterstitialCallBack {
                override fun onDismiss() {
                    finish()
                }
            })
        }
    }
}