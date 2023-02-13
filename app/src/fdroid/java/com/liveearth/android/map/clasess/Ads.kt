package com.liveearth.android.map.clasess

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.applovin.mediation.*
import com.applovin.mediation.ads.MaxAdView
import com.applovin.mediation.ads.MaxInterstitialAd
import com.applovin.mediation.nativeAds.MaxNativeAdListener
import com.applovin.mediation.nativeAds.MaxNativeAdLoader
import com.applovin.mediation.nativeAds.MaxNativeAdView
import com.applovin.mediation.nativeAds.MaxNativeAdViewBinder
import com.applovin.sdk.AppLovinSdkUtils
import com.google.android.gms.ads.*
import com.google.android.gms.ads.nativead.NativeAd
import com.liveearth.android.map.R
import com.liveearth.android.map.clasess.Misc.Companion.logKey
import com.liveearth.android.map.clasess.Misc.Companion.nativeFailedCount
import com.liveearth.android.map.interfaces.InterstitialCallBack
import com.liveearth.android.map.interfaces.LoadInterstitialCallBack
import com.liveearth.android.map.interfaces.NativeAdCallBack
import kotlinx.android.synthetic.fdroid.admob_native.view.*

@SuppressLint("LogNotTimber")
class Ads {
    companion object {
        @SuppressLint("StaticFieldLeak")
        private lateinit var adView: MaxAdView
        private var isBannerAdLoaded = false

        var isSplashNativeDisplayed = false

        var mNativeAdAdMobOne: NativeAd? = null
        var mNativeAdAdMobTwo: NativeAd? = null

        @SuppressLint("StaticFieldLeak")
        var nativeAdView: MaxNativeAdView? = null
        var mNativeAd: MaxAd? = null
        var mInterstitialAdApplovin: MaxInterstitialAd? = null
        lateinit var nativeAdLoader: MaxNativeAdLoader

        fun loadApplovinInterstitial(activity: Activity, callback: LoadInterstitialCallBack?) {
            if (!Misc.getPurchasedStatus(activity) && Misc.interstitialAdIdApplovin != "" && Misc.checkInternetConnection(
                    activity
                )
            ) {
                try {
                    mInterstitialAdApplovin =
                        MaxInterstitialAd(Misc.interstitialAdIdApplovin, activity)
                    mInterstitialAdApplovin?.loadAd()
                    callback?.onLoaded()
                } catch (e: Exception) {
                    callback?.onFailed()
                    e.printStackTrace()
                }
            }
        }

        private fun showApplovinInterstitial(activity: Activity, callBack: InterstitialCallBack?) {
            if (mInterstitialAdApplovin != null) {
                Log.d(logKey, "Filled")
                if (mInterstitialAdApplovin?.isReady == true) {
                    Log.d(logKey, "Ready")
                    mInterstitialAdApplovin?.showAd()
                } else {
                    callBack?.onDismiss()
                }
            } else {
                loadApplovinInterstitial(activity, null)
                callBack?.onDismiss()
                Log.d(logKey, "The interstitial ad wasn't ready yet.")
                return
            }

            val maxAdListener: MaxAdListener = object : MaxAdListener {
                override fun onAdLoaded(ad: MaxAd) {
                    Misc.intFailedCount = 0
                    Log.d(logKey, "Interstitial Ad loaded.")
                }

                override fun onAdDisplayed(ad: MaxAd) {
                    Log.d(logKey, "Ad showed fullscreen content.")
                    mInterstitialAdApplovin = null
                    loadApplovinInterstitial(activity, null)
                }

                override fun onAdHidden(ad: MaxAd) {
                    callBack?.onDismiss()
                }

                override fun onAdClicked(ad: MaxAd) {}

                override fun onAdLoadFailed(adUnitId: String, error: MaxError) {
                    Log.d(logKey, error.message)
                    Misc.intFailedCount++
                    callBack?.onDismiss()
                    mInterstitialAdApplovin = null
                }

                override fun onAdDisplayFailed(ad: MaxAd, error: MaxError) {
                    callBack?.onDismiss()
                    if (Misc.intFailedCount < 3) loadApplovinInterstitial(activity, null)
                }
            }
            mInterstitialAdApplovin?.setListener(maxAdListener)

        }

        fun showInterstitial(
            activity: Activity,
            isEnabled: String,
            callBack: InterstitialCallBack?
        ) {
            if (isEnabled.contains("am_al")) {
                if (AdMobInterstitial.interAdmob != null || AdMobInterstitial.interAdmobOne != null || AdMobInterstitial.interAdmobTwo != null || AdMobInterstitial.interAdmobThree != null || AdMobInterstitial.interAdmobFour != null) {
                    AdMobInterstitial.show(activity, "am", callBack)
                } else {
                    showApplovinInterstitial(activity, callBack)
                }
            } else if (isEnabled.contains("al")) {
                showApplovinInterstitial(activity, callBack)
            } else if (isEnabled.contains("am")) {
                AdMobInterstitial.show(activity, "am", callBack)
            } else {
                callBack?.onDismiss()
            }
        }

        private fun showApplovinNative(
            activity: Activity, adFrameLayout: FrameLayout, callBack: NativeAdCallBack?
        ) {
            if (mNativeAd != null) {
                callBack?.onLoad()
                Log.d(logKey, "Native Ad displayed.")
                adFrameLayout.removeAllViews()
                adFrameLayout.addView(nativeAdView)
                adFrameLayout.visibility = View.VISIBLE
            } else {
                adFrameLayout.visibility = View.GONE
            }
            loadApplovinNativeAd(activity, null)
        }

        fun showNativeAd(
            activity: Activity,
            adFrameLayout: FrameLayout,
            isEnabled: String,
            callBack: NativeAdCallBack?
        ) {
            try {
                if (!Misc.getPurchasedStatus(activity)) if (isEnabled.contains("am_al")) {
                    if (mNativeAdAdMobOne != null || mNativeAdAdMobTwo != null) {
                        showNativeAdAdMob(activity, adFrameLayout, isEnabled, callBack)
                    } else {
                        showApplovinNative(activity, adFrameLayout, callBack)
                    }
                } else if (isEnabled.contains("am") || mNativeAdAdMobTwo != null) {
                    showNativeAdAdMob(activity, adFrameLayout, isEnabled, callBack)
                } else if (isEnabled.contains("al")) {
                    showApplovinNative(activity, adFrameLayout, callBack)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fun loadApplovinNativeAd(
            activity: Activity,
            callBack: LoadInterstitialCallBack?,
            isLargeBtnNative: Boolean = false
        ) {
            try {
                if (!Misc.getPurchasedStatus(activity) && Misc.nativeAdIdApplovin != "" && Misc.checkInternetConnection(
                        activity
                    )
                ) {
                    nativeAdLoader = MaxNativeAdLoader(Misc.nativeAdIdApplovin, activity)
                    nativeAdLoader.setNativeAdListener(object : MaxNativeAdListener() {

                        override fun onNativeAdLoaded(p0: MaxNativeAdView?, ad: MaxAd?) {
                            mNativeAd = ad
                            nativeAdView = p0
                            nativeFailedCount = 0
                            callBack?.onLoaded()
                        }

                        override fun onNativeAdLoadFailed(adUnitId: String, error: MaxError) {
                            Log.d(logKey, "Applovin Native ad load failed.")
                            nativeFailedCount++
                            callBack?.onFailed()
                            if (nativeFailedCount < 3) {
                                loadApplovinNativeAd(activity, null)
                            }
                        }

                        override fun onNativeAdClicked(ad: MaxAd) {
                            Log.d(logKey, "Applovin native ad clicked.")
                        }
                    })

                    if (isLargeBtnNative) nativeAdLoader.loadAd(createApplovinNativeAdView(activity))
                    else nativeAdLoader.loadAd(createApplovinSmallNativeAdView(activity))

                } else {
                    Log.d(logKey, "Native ad Id = null")
                }
            } catch (e: Exception) {
                Log.d(logKey, "Exception")
                e.printStackTrace()
            }
        }

        private fun createApplovinNativeAdView(activity: Activity): MaxNativeAdView {
            val binder: MaxNativeAdViewBinder =
                MaxNativeAdViewBinder.Builder(R.layout.applovin_native)
                    .setTitleTextViewId(R.id.title_text_view).setBodyTextViewId(R.id.body_text_view)
                    .setAdvertiserTextViewId(R.id.advertiser_textView)
                    .setIconImageViewId(R.id.icon_image_view)
                    .setMediaContentViewGroupId(R.id.media_view_container)
                    .setOptionsContentViewGroupId(R.id.options_view)
                    .setCallToActionButtonId(R.id.cta_button).build()
            return MaxNativeAdView(binder, activity)
        }

        private fun createApplovinSmallNativeAdView(activity: Activity): MaxNativeAdView {
            val binder: MaxNativeAdViewBinder =
                MaxNativeAdViewBinder.Builder(R.layout.applovin_native_small_btn)
                    .setTitleTextViewId(R.id.title_text_view).setBodyTextViewId(R.id.body_text_view)
                    .setAdvertiserTextViewId(R.id.advertiser_textView)
                    .setIconImageViewId(R.id.icon_image_view)
                    .setMediaContentViewGroupId(R.id.media_view_container)
                    .setOptionsContentViewGroupId(R.id.options_view)
                    .setCallToActionButtonId(R.id.cta_button).build()
            return MaxNativeAdView(binder, activity)
        }

        fun showMREC(activity: Activity, marcAdContainer: FrameLayout, isEnabled: Boolean) {
            if (Misc.mRecAdId != "") if (isEnabled) {
                val adViewMRec = MaxAdView(Misc.mRecAdId, MaxAdFormat.MREC, activity)
                val maxAdViewAdListener: MaxAdViewAdListener = object : MaxAdViewAdListener {
                    override fun onAdExpanded(ad: MaxAd) {}
                    override fun onAdCollapsed(ad: MaxAd) {}
                    override fun onAdLoaded(ad: MaxAd) {
                        Log.e("Add", "LOADED")
                        marcAdContainer.visibility = View.VISIBLE
                        //marcAdContainer.addView(adViewMarcs);
                    }

                    override fun onAdDisplayed(ad: MaxAd) {
                        Log.e("Add", "DISPLAYED")
                    }

                    override fun onAdHidden(ad: MaxAd) {}
                    override fun onAdClicked(ad: MaxAd) {}
                    override fun onAdLoadFailed(adUnitId: String, error: MaxError) {
                        Log.e("Add", "FAILED TO LOADED")
                    }

                    override fun onAdDisplayFailed(ad: MaxAd, error: MaxError) {
                        Log.e("Add", "FAILED TO DISPLAY")
                    }
                }
                val maxAdRevenueListener = MaxAdRevenueListener { ad: MaxAd? -> }
                val widthPx = AppLovinSdkUtils.dpToPx(activity, 300)
                val heightPx = AppLovinSdkUtils.dpToPx(activity, 250)
                adViewMRec.layoutParams = FrameLayout.LayoutParams(widthPx, heightPx)
                marcAdContainer.addView(adViewMRec)
                adViewMRec.setListener(maxAdViewAdListener)
                adViewMRec.setRevenueListener(maxAdRevenueListener)
                adViewMRec.loadAd()
                adViewMRec.startAutoRefresh()
            }
        }

        fun loadBannerAd(activity: Activity) {
            if (Misc.getPurchasedStatus(activity)) {
                return
            }
            try {
                if (Misc.checkInternetConnection(activity) && Misc.bannerAdId != "") {
                    adView = MaxAdView(Misc.bannerAdId, activity)

                    adView.setListener(object : MaxAdViewAdListener {
                        override fun onAdLoaded(maxAd: MaxAd) {
                            isBannerAdLoaded = true
                        }

                        override fun onAdLoadFailed(adUnitId: String?, error: MaxError?) {
                            isBannerAdLoaded = false
                            Log.d(logKey, "Banner ad load failed, ${error?.message}")
                        }

                        override fun onAdDisplayFailed(ad: MaxAd?, error: MaxError?) {
                            isBannerAdLoaded = false
                            Log.d(logKey, "Banner ad load failed, ${error?.message}")
                        }

                        override fun onAdClicked(maxAd: MaxAd) {}

                        override fun onAdExpanded(maxAd: MaxAd) {}

                        override fun onAdCollapsed(maxAd: MaxAd) {}

                        override fun onAdDisplayed(maxAd: MaxAd) { /* DO NOT USE - THIS IS RESERVED FOR FULLSCREEN ADS ONLY AND WILL BE REMOVED IN A FUTURE SDK RELEASE */
                        }

                        override fun onAdHidden(maxAd: MaxAd) { /* DO NOT USE - THIS IS RESERVED FOR FULLSCREEN ADS ONLY AND WILL BE REMOVED IN A FUTURE SDK RELEASE */
                            Log.d(logKey, "Banner ad load is hidden.")
                        }

                    })

                    val width = ViewGroup.LayoutParams.MATCH_PARENT
                    val heightPx: Int =
                        activity.resources.getDimensionPixelSize(R.dimen.banner_ad_height)

                    adView.layoutParams = FrameLayout.LayoutParams(width, heightPx)
                    adView.loadAd()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fun showBannerAd(isEnabled: Boolean, rootView: FrameLayout) {
            if (this::adView.isInitialized) if (isBannerAdLoaded) if (isEnabled) {
                rootView.removeAllViews()
                if (adView.parent != null) {
                    (adView.parent as ViewGroup).removeView(adView)
                }
                rootView.addView(adView)
                rootView.visibility = View.VISIBLE
            }
        }

        private fun showNativeAdAdMob(
            activity: Activity,
            amLayout: FrameLayout,
            isEnabled: String,
            callBack: NativeAdCallBack?
        ) {
            if (isEnabled.contains("small")) {
                AdMobNative.showSmallNativeAd(activity, amLayout, callBack)
            } else {
                AdMobNative.showNativeAd(activity, amLayout, callBack)
            }
        }


        fun loadAdMobNativeAd(
            activity: Activity, callBack: LoadInterstitialCallBack?
        ) {
            if (mNativeAdAdMobOne == null) {
                AdMobNative.loadNativeOne(activity, callBack)
            }

            if (mNativeAdAdMobTwo == null) {
                AdMobNative.loadNativeTwo(activity, callBack)
            }
        }

        fun loadAdMobInterstitial(activity: Activity) {
            AdMobInterstitial.manageLoadInterAdmob(activity)
        }
    }
}