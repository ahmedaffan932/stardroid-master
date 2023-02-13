package com.liveearth.android.map.clasess

import android.util.Log
import android.app.Activity
import android.annotation.SuppressLint
import android.content.Intent
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.LoadAdError
import com.liveearth.android.map.PremiumScreenActivity
import com.liveearth.android.map.R
import com.liveearth.android.map.databinding.AdmobNativeBinding
import com.liveearth.android.map.databinding.AdmobSmallNativeAdBinding
import com.liveearth.android.map.databinding.AdmobSmallNativeAdSmallBtnBinding
import com.liveearth.android.map.interfaces.LoadInterstitialCallBack
import com.liveearth.android.map.interfaces.NativeAdCallBack

@SuppressLint("LogNotTimber")
object AdMobNative {

    fun loadNativeOne(activity: Activity, callBack: LoadInterstitialCallBack?) {
        Ads.mNativeAdAdMobOne = null
        if (Misc.getPurchasedStatus(activity)) return

        val adLoader: AdLoader =
            AdLoader.Builder(activity, Misc.nativeAdIdAdMobOne).forNativeAd { nativeAd ->
                Log.d(Misc.logKey, "Native Ad Loaded")
                Ads.mNativeAdAdMobOne = nativeAd
                callBack?.onLoaded()

            }.withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    callBack?.onFailed()
                    Ads.mNativeAdAdMobOne = null
                    Log.e(Misc.logKey, adError.message)
                }
            }).build()
        adLoader.loadAd(AdRequest.Builder().build())
    }

    fun loadNativeTwo(activity: Activity, callBack: LoadInterstitialCallBack?) {
        Ads.mNativeAdAdMobTwo = null
        if (Misc.getPurchasedStatus(activity)) return
        val adLoader: AdLoader =
            AdLoader.Builder(activity, Misc.nativeAdIdAdMobTwo).forNativeAd { nativeAd ->
                Log.d(Misc.logKey, "Native Ad Loaded")
                Ads.mNativeAdAdMobTwo = nativeAd

                callBack?.onLoaded()

            }.withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    callBack?.onFailed()
                    Ads.mNativeAdAdMobTwo = null
                    Log.e(Misc.logKey, adError.message)
                }
            }).build()
        adLoader.loadAd(AdRequest.Builder().build())
    }

    fun showNativeAd(
        activity: Activity,
        amLayout: FrameLayout,
        callBack: NativeAdCallBack?
    ) {
        val nativeAdToShow = if (Ads.mNativeAdAdMobOne != null) {
            Ads.mNativeAdAdMobOne
        } else {
            Ads.mNativeAdAdMobTwo
        }

        if (nativeAdToShow != null) {

            val clNativeAd = AdmobNativeBinding.inflate(LayoutInflater.from(activity))
            val nativeAdView = clNativeAd.nativeAd

            amLayout.removeAllViews()
            clNativeAd.clRemoveAd.setOnClickListener {
                val intent = Intent(activity, PremiumScreenActivity::class.java)
                intent.putExtra(Misc.data, Misc.data)
                activity.startActivity(intent)
            }

            amLayout.addView(clNativeAd.root)
            amLayout.setBackgroundResource(R.drawable.bg_native_ad)

            nativeAdView.mediaView = nativeAdView.findViewById(R.id.ad_media)
            nativeAdView.headlineView = nativeAdView.findViewById(R.id.ad_headline)
            nativeAdView.bodyView = nativeAdView.findViewById(R.id.ad_body)
            nativeAdView.callToActionView = nativeAdView.findViewById(R.id.ad_call_to_action)
            nativeAdView.iconView = nativeAdView.findViewById(R.id.ad_app_icon)

            (nativeAdView.headlineView as TextView).text = nativeAdToShow.headline
            nativeAdToShow.mediaContent?.let { nativeAdView.mediaView?.setMediaContent(it) }

            if (nativeAdToShow.body == null) {
                nativeAdView.bodyView?.visibility = View.INVISIBLE
            } else {
                nativeAdView.bodyView?.visibility = View.VISIBLE
                (nativeAdView.bodyView as TextView).text = nativeAdToShow.body
            }

            if (nativeAdToShow.callToAction == null) {
                nativeAdView.callToActionView?.visibility = View.INVISIBLE
            } else {
                nativeAdView.callToActionView?.visibility = View.VISIBLE
                (nativeAdView.callToActionView as Button).text = nativeAdToShow.callToAction
            }

            if (nativeAdToShow.icon == null) {
                nativeAdView.iconView?.visibility = View.GONE
            } else {
                (nativeAdView.iconView as ImageView).setImageDrawable(nativeAdToShow.icon?.drawable)
                nativeAdView.iconView?.visibility = View.VISIBLE
            }

            nativeAdView.setNativeAd(nativeAdToShow)
            amLayout.visibility = View.VISIBLE
            callBack?.onLoad()
        } else {
            amLayout.visibility = View.GONE
        }

        object : CountDownTimer(1000, 2000) {
            override fun onTick(p0: Long) {}
            override fun onFinish() {
                if (nativeAdToShow == Ads.mNativeAdAdMobOne) {
                    Ads.mNativeAdAdMobOne = null
                } else {
                    Ads.mNativeAdAdMobTwo = null
                }
                Ads.loadAdMobNativeAd(activity, null)
            }
        }.start()
    }

    fun showSmallNativeAd(
        activity: Activity,
        amLayout: FrameLayout,
        callBack: NativeAdCallBack?
    ) {
        if(Misc.isSmallNativeSmallBtn){
            showSmallNativeAdSmallBtn(activity,amLayout,callBack)
        }else{
            showSmallNativeAdLargeBtn(activity,amLayout,callBack)
        }
    }

    private fun showSmallNativeAdLargeBtn(
        activity: Activity,
        amLayout: FrameLayout,
        callBack: NativeAdCallBack?
    ) {
        val nativeAdToShow = if (Ads.mNativeAdAdMobOne != null) {
            Ads.mNativeAdAdMobOne
        } else {
            Ads.mNativeAdAdMobTwo
        }

        if (nativeAdToShow != null) {
            val clNativeAd = AdmobSmallNativeAdBinding.inflate(LayoutInflater.from(activity))
            val nativeAdView = clNativeAd.nativeAd

            amLayout.removeAllViews()
            clNativeAd.clRemoveAd.setOnClickListener {
                val intent = Intent(activity, PremiumScreenActivity::class.java)
                intent.putExtra(Misc.data, Misc.data)
                activity.startActivity(intent)
            }


            if (!Misc.isRemoveAdTagEnabled) {
                clNativeAd.clRemoveAd.visibility = View.GONE
            }
            amLayout.addView(clNativeAd.root)

            nativeAdView.mediaView = nativeAdView.findViewById(R.id.ad_media)
            nativeAdView.headlineView = nativeAdView.findViewById(R.id.ad_headline)
            nativeAdView.bodyView = nativeAdView.findViewById(R.id.ad_body)
            nativeAdView.callToActionView = nativeAdView.findViewById(R.id.ad_call_to_action)
            nativeAdView.iconView = nativeAdView.findViewById(R.id.ad_app_icon)

            (nativeAdView.headlineView as TextView).text = nativeAdToShow.headline
            nativeAdToShow.mediaContent?.let { nativeAdView.mediaView?.setMediaContent(it) }

            if (nativeAdToShow.body == null) {
                nativeAdView.bodyView?.visibility = View.INVISIBLE
            } else {
                nativeAdView.bodyView?.visibility = View.VISIBLE
                (nativeAdView.bodyView as TextView).text = nativeAdToShow.body
            }

            if (nativeAdToShow.callToAction == null) {
                nativeAdView.callToActionView?.visibility = View.INVISIBLE
            } else {
                nativeAdView.callToActionView?.visibility = View.VISIBLE
                (nativeAdView.callToActionView as Button).text = nativeAdToShow.callToAction
            }

            if (nativeAdToShow.icon == null) {
                nativeAdView.iconView?.visibility = View.GONE
            } else {
                (nativeAdView.iconView as ImageView).setImageDrawable(nativeAdToShow.icon?.drawable)
                nativeAdView.iconView?.visibility = View.VISIBLE
            }

            nativeAdView.setNativeAd(nativeAdToShow)
            amLayout.visibility = View.VISIBLE
            callBack?.onLoad()
        } else {
            amLayout.visibility = View.GONE
        }

        object : CountDownTimer(1000, 2000) {
            override fun onTick(p0: Long) {}
            override fun onFinish() {
                if (nativeAdToShow == Ads.mNativeAdAdMobOne) {
                    Ads.mNativeAdAdMobOne = null
                } else {
                    Ads.mNativeAdAdMobTwo = null
                }
                Ads.loadAdMobNativeAd(activity, null)
            }
        }.start()
    }

    fun showSmallNativeAdSmallBtn(
        activity: Activity,
        amLayout: FrameLayout,
        callBack: NativeAdCallBack?
    ) {
        val nativeAdToShow = if (Ads.mNativeAdAdMobOne != null) {
            Ads.mNativeAdAdMobOne
        } else {
            Ads.mNativeAdAdMobTwo
        }

        if (nativeAdToShow != null) {
            val clNativeAd =
                AdmobSmallNativeAdSmallBtnBinding.inflate(LayoutInflater.from(activity))
            val nativeAdView = clNativeAd.nativeAd

            amLayout.removeAllViews()
            clNativeAd.clRemoveAd.setOnClickListener {
                val intent = Intent(activity, PremiumScreenActivity::class.java)
                intent.putExtra(Misc.data, Misc.data)
                activity.startActivity(intent)
            }


            if (!Misc.isRemoveAdTagEnabled) {
                clNativeAd.clRemoveAd.visibility = View.GONE
            }
            amLayout.addView(clNativeAd.root)

            nativeAdView.mediaView = nativeAdView.findViewById(R.id.ad_media)
            nativeAdView.headlineView = nativeAdView.findViewById(R.id.ad_headline)
            nativeAdView.bodyView = nativeAdView.findViewById(R.id.ad_body)
            nativeAdView.callToActionView = nativeAdView.findViewById(R.id.ad_call_to_action)
            nativeAdView.iconView = nativeAdView.findViewById(R.id.ad_app_icon)

            (nativeAdView.headlineView as TextView).text = nativeAdToShow.headline
            nativeAdToShow.mediaContent?.let { nativeAdView.mediaView?.setMediaContent(it) }

//            if (nativeAdToShow.body == null) {
//                nativeAdView.bodyView?.visibility = View.INVISIBLE
//            } else {
//                nativeAdView.bodyView?.visibility = View.VISIBLE
//                (nativeAdView.bodyView as TextView).text = nativeAdToShow.body
//            }

            if (nativeAdToShow.callToAction == null) {
                nativeAdView.callToActionView?.visibility = View.INVISIBLE
            } else {
                nativeAdView.callToActionView?.visibility = View.VISIBLE
                (nativeAdView.callToActionView as Button).text = nativeAdToShow.callToAction
            }

            if (nativeAdToShow.icon == null) {
                nativeAdView.iconView?.visibility = View.GONE
            } else {
                (nativeAdView.iconView as ImageView).setImageDrawable(nativeAdToShow.icon?.drawable)
                nativeAdView.iconView?.visibility = View.VISIBLE
            }

            nativeAdView.setNativeAd(nativeAdToShow)
            amLayout.visibility = View.VISIBLE
            callBack?.onLoad()
        } else {
            amLayout.visibility = View.GONE
        }

        object : CountDownTimer(1000, 2000) {
            override fun onTick(p0: Long) {}
            override fun onFinish() {
                if (nativeAdToShow == Ads.mNativeAdAdMobOne) {
                    Ads.mNativeAdAdMobOne = null
                } else {
                    Ads.mNativeAdAdMobTwo = null
                }
                Ads.loadAdMobNativeAd(activity, null)
            }
        }.start()
    }
}