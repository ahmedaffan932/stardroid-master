package com.liveearth.android.map.interfaces

import android.location.Location

interface LocationInterface {
    fun onLocation(location: Location)
}