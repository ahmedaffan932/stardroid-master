package com.liveearth.android.map.clasess

import android.util.Log
import android.view.View
import com.facebook.ads.*
import android.app.Activity
import android.widget.Button
import android.content.Context
import android.widget.TextView
import android.widget.FrameLayout
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.liveearth.android.map.R
import android.annotation.SuppressLint
import androidx.constraintlayout.widget.ConstraintLayout
import com.liveearth.android.map.interfaces.InterstitialCallBack
import com.liveearth.android.map.interfaces.LoadInterstitialCallBack
import com.liveearth.android.map.databinding.FbNaiveAdInflaterBinding

@SuppressLint("LogNotTimber")
object FacebookAds {
    private var nativeAd: NativeAd? = null
    private var interstitialAd: InterstitialAd? = null

    fun loadNativeAd(context: Context, callBack: LoadInterstitialCallBack? = null) {

        nativeAd = NativeAd(context, Ads.nativeAdIdFB)
        val nativeAdListener: NativeAdListener = object : NativeAdListener {
            override fun onMediaDownloaded(ad: Ad) {
                Log.e(Misc.logKey, "Native ad finished downloading all assets.")
            }

            override fun onError(ad: Ad?, adError: AdError) {
                Log.e(Misc.logKey, "Native ad failed to load: " + adError.errorMessage)
                nativeAd = null
            }

            override fun onAdLoaded(ad: Ad) {
                Log.d(Misc.logKey, "Native ad is loaded and ready to be displayed!")
                callBack?.onLoaded()
            }

            override fun onAdClicked(ad: Ad) {
                Log.d(Misc.logKey, "Native ad clicked!")
            }

            override fun onLoggingImpression(ad: Ad) {
                Log.d(Misc.logKey, "Native ad impression logged!")
            }
        }

        nativeAd!!.loadAd(nativeAd!!.buildLoadAdConfig().withAdListener(nativeAdListener).build())
    }


    fun showNativeAd(context: Context, frameLayout: FrameLayout) {

        nativeAd?.unregisterView()

        val nativeAdBinding: FbNaiveAdInflaterBinding =
            FbNaiveAdInflaterBinding.inflate(LayoutInflater.from(context))

        val inflater: LayoutInflater = LayoutInflater.from(context)
        val adView =
            inflater.inflate(
                R.layout.facebook_native_ad,
                nativeAdBinding.root,
                false
            ) as ConstraintLayout
        nativeAdBinding.root.addView(adView)

        frameLayout.addView(nativeAdBinding.root)
        frameLayout.visibility = View.VISIBLE

        val adChoicesContainer: LinearLayout = adView.findViewById(R.id.ad_choices_container)
        val adOptionsView = AdOptionsView(context, nativeAd, nativeAdBinding.root)
        adChoicesContainer.removeAllViews()
        adChoicesContainer.addView(adOptionsView, 0)

        val nativeAdBody: TextView = adView.findViewById(R.id.native_ad_body)
        val nativeAdIcon: MediaView = adView.findViewById(R.id.native_ad_icon)
        val nativeAdTitle: TextView = adView.findViewById(R.id.native_ad_title)
        val nativeAdMedia: MediaView = adView.findViewById(R.id.native_ad_media)
        val sponsoredLabel: TextView = adView.findViewById(R.id.native_ad_sponsored_label)
        val nativeAdCallToAction: Button = adView.findViewById(R.id.native_ad_call_to_action)
        val nativeAdSocialContext: TextView = adView.findViewById(R.id.native_ad_social_context)

        nativeAdBody.text = nativeAd?.adBodyText
        nativeAdTitle.text = nativeAd?.advertiserName
        sponsoredLabel.text = nativeAd?.sponsoredTranslation
        nativeAdCallToAction.text = nativeAd?.adCallToAction
        nativeAdSocialContext.text = nativeAd?.adSocialContext
        nativeAdCallToAction.visibility =
            if (nativeAd?.hasCallToAction()!!) View.VISIBLE else View.INVISIBLE

        val clickableViews: MutableList<View> = ArrayList()
        clickableViews.add(nativeAdBody)
        clickableViews.add(nativeAdIcon)
        clickableViews.add(nativeAdTitle)
        clickableViews.add(nativeAdMedia)
        clickableViews.add(sponsoredLabel)
        clickableViews.add(nativeAdCallToAction)
        clickableViews.add(nativeAdSocialContext)

        nativeAd?.registerViewForInteraction(
            adView, nativeAdMedia, nativeAdIcon, clickableViews
        )
        loadNativeAd(context, null)
    }


    fun loadInterstitialAd(context: Context, onLoadCallBack: LoadInterstitialCallBack?) {
        interstitialAd = InterstitialAd(context, Ads.interstitialAdIdFB)

        val interstitialAdListener: InterstitialAdListener = object : InterstitialAdListener {
            override fun onInterstitialDisplayed(ad: Ad) {
                Log.e(Misc.logKey, "Interstitial ad displayed.")
            }

            override fun onInterstitialDismissed(ad: Ad) {
                Log.e(Misc.logKey, "Interstitial ad dismissed.")
            }

            override fun onError(ad: Ad?, adError: AdError) {
                Log.e(Misc.logKey, "Interstitial ad failed to load: " + adError.errorMessage)
                onLoadCallBack?.onFailed()
            }

            override fun onAdLoaded(ad: Ad) {
                Log.d(Misc.logKey, "Interstitial ad is loaded and ready to be displayed!")
                onLoadCallBack?.onLoaded()
            }

            override fun onAdClicked(ad: Ad) {
                Log.d(Misc.logKey, "Interstitial ad clicked!")
            }

            override fun onLoggingImpression(ad: Ad) {
                Log.d(Misc.logKey, "Interstitial ad impression logged!")
            }
        }

        interstitialAd!!.loadAd(
            interstitialAd!!.buildLoadAdConfig()
                .withAdListener(interstitialAdListener)
                .build()
        )

    }

    fun showInterstitial(activity: Activity, callBack: InterstitialCallBack? = null) {
        val interstitialAdListener: InterstitialAdListener = object : InterstitialAdListener {
            override fun onInterstitialDisplayed(ad: Ad) {
                Log.e(Misc.logKey, "Interstitial ad displayed.")
            }

            override fun onInterstitialDismissed(ad: Ad) {
                Log.e(Misc.logKey, "Interstitial ad dismissed.")
                callBack?.onDismiss()
                loadInterstitialAd(activity, null)
            }

            override fun onError(ad: Ad?, adError: AdError) {
                Log.e(Misc.logKey, "Interstitial ad failed to load: " + adError.errorMessage)
            }

            override fun onAdLoaded(ad: Ad) {
                Log.d(Misc.logKey, "Interstitial ad is loaded and ready to be displayed!")
            }

            override fun onAdClicked(ad: Ad) {
                Log.d(Misc.logKey, "Interstitial ad clicked!")
            }

            override fun onLoggingImpression(ad: Ad) {
                Log.d(Misc.logKey, "Interstitial ad impression logged!")
            }
        }
        interstitialAd?.buildLoadAdConfig()?.withAdListener(interstitialAdListener)

        if (interstitialAd != null) {
            interstitialAd?.show()
        } else {
            callBack?.onDismiss()
            loadInterstitialAd(activity, null)
            Log.d(Misc.logKey, "The interstitial ad wasn't ready yet.")
        }
    }
}