package com.liveearth.android.map

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.liveearth.android.map.activities.DynamicStarMapActivity
import com.liveearth.android.map.clasess.Misc
import com.liveearth.android.map.interfaces.OnBackPressCallBack
import com.liveearth.android.map.interfaces.StartActivityCallBack
import kotlinx.android.synthetic.fdroid.activity_sky_map.*
import kotlinx.android.synthetic.fdroid.activity_sky_map.bannerAdFrameLayout
import kotlinx.android.synthetic.main.activity_live_earth.*

class SkyMapActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sky_map)

        Toast.makeText(this, "Thanks to Stardroid Google.",Toast.LENGTH_SHORT).show()

        Misc.loadBannerAd(this, Misc.isSkyMapBannerEnabled, Misc.bannerAdId, bannerAdFrameLayout)

        clSearchSkyMap.setOnClickListener {
           searchPlanet("search")
        }

        btnBackSkyMap.setOnClickListener {
            onBackPressed()
        }

        clMoon.setOnClickListener {
            searchPlanet(getString(R.string.moon))
        }
        clSun.setOnClickListener {
            searchPlanet(getString(R.string.sun))
        }
        clMars.setOnClickListener {
            searchPlanet(getString(R.string.mars))
        }
        clVenus.setOnClickListener {
            searchPlanet(getString(R.string.venus))
        }
        clMercury.setOnClickListener {
            searchPlanet(getString(R.string.mercury))
        }
        clSaturn.setOnClickListener {
            searchPlanet(getString(R.string.saturn))
        }
        clUranus.setOnClickListener {
            searchPlanet(getString(R.string.uranus))
        }
        clJupiter.setOnClickListener {
            searchPlanet(getString(R.string.jupiter))
        }
        clNeptune.setOnClickListener {
            searchPlanet(getString(R.string.neptune))
        }
        clPluto.setOnClickListener {
            searchPlanet(getString(R.string.pluto))
        }
    }

    private fun searchPlanet(str: String){
        val intent = Intent(this, DynamicStarMapActivity::class.java)
        intent.putExtra(Misc.data, str)
        Misc.startActivity(this, Misc.isSkyMapIntEnabled, object : StartActivityCallBack {
            override fun onStart() {
                startActivity(intent)
            }
        })
    }

    override fun onBackPressed() {
        Misc.onBackPress(this, Misc.isSkyMapBackIntEnabled, object : OnBackPressCallBack {
            override fun onBackPress() {
                finish()
            }
        })
    }
}