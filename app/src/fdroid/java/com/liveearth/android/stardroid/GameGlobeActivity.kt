package com.google.android.stardroid

import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.liveearth.android.stardroid.R
import gov.nasa.worldwind.Frame
import gov.nasa.worldwind.WorldWindow
import gov.nasa.worldwind.globe.BasicElevationCoverage
import gov.nasa.worldwind.layer.BackgroundLayer
import gov.nasa.worldwind.layer.BlueMarbleLandsatLayer


class GameGlobeActivity : AppCompatActivity() {

    private var wwd: WorldWindow? = null

    fun BasicGlobeFragment() {}

    /**
     * Creates a new WorldWindow (GLSurfaceView) object.
     */
    private fun createWorldWindow(): WorldWindow? {
        // Create the WorldWindow (a GLSurfaceView) which displays the globe.
        wwd = WorldWindow(this)
        // Setup the WorldWindow's layers.
        wwd!!.layers.addLayer(BackgroundLayer())
        wwd!!.layers.addLayer(BlueMarbleLandsatLayer())
        // Setup the WorldWindow's elevation coverages.
        wwd!!.globe.elevationModel.addCoverage(BasicElevationCoverage())
        return wwd
    }

    /**
     * Gets the WorldWindow (GLSurfaceView) object.
     */
    fun getWorldWindow(): WorldWindow? {
        return wwd
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_globe)

        findViewById<FrameLayout>(R.id.globe).addView(this.createWorldWindow());
    }

    override fun onResume() {
        super.onResume()
        wwd!!.onResume() // resumes a paused rendering thread
    }

    /**
     * Pauses the WorldWindow's rendering thread
     */
    override fun onPause() {
        super.onPause()
        wwd!!.onPause() // pauses the rendering thread
    }
}