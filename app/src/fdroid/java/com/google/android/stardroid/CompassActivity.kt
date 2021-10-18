package com.google.android.stardroid

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.blongho.country_data.Country
import com.google.android.stardroid.clasess.Misc
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
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
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions
import edu.arbelkilani.compass.CompassListener
import kotlinx.android.synthetic.main.activity_altitude.*
import kotlinx.android.synthetic.main.activity_compass.*
import kotlinx.android.synthetic.main.activity_compass.btnGetCurrentLocationCompass
import java.io.IOException
import java.util.*


class CompassActivity() : AppCompatActivity(), PermissionsListener, OnMapReadyCallback,
    MapboxMap.OnMapClickListener {
    private val point: LatLng = LatLng()
    private lateinit var mapView: MapView
    private lateinit var mapBoxStyle: Style
    private lateinit var mapboxMap: MapboxMap
    private val REQUEST_CODE_AUTOCOMPLETE = 1
    private lateinit var locationCallback: LocationCallback
    private lateinit var permissionsManager: PermissionsManager
    var isCurrentLocation = true


    companion object {
        private const val DROPPED_MARKER_LAYER_ID = "DROPPED_MARKER_LAYER_ID"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token))
        setContentView(R.layout.activity_compass)

        mapView = findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        btnBackCompass.setOnClickListener {
            onBackPressed()
        }

        val pm = packageManager
        if (!pm.hasSystemFeature(PackageManager.FEATURE_SENSOR_COMPASS)) {
           Toast.makeText(this, "Your device does not support compass.", Toast.LENGTH_SHORT).show()
            onBackPressed()
        }

        compass_1.setListener(object : CompassListener {
            @SuppressLint("LogNotTimber")
            override fun onSensorChanged(event: SensorEvent) {
                Log.d("TAG", "onSensorChanged : $event")
            }

            @SuppressLint("LogNotTimber")
            override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
                Log.d("TAG", "onAccuracyChanged : sensor : $sensor")
                Log.d("TAG", "onAccuracyChanged : accuracy : $accuracy")
            }

        })

        btnGetCurrentLocationCompass.setOnClickListener {
            val manager = getSystemService(LOCATION_SERVICE) as LocationManager
            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                buildAlertMessageNoGps()
            } else {
                isCurrentLocation = true
            }
        }

    }

    override fun onMapReady(mapboxMap: MapboxMap) {
        this.mapboxMap = mapboxMap
        setMapBoxStyle(Style.OUTDOORS)

//        getAddress(this, point)
    }

    @SuppressLint("LogNotTimber")
    override fun onMapClick(point: LatLng): Boolean {
        Log.d("TAG", "OnMapClickCompass")
        return true
    }

    private fun setMapBoxStyle(styleName: String) {
        mapboxMap.setStyle(
            styleName
        ) { style ->
            mapBoxStyle = style

            mapboxMap.animateCamera(
                CameraUpdateFactory.newCameraPosition(
                    CameraPosition.Builder()
                        .target(mapboxMap.cameraPosition.target)
                        .zoom(mapboxMap.cameraPosition.zoom)
                        .tilt(0.0)
                        .build()
                ), 4000
            )


            mapboxMap.addOnMapClickListener(this)

            initSearchFab()

//            val lastKnownLocation = enableLocationPlugin(mapboxMap.style!!)
//            point.latitude = lastKnownLocation!!.latitude
//            point.longitude = lastKnownLocation.longitude
//            animateCamera(point, 14.0)
//            getAddress(point)
        }
    }


    private fun initSearchFab() {
        btnSearchLocationCompass.setOnClickListener {
            val intent = PlaceAutocomplete.IntentBuilder()
                .accessToken(
                    (if (Mapbox.getAccessToken() != null) Mapbox.getAccessToken() else getString(
                        R.string.mapbox_access_token
                    ))!!
                )
                .placeOptions(
                    PlaceOptions.builder()
                        .backgroundColor(Color.parseColor("#EEEEEE"))
                        .limit(10)
                        .build(PlaceOptions.MODE_CARDS)
                )
                .build(this)
            startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE)
        }
    }

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
        } else {
            permissionsManager = PermissionsManager(this)
            permissionsManager.requestLocationPermissions(this)
        }
        return locationComponent!!.lastKnownLocation
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onExplanationNeeded(permissionsToExplain: List<String>) {
        Toast.makeText(this, R.string.user_location_permission_explanation, Toast.LENGTH_LONG)
            .show()
    }

    override fun onPermissionResult(granted: Boolean) {
        if (granted) {
            val style = mapboxMap.style
            style?.let { enableLocationPlugin(it) }
        } else {
            Toast.makeText(this, R.string.user_location_permission_not_granted, Toast.LENGTH_LONG)
                .show()
            finish()
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


    @SuppressLint("LogNotTimber", "SetTextI18n")
    private fun getAddress(p: LatLng): String? {
        val geocoder = Geocoder(this, Locale.getDefault())
        val addresses: List<Address>?
        return try {
            addresses = geocoder.getFromLocation(p.latitude, p.longitude, 1)
            if (!(addresses == null || addresses.isEmpty())) {
                mapboxMap.getStyle { style ->
                    tvAddressCompass.text = addresses[0].getAddressLine(0)
                    tvLatLngCompass.text = "Latitude:${p.latitude}, Longitude:${p.longitude}"
                    Log.d(Misc.logKey, addresses[0].getAddressLine(0))
                }
                addresses[0].countryName
            } else "null"
        } catch (ignored: IOException) {
            ignored.printStackTrace()
            "Exception"
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_AUTOCOMPLETE) {
            isCurrentLocation = false

            val selectedCarmenFeature = PlaceAutocomplete.getPlace(data)

            point.latitude = (selectedCarmenFeature.geometry() as Point?)!!.latitude()
            point.longitude = (selectedCarmenFeature.geometry() as Point?)!!.longitude()
            animateCamera(point, 14.0)
            getAddress(point)
        }
    }


    @SuppressLint("MissingPermission")
    public override fun onResume() {
        super.onResume()
        mapView.onResume()

        locationCallback = object : LocationCallback() {
            @SuppressLint("SetTextI18n", "LogNotTimber")
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)
                Log.d(Misc.logKey, p0.toString())
                point.latitude = p0.lastLocation.latitude
                point.longitude = p0.lastLocation.longitude
                if (isCurrentLocation && this@CompassActivity::mapboxMap.isInitialized){
                    animateCamera(point, 14.0)
                    getAddress(point)
                }
            }
        }

        val locationRequest = LocationRequest.create()
        locationRequest.fastestInterval = 1000
        locationRequest.interval = 5000
        LocationServices.getFusedLocationProviderClient(this)
            .requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
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


}