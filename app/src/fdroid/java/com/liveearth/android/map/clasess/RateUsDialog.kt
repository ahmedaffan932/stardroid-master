package com.liveearth.android.map.clasess

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.Window
import com.liveearth.android.map.R

class RateUsDialog
    (var c: Activity) : Dialog(c), View.OnClickListener {
    var d: Dialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.rate_us_dialog_box)
    }

    override fun onClick(v: View) {
    }
}