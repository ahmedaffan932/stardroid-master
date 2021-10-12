package com.google.android.stardroid

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.stardroid.activities.DynamicStarMapActivity
import com.google.android.stardroid.clasess.Misc
import com.google.android.stardroid.interfaces.StartActivityCallBack
import kotlinx.android.synthetic.fdroid.activity_sky_map.*

class SkyMapActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sky_map)

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
}