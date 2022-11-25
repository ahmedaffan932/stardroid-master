package com.liveearth.android.map.clasess

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.LinearLayout.LayoutParams
import android.widget.TextView
import com.applovin.mediation.*
import com.applovin.mediation.ads.MaxAdView
import com.applovin.mediation.ads.MaxInterstitialAd
import com.applovin.mediation.nativeAds.MaxNativeAdListener
import com.applovin.mediation.nativeAds.MaxNativeAdLoader
import com.applovin.mediation.nativeAds.MaxNativeAdView
import com.applovin.mediation.nativeAds.MaxNativeAdViewBinder
import com.applovin.sdk.AppLovinSdkUtils
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.liveearth.android.map.PremiumScreenActivity
import com.liveearth.android.map.R
import com.liveearth.android.map.clasess.Misc.Companion.logKey
import com.liveearth.android.map.clasess.Misc.Companion.nativeFailedCount
import com.liveearth.android.map.databinding.AdmobNativeBinding
import com.liveearth.android.map.databinding.RemoveAdsOnNativeAdBinding
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
        var mInterstitialAdAdMob: InterstitialAd? = null
        var mNativeAdAdMob: NativeAd? = null

        @SuppressLint("StaticFieldLeak")
        var nativeAdView: MaxNativeAdView? = null
        var mNativeAd: MaxAd? = null
        var mInterstitialAdApplovin: MaxInterstitialAd? = null
        lateinit var nativeAdLoader: MaxNativeAdLoader

        fun loadApplovinInterstitial(activity: Activity, callback: LoadInterstitialCallBack?) {
            if (!isApplovinInterstitialAdRequired()) {
                callback?.onFailed()
                return
            }
            if (Misc.frequencyCappingApplovinCount < Misc.frequencyCappingApplovinLimit) {
                if (!Misc.getPurchasedStatus(activity) && Misc.interstitialAdIdApplovin != "" && Misc.checkInternetConnection(
                        activity
                    )
                ) {
                    try {
                        mInterstitialAdApplovin =
                            MaxInterstitialAd(Misc.interstitialAdIdApplovin, activity)
                        mInterstitialAdApplovin?.loadAd()
                        Log.d(
                            logKey,
                            "frequencyCappingApplovinCount ${Misc.frequencyCappingApplovinCount}"
                        )
                        callback?.onLoaded()
                    } catch (e: Exception) {
                        callback?.onFailed()
                        e.printStackTrace()
                    }
                }
            } else {
                callback?.onFailed()
            }
        }

        private fun showApplovinInterstitial(activity: Activity, callBack: InterstitialCallBack?) {
            if (Misc.adBreakCount < Misc.adBreakLimit) {
                Misc.adBreakCount++
                callBack?.onDismiss()
                return
            }
            Misc.adBreakCount = 0

            if (mInterstitialAdApplovin != null) {
                Log.d(logKey, "Filled")
                if (mInterstitialAdApplovin?.isReady == true) {
                    Log.d(logKey, "Ready")
                    mInterstitialAdApplovin?.showAd()
                    Misc.frequencyCappingApplovinCount++
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
                    val clipboard: ClipboardManager =
                        activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip =
                        ClipData.newPlainText("Camera Translator", error.message)
                    clipboard.setPrimaryClip(clip)
                    Log.e(logKey, error.message)
                    Misc.intFailedCount++
                    callBack?.onDismiss()
                    mInterstitialAdApplovin = null
                }

                override fun onAdDisplayFailed(ad: MaxAd, error: MaxError) {
                    callBack?.onDismiss()
                    if (Misc.intFailedCount < 3)
                        loadApplovinInterstitial(activity, null)
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
                if (mInterstitialAdAdMob != null) {
                    showAdMobInterstitial(activity, callBack)
                } else {
                    showApplovinInterstitial(activity, callBack)
                }
            } else if (isEnabled.contains("al")) {
                showApplovinInterstitial(activity, callBack)
            } else if (isEnabled.contains("am")) {
                showAdMobInterstitial(activity, callBack)
            } else {
                callBack?.onDismiss()
            }
        }

        private fun showApplovinNative(
            activity: Activity,
            adFrameLayout: FrameLayout,
            callBack: NativeAdCallBack?
        ) {
            if (mNativeAd != null) {
                callBack?.onLoad()
                Log.d(logKey, "Native Ad displayed.")
                adFrameLayout.removeAllViews()
                adFrameLayout.addView(nativeAdView)
                adFrameLayout.visibility = View.VISIBLE
                if (isApplovinNativeRequired())
                    loadApplovinNativeAd(activity, null)
            } else {
                if (isApplovinNativeRequired())
                    loadApplovinNativeAd(activity, null)
                adFrameLayout.visibility = View.GONE
            }
        }

        fun showNativeAd(
            activity: Activity,
            adFrameLayout: FrameLayout,
            isEnabled: String,
            callBack: NativeAdCallBack?
        ) {
            try {
                if (!Misc.getPurchasedStatus(activity))
                    if (isEnabled.contains("am_al")) {
                        if (mNativeAdAdMob != null) {
                            showNativeAdAdMob(activity, adFrameLayout, callBack)
                        } else {
                            showApplovinNative(activity, adFrameLayout, callBack)
                        }
                    } else if (isEnabled.contains("am")) {
                        showNativeAdAdMob(activity, adFrameLayout, callBack)
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
                            val clipboard: ClipboardManager =
                                activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip =
                                ClipData.newPlainText("Camera Translator", error.message)
                            clipboard.setPrimaryClip(clip)
                            nativeFailedCount++
                            callBack?.onFailed()
                            if (nativeFailedCount < 3) {
                                if (isApplovinNativeRequired())
                                    loadApplovinNativeAd(activity, null)
                            }
                        }

                        override fun onNativeAdClicked(ad: MaxAd) {
                            // Optional click callback
                            Log.d(logKey, "Applovin native ad clicked.")
                        }
                    })

                    if (isLargeBtnNative)
                        nativeAdLoader.loadAd(createApplovinNativeAdView(activity))
                    else
                        nativeAdLoader.loadAd(createApplovinSmallNativeAdView(activity))

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
                    .setTitleTextViewId(R.id.title_text_view)
                    .setBodyTextViewId(R.id.body_text_view)
                    .setAdvertiserTextViewId(R.id.advertiser_textView)
                    .setIconImageViewId(R.id.icon_image_view)
                    .setMediaContentViewGroupId(R.id.media_view_container)
                    .setOptionsContentViewGroupId(R.id.options_view)
                    .setCallToActionButtonId(R.id.cta_button)
                    .build()
            return MaxNativeAdView(binder, activity)
        }

        private fun createApplovinSmallNativeAdView(activity: Activity): MaxNativeAdView {
            val binder: MaxNativeAdViewBinder =
                MaxNativeAdViewBinder.Builder(R.layout.applovin_native_small_btn)
                    .setTitleTextViewId(R.id.title_text_view)
                    .setBodyTextViewId(R.id.body_text_view)
                    .setAdvertiserTextViewId(R.id.advertiser_textView)
                    .setIconImageViewId(R.id.icon_image_view)
                    .setMediaContentViewGroupId(R.id.media_view_container)
                    .setOptionsContentViewGroupId(R.id.options_view)
                    .setCallToActionButtonId(R.id.cta_button)
                    .build()
            return MaxNativeAdView(binder, activity)
        }

        fun showMREC(activity: Activity, marcAdContainer: FrameLayout, isEnabled: Boolean) {
            if (Misc.mRecAdId != "")
                if (isEnabled) {
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
            if (this::adView.isInitialized)
                if (isBannerAdLoaded)
                    if (isEnabled) {
                        rootView.removeAllViews()
                        if (adView.parent != null) {
                            (adView.parent as ViewGroup).removeView(adView)
                        }
                        rootView.addView(adView)
                        rootView.visibility = View.VISIBLE
                    }
        }

        private fun showAdMobInterstitial(activity: Activity, callBack: InterstitialCallBack?) {
            if (Misc.getPurchasedStatus(activity)) {
                callBack?.onDismiss()
                return
            }
            if (Misc.adBreakCount < Misc.adBreakLimit) {
                Misc.adBreakCount++
                callBack?.onDismiss()
                return
            }
            Misc.adBreakCount = 0

            if (mInterstitialAdAdMob != null) {
                mInterstitialAdAdMob?.show(activity)

            } else {
                callBack?.onDismiss()
                Log.d("TAG", "The interstitial ad wasn't ready yet.")
                return
            }

            mInterstitialAdAdMob?.fullScreenContentCallback =
                object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        callBack?.onDismiss()
                        loadAdMobInterstitial(activity, null)
                    }

                    override fun onAdFailedToShowFullScreenContent(adError: AdError?) {
                        callBack?.onDismiss()
                        loadAdMobInterstitial(activity, null)
                    }

                    override fun onAdShowedFullScreenContent() {
                        Log.d(logKey, "Ad showed fullscreen content.")
                        mInterstitialAdAdMob = null
                    }
                }

            if (!Misc.getPurchasedStatus(activity)) {
                if (mInterstitialAdAdMob == null) {
                    callBack?.onDismiss()
                }
            }
        }

        private fun showNativeAdAdMob(
            context: Context,
            amLayout: FrameLayout,
            callBack: NativeAdCallBack?
        ) {
            if (mNativeAdAdMob != null) {
//                val inflater =
//                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

                val clNativeAd = AdmobNativeBinding.inflate(LayoutInflater.from(context))

                val nativeAdView =  clNativeAd.nativeAd
//                    inflater.inflate(R.layout.admob_native, null) as NativeAdView

                amLayout.removeAllViews()
                clNativeAd.clRemoveAd.setOnClickListener {
                    val intent = Intent(context, PremiumScreenActivity::class.java)
                    intent.putExtra(Misc.data, Misc.data)
                    context.startActivity(intent)
                }
                amLayout.addView(clNativeAd.root)
                amLayout.setBackgroundResource(R.drawable.bg_native_ad)

                nativeAdView.mediaView = nativeAdView.findViewById(R.id.ad_media)
                nativeAdView.headlineView = nativeAdView.findViewById(R.id.ad_headline)
                nativeAdView.bodyView = nativeAdView.findViewById(R.id.ad_body)
                nativeAdView.callToActionView = nativeAdView.findViewById(R.id.ad_call_to_action)
                nativeAdView.iconView = nativeAdView.findViewById(R.id.ad_app_icon)

                (nativeAdView.headlineView as TextView).text = mNativeAdAdMob?.headline
                mNativeAdAdMob?.mediaContent?.let { nativeAdView.mediaView?.setMediaContent(it) }

                if (mNativeAdAdMob?.body == null) {
                    nativeAdView.bodyView?.visibility = View.INVISIBLE
                } else {
                    nativeAdView.bodyView?.visibility = View.VISIBLE
                    (nativeAdView.bodyView as TextView).text = mNativeAdAdMob?.body
                }

                if (mNativeAdAdMob?.callToAction == null) {
                    nativeAdView.callToActionView?.visibility = View.INVISIBLE
                } else {
                    nativeAdView.callToActionView?.visibility = View.VISIBLE
                    (nativeAdView.callToActionView as Button).text = mNativeAdAdMob?.callToAction
                }

                if (mNativeAdAdMob?.icon == null) {
                    nativeAdView.iconView?.visibility = View.GONE
                } else {
                    (nativeAdView.iconView as ImageView).setImageDrawable(
                        mNativeAdAdMob!!.icon?.drawable
                    )
                    nativeAdView.iconView?.visibility = View.VISIBLE
                }

                nativeAdView.setNativeAd(mNativeAdAdMob!!)
                amLayout.visibility = View.VISIBLE
                callBack?.onLoad()
            } else {
                amLayout.visibility = View.GONE
            }
        }

        fun loadAdMobNativeAd(
            activity: Activity,
            callBack: LoadInterstitialCallBack?
        ) {
            if (!isApplovinNativeRequired()) {
                return
            }
            mNativeAdAdMob = null
            if (Misc.getPurchasedStatus(activity))
                return
            val adLoader: AdLoader =
                AdLoader.Builder(activity, Misc.nativeAdIdAdMob)
                    .forNativeAd { nativeAd ->
                        Log.d(logKey, "Native Ad Loaded")
                        nativeFailedCount = 0
                        mNativeAdAdMob = nativeAd
                        if (activity.isDestroyed) {
                            nativeAd.destroy()
                        }
                        callBack?.onLoaded()

                    }.withAdListener(object : AdListener() {
                        override fun onAdFailedToLoad(adError: LoadAdError) {
                            val clipboard: ClipboardManager =
                                activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip =
                                ClipData.newPlainText("Camera Translator", adError.message)
                            clipboard.setPrimaryClip(clip)
                            nativeFailedCount++
                            callBack?.onFailed()
                            Log.e(logKey, adError.message)
                        }
                    })
                    .build()
            adLoader.loadAd(AdRequest.Builder().build())
        }

        fun loadAdMobInterstitial(
            activity: Activity,
            callback: LoadInterstitialCallBack?
        ) {
            if (isAdMobInterstitialAdRequired())
                if (Misc.frequencyCappingAdMobCount < Misc.frequencyCappingAdMobLimit) {
                    if (!Misc.getPurchasedStatus(activity) && Misc.intFailedCount < 3 && Misc.checkInternetConnection(
                            activity
                        )
                    ) {
                        val adRequest = AdRequest.Builder().build()
                        InterstitialAd.load(
                            activity,
                            Misc.interstitialAdIdAdMob,
                            adRequest,
                            object : InterstitialAdLoadCallback() {
                                override fun onAdFailedToLoad(adError: LoadAdError) {
                                    Log.d(logKey, adError.message)
                                    val clipboard: ClipboardManager =
                                        activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip =
                                        ClipData.newPlainText("Camera Translator", adError.message)
                                    clipboard.setPrimaryClip(clip)
                                    Log.e(logKey, "Interstitial ad load failed.")
                                    Misc.intFailedCount++
                                    mInterstitialAdAdMob = null
                                    callback?.onFailed()
                                }

                                override fun onAdLoaded(p0: InterstitialAd) {
                                    mInterstitialAdAdMob = p0
                                    Misc.intFailedCount = 0
                                    Log.d(logKey, "Interstitial Ad loaded.")
                                    callback?.onLoaded()
                                }
                            }
                        )
                        Misc.frequencyCappingAdMobCount++
                        Log.d(
                            logKey,
                            "frequencyCappingAdMobCount ${Misc.frequencyCappingAdMobCount}"
                        )
                    } else {
                        callback?.onFailed()
                    }
                } else {
                    callback?.onFailed()
                }
        }

        private fun isApplovinInterstitialAdRequired(): Boolean {
            return Misc.isQuitIntAm_Al.contains("al") || Misc.isSkyMapIntAm_Al.contains("al") || Misc.startGameIntAm_Al.contains(
                "al"
            ) || Misc.generateQRIntAm_Al.contains("al") || Misc.skyMapBackIntAm_Al.contains(
                "al"
            ) || Misc.settingBackIntAm_Al.contains("al") || Misc.compassBackIntAm_Al.contains(
                "al"
            ) || Misc.altitudeBackIntAm_Al.contains("al") || Misc.quizCompleteIntAm_Al.contains(
                "al"
            ) || Misc.viewWorldBackIntAm_Al.contains("al") || Misc.quizCountriesIntAm_Al.contains(
                "al"
            ) || Misc.noteCamOnBackIntAm_Al.contains("al") || Misc.soundMeterBackIntAm_Al.contains(
                "al"
            ) || Misc.quizSelectModeIntAm_Al.contains("al") || Misc.worldQuizOnBackIntAm_Al.contains(
                "al"
            ) || Misc.continentSelectIntAm_Al.contains("al") || Misc.liveEarthOnBackIntAm_Al.contains(
                "al"
            ) || Misc.generateQrOnBackIntAm_Al.contains("al") || Misc.quizCompleteBackIntAm_Al.contains(
                "al"
            ) || Misc.mainFromProScreenIntAm_Al.contains("al") || Misc.quizScreenOneBackIntAm_Al.contains(
                "al"
            ) || Misc.continentSelectBackIntAm_Al.contains("al") || Misc.lsvIntAm_al.contains("al") || Misc.worldQuizIntAm_al.contains(
                "al"
            ) || Misc.skyMapIntAm_al.contains("al") || Misc.gpsCamIntAm_al.contains("al") || Misc.noteCamIntAm_al.contains(
                "al"
            ) || Misc.speedometerIntAm_al.contains("al") || Misc.compassIntAm_al.contains("al") || Misc.soundMeterIntAm_al.contains(
                "al"
            ) || Misc.altitudeIntAm_al.contains("al") || Misc.isSplashIntAm_al.contains("al")
        }

        private fun isAdMobInterstitialAdRequired(): Boolean {
            return Misc.isQuitIntAm_Al.contains("am") || Misc.isSkyMapIntAm_Al.contains("am") || Misc.startGameIntAm_Al.contains(
                "am"
            ) || Misc.generateQRIntAm_Al.contains("am") || Misc.skyMapBackIntAm_Al.contains(
                "am"
            ) || Misc.settingBackIntAm_Al.contains("am") || Misc.compassBackIntAm_Al.contains(
                "am"
            ) || Misc.altitudeBackIntAm_Al.contains("am") || Misc.quizCompleteIntAm_Al.contains(
                "am"
            ) || Misc.viewWorldBackIntAm_Al.contains("am") || Misc.quizCountriesIntAm_Al.contains(
                "am"
            ) || Misc.noteCamOnBackIntAm_Al.contains("am") || Misc.soundMeterBackIntAm_Al.contains(
                "am"
            ) || Misc.quizSelectModeIntAm_Al.contains("am") || Misc.worldQuizOnBackIntAm_Al.contains(
                "am"
            ) || Misc.continentSelectIntAm_Al.contains("am") || Misc.liveEarthOnBackIntAm_Al.contains(
                "am"
            ) || Misc.generateQrOnBackIntAm_Al.contains("am") || Misc.quizCompleteBackIntAm_Al.contains(
                "am"
            ) || Misc.mainFromProScreenIntAm_Al.contains("am") || Misc.quizScreenOneBackIntAm_Al.contains(
                "am"
            ) || Misc.continentSelectBackIntAm_Al.contains("am") || Misc.lsvIntAm_al.contains("am") || Misc.worldQuizIntAm_al.contains(
                "am"
            ) || Misc.skyMapIntAm_al.contains("am") || Misc.gpsCamIntAm_al.contains("am") || Misc.noteCamIntAm_al.contains(
                "am"
            ) || Misc.speedometerIntAm_al.contains("am") || Misc.compassIntAm_al.contains("am") || Misc.soundMeterIntAm_al.contains(
                "am"
            ) || Misc.altitudeIntAm_al.contains("am") || Misc.isSplashIntAm_al.contains("am")
        }

        private fun isAdMobNativeRequired(): Boolean {
            return Misc.quitNativeAm_Al.contains("am") ||
                    Misc.dashboardNativeAm_Al.contains("am") ||
                    Misc.createQRNativeAm_Al.contains("am") ||
                    Misc.soundMeterNativeAm_Al.contains("am") ||
                    Misc.speedoMeterNativeAm_Al.contains("am") ||
                    Misc.quizCompleteNativeAm_Al.contains("am") ||
                    Misc.quizScreenOneNativeAm_Al.contains("am") ||
                    Misc.quizSelectModeNativeAm_Al.contains("am") ||
                    Misc.continentSelectNativeAm_Al.contains("am") ||
                    Misc.worldQuizActivityNativeAm_Al.contains("am")
        }

        private fun isApplovinNativeRequired(): Boolean {
            return Misc.quitNativeAm_Al.contains("al") ||
                    Misc.dashboardNativeAm_Al.contains("al") ||
                    Misc.createQRNativeAm_Al.contains("al") ||
                    Misc.soundMeterNativeAm_Al.contains("al") ||
                    Misc.speedoMeterNativeAm_Al.contains("al") ||
                    Misc.quizCompleteNativeAm_Al.contains("al") ||
                    Misc.quizScreenOneNativeAm_Al.contains("al") ||
                    Misc.quizSelectModeNativeAm_Al.contains("al") ||
                    Misc.continentSelectNativeAm_Al.contains("al") ||
                    Misc.worldQuizActivityNativeAm_Al.contains("al")
        }
    }
}