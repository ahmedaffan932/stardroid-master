package com.google.android.stardroid.interfaces

import android.app.Activity
import android.content.Context
import android.webkit.JavascriptInterface
import android.widget.Toast

class WebAppInterface(private val mContext: Context) {

    /** Show a toast from the web page  */
    @JavascriptInterface
    fun showToast(toast: String): String {
        Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show()
        return toast
    }
}