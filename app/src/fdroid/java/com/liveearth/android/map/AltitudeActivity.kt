package com.liveearth.android.map

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.speech.RecognizerIntent
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.liveearth.android.map.clasess.Misc
import com.liveearth.android.map.interfaces.OnImageSaveCallBack
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.api.geocoding.v5.MapboxGeocoding
import com.mapbox.api.geocoding.v5.models.GeocodingResponse
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.location.LocationComponent
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.layers.Property
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import com.tarek360.instacapture.Instacapture
import com.tarek360.instacapture.listener.SimpleScreenCapturingListener
import kotlinx.android.synthetic.main.activity_altitude.*
import kotlinx.android.synthetic.main.activity_altitude.btnSpeakSearchLocation
import kotlinx.android.synthetic.main.activity_altitude.btnZoomIn
import kotlinx.android.synthetic.main.activity_altitude.btnZoomOut
import kotlinx.android.synthetic.main.activity_altitude.llDefault
import kotlinx.android.synthetic.main.activity_altitude.llHybrid
import kotlinx.android.synthetic.main.activity_altitude.llSatellite
import kotlinx.android.synthetic.main.activity_altitude.llTerrain
import kotlinx.android.synthetic.main.activity_altitude.svLocation
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class AltitudeActivity : AppCompatActivity(), PermissionsListener,
    OnMapReadyCallback, MapboxMap.OnMapClickListener {
    private val REQUEST_CODE_AUTOCOMPLETE = 1
    private lateinit var mapView: MapView
    private lateinit var mapboxMap: MapboxMap
    private lateinit var mapBoxStyle: Style
    private lateinit var hoveringMarker: ImageView
    private lateinit var locationCallback: LocationCallback
    private val point = LatLng()
    private val speechRequestCode = 0
    var isCurrentLocation = true

    companion object {
        private const val DROPPED_MARKER_LAYER_ID = "DROPPED_MARKER_LAYER_ID"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(this, getString(com.liveearth.android.map.R.string.mapbox_access_token))
        setContentView(R.layout.activity_altitude)

        mapView = findViewById(com.liveearth.android.map.R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        btnScreenShot.setOnClickListener {

            Instacapture.capture(this, object : SimpleScreenCapturingListener() {
                override fun onCaptureComplete(bitmap: Bitmap) {

                    Misc.saveImageToExternal(this@AltitudeActivity, bitmap, object : OnImageSaveCallBack {
                        override fun onImageSaved() {
                            Toast.makeText(this@AltitudeActivity, "Screen Shot Saved in Gallery. ", Toast.LENGTH_SHORT).show()
                        }
                    })

                }
            })
        }

        btnZoomIn.setOnClickListener {
            val position = CameraPosition.Builder()
                .target(mapboxMap.cameraPosition.target)
                .zoom(mapboxMap.cameraPosition.zoom + 1)
                .tilt(mapboxMap.cameraPosition.tilt)
                .build()
            mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), 100)
        }

        btnBackAltitude.setOnClickListener {
            onBackPressed()
        }

        btnSpeakSearchLocation.setOnClickListener {
            displaySpeechRecognizer()
        }

        btnZoomOut.setOnClickListener {
            val position = CameraPosition.Builder()
                .target(mapboxMap.cameraPosition.target)
                .zoom(mapboxMap.cameraPosition.zoom - 1)
                .tilt(mapboxMap.cameraPosition.tilt)
                .build()
            mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), 100)
        }

        llDefault.setOnClickListener {
            setMapBoxStyle(Style.OUTDOORS, false)
        }

        llSatellite.setOnClickListener {
            setMapBoxStyle(Style.SATELLITE, false)
        }
        llTerrain.setOnClickListener {
            setMapBoxStyle(Style.SATELLITE_STREETS, false)
        }
        llHybrid.setOnClickListener {
            setMapBoxStyle(Style.DARK, false)
        }
    }

    override fun onExplanationNeeded(permissionsToExplain: MutableList<String>?) {
    }

    override fun onPermissionResult(granted: Boolean) {
        TODO("Not yet implemented")
    }

    override fun onMapReady(mapboxMap: MapboxMap) {
        this.mapboxMap = mapboxMap
        setMapBoxStyle(Style.OUTDOORS, false)
    }

    override fun onMapClick(point: LatLng): Boolean {
//        Log.d(Misc.logKey, point.toString())
        return true
    }


    @SuppressLint("MissingPermission")
    override fun onResume() {
        super.onResume()
        locationCallback = object : LocationCallback() {
            @SuppressLint("SetTextI18n", "LogNotTimber")
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)
                Log.d(Misc.logKey, p0.toString())
                val loc = p0.lastLocation
                if (isCurrentLocation) {
                    tvAltitude.text = loc.altitude.toString()
                    tvLatLngAltitude.text = "Latitude:${loc.latitude}, Longitude: ${loc.longitude}"
                }
            }
        }

        val locationRequest = LocationRequest.create()
        locationRequest.fastestInterval = 1000
        locationRequest.interval = 5000
        LocationServices.getFusedLocationProviderClient(this)
            .requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }

    private fun setMapBoxStyle(styleName: String, isThreeDView: Boolean) {

        mapboxMap.setStyle(
            styleName
        ) { style ->
            mapBoxStyle = style
                enableLocationPlugin(style)
                val manager = getSystemService(LOCATION_SERVICE) as LocationManager
                if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    buildAlertMessageNoGps()
                }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initDroppedMarker(loadedMapStyle: Style) {
        // Add the marker image to map
        loadedMapStyle.addImage(
            "dropped-icon-image",
            resources.getDrawable(com.liveearth.android.map.R.drawable.ic_pin_selected)
        )
        loadedMapStyle.addSource(GeoJsonSource("dropped-marker-source-id"))
        loadedMapStyle.addLayer(
            SymbolLayer(
                com.liveearth.android.map.AltitudeActivity.Companion.DROPPED_MARKER_LAYER_ID,
                "dropped-marker-source-id"
            ).withProperties(
                PropertyFactory.iconImage("dropped-icon-image"),
                PropertyFactory.visibility(Property.NONE),
                PropertyFactory.iconAllowOverlap(true),
                PropertyFactory.iconIgnorePlacement(true)
            )
        )
    }

    private fun buildAlertMessageNoGps() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
            .setCancelable(false)
            .setPositiveButton(
                "Yes"
            ) { dialog, id -> startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)) }
            .setNegativeButton(
                "No"
            ) { dialog, id -> dialog.cancel() }
        val alert: AlertDialog = builder.create()
        alert.show()
    }


    @SuppressLint("SetTextI18n")
    private fun enableLocationPlugin(loadedMapStyle: Style): Location? {
        var locationComponent: LocationComponent? = null
        if (PermissionsManager.areLocationPermissionsGranted(this)) {

            locationComponent = mapboxMap.locationComponent
            locationComponent.activateLocationComponent(
                LocationComponentActivationOptions.builder(
                    this, loadedMapStyle
                ).build()
            )
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
            }
            locationComponent.isLocationComponentEnabled = true

            // Set the component's camera mode
            locationComponent.cameraMode = CameraMode.TRACKING
            locationComponent.renderMode = RenderMode.NORMAL
        }
        return locationComponent!!.lastKnownLocation
    }

    @SuppressLint("SetTextI18n")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == speechRequestCode && resultCode == Activity.RESULT_OK) {
            val spokenText: String? =
                data!!.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).let { results ->
                    results?.get(0)
                }

            if (spokenText != null) {
                val geocodingClient: MapboxGeocoding = MapboxGeocoding.builder()
                    .query(spokenText)
                    .accessToken(getString(com.liveearth.android.map.R.string.mapbox_access_token))
                    .build()

                geocodingClient.enqueueCall(object : Callback<GeocodingResponse> {
                    @SuppressLint("LogNotTimber")
                    override fun onResponse(
                        call: Call<GeocodingResponse>,
                        response: Response<GeocodingResponse>
                    ) {
                        if(response.isSuccessful){
                            val t =  response.body()?.features()?.get(0)?.geometry()
                            Log.d(Misc.logKey, response.body()?.features().toString())
                            val arr = t.toString().split("[", "]")
                            val p = arr[1].split(",")
                            point.latitude = p[1].toDouble()
                            point.longitude = p[0].toDouble()
                            animateCamera(point, 14.0)
                            for(item in arr)
                                Log.d(Misc.logKey,item)
                        }else{
                            Toast.makeText(this@AltitudeActivity, "Place not found.", Toast.LENGTH_SHORT).show()
                        }

                    }

                    override fun onFailure(call: Call<GeocodingResponse>, t: Throwable) {
                        Toast.makeText(this@AltitudeActivity, "Place not found.", Toast.LENGTH_SHORT).show()
                    }

                })
            }
        }

    }



    private fun animateCamera(p: LatLng, zoom: Double) {
        mapboxMap.animateCamera(
            CameraUpdateFactory.newCameraPosition(
                CameraPosition.Builder()
                    .target(
                        LatLng(
                            p.latitude,
                            p.longitude
                        )
                    )
                    .zoom(zoom)
                    .build()
            ), 4000
        )
    }

    private fun displaySpeechRecognizer() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.EXTRA_LANGUAGE)
        }
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE,
            "en"
        )
        startActivityForResult(intent, speechRequestCode)
    }



    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    public override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

}