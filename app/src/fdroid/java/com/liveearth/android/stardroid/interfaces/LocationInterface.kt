package com.liveearth.android.stardroid.interfaces

import android.location.Location

interface LocationInterface {
    fun onLocationChanged(location: Location)
}