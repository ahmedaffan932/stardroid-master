package com.gpsnavigation.customnotification.services

import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.gpsnavigation.customnotification.BuildConfig

open class FcmFireBaseID : FirebaseMessagingService() {

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        Log.e("TAG", "onNewToken: $p0")
        if (BuildConfig.DEBUG)
            Log.d("Refreshed token: %s", p0)
        subscribeToTopic()
    }

    companion object {
        private const val RELEASE_TOPIC =
            "LEMRelease"
        private const val DEBUG_TOPIC =
            "LEMDebug"

        fun subscribeToTopic(): Boolean {
            try {
                if (FirebaseInstanceId.getInstance().id.isNotEmpty()) {
                    if (BuildConfig.DEBUG)
                        FirebaseMessaging.getInstance().subscribeToTopic(DEBUG_TOPIC)
                    else {
                        FirebaseMessaging.getInstance().subscribeToTopic(RELEASE_TOPIC)
                        FirebaseMessaging.getInstance().unsubscribeFromTopic(DEBUG_TOPIC)
                    }
                    return true
                }
            } catch (e: Exception) {
                Log.e("FirebaseMessaging", e.toString())
            }
            return false
        }
    }
}
