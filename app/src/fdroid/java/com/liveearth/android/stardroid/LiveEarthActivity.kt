package com.google.android.stardroid

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.speech.RecognizerIntent
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.stardroid.clasess.Misc
import com.google.android.stardroid.interfaces.ActivityOnBackPress
import com.liveearth.android.stardroid.R
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.api.geocoding.v5.GeocodingCriteria
import com.mapbox.api.geocoding.v5.MapboxGeocoding
import com.mapbox.api.geocoding.v5.models.CarmenFeature
import com.mapbox.api.geocoding.v5.models.GeocodingResponse
import com.mapbox.core.exceptions.ServicesException
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
import com.mapbox.mapboxsdk.plugins.building.BuildingPlugin
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions
import com.mapbox.mapboxsdk.style.layers.Layer
import com.mapbox.mapboxsdk.style.layers.Property
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import kotlinx.android.synthetic.main.activity_live_earth.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.*


class LiveEarthActivity : AppCompatActivity(), PermissionsListener,OnMapReadyCallback, MapboxMap.OnMapClickListener
{
    private val REQUEST_CODE_AUTOCOMPLETE = 1
    private val speechRequestCode = 0
    private var buildingPlugin: BuildingPlugin? = null
    private var isThreeDViewEnabled = false

    private var address = ""
    private var isFirstTime = true
    private var isAddressEmpty = true
    private var isBtnGenerateVisible = true
    private lateinit var mapView: MapView
    private lateinit var mapboxMap: MapboxMap
    private lateinit var mapBoxStyle: Style
    private lateinit var permissionsManager: PermissionsManager
    private lateinit var hoveringMarker: ImageView
    private lateinit var droppedMarkerLayer: Layer
    private var latLng: String = ""
    private val point = LatLng()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token))
        setContentView(R.layout.activity_live_earth)

        mapView = findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        /*if (intent.getStringExtra(Misc.data) != null){
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
        }*/


        isBtnGenerateVisible = Misc.hideShowView(btnGenerateQR, this, isBtnGenerateVisible)

        btnShareLocation.setOnClickListener {
            if (address == "") {
                Toast.makeText(this, "Please select valid location.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val sharingIntent = Intent(Intent.ACTION_SEND)
            sharingIntent.type = "text/plain"
            sharingIntent.putExtra(Intent.EXTRA_TEXT, address)
            startActivity(Intent.createChooser(sharingIntent, "Share via"))
        }

        btnZoomIn.setOnClickListener {
            val position = CameraPosition.Builder()
                .target(mapboxMap.cameraPosition.target)
                .zoom(mapboxMap.cameraPosition.zoom + 1)
                .tilt(mapboxMap.cameraPosition.tilt)
                .build()
            mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), 100)
        }

        btnBackLiveEarthMap.setOnClickListener {
            onBackPressed()
        }

        btnZoomOut.setOnClickListener {
            val position = CameraPosition.Builder()
                .target(mapboxMap.cameraPosition.target)
                .zoom(mapboxMap.cameraPosition.zoom - 1)
                .tilt(mapboxMap.cameraPosition.tilt)
                .build()
            mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), 100)
        }

        btnGenerateQR.setOnClickListener {
            val intent = Intent(this, QRGeneratedActivity::class.java)
            Log.d(Misc.logKey, latLng)
            intent.putExtra(Misc.data, latLng)
            startActivity(intent)
        }

        btnGetCurrentLocation.setOnClickListener {
            val manager = getSystemService(LOCATION_SERVICE) as LocationManager
            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                buildAlertMessageNoGps()
            } else {
                val lastKnownLocation = enableLocationPlugin(mapboxMap.style!!)
                point.latitude = lastKnownLocation!!.latitude
                point.longitude = lastKnownLocation.longitude
                setMarker(point)
                animateCamera(point, 14.0)
                getAddress(point)
            }
        }

        btnSpeakSearchLocation.setOnClickListener {
            displaySpeechRecognizer()
        }

        btnTraffic.setOnClickListener {
            setMapBoxStyle(Style.TRAFFIC_DAY, false)
        }

        btnThreeDView.setOnClickListener {
            setMapBoxStyle(Style.MAPBOX_STREETS, true)
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
//        mapView.getMapAsync { mapboxMap ->
//            this.mapboxMap = mapboxMap
//            mapboxMap.setStyle(Style.SATELLITE_STREETS)
//        }
//        mapView.getMapAsync(this)
    }

    override fun onMapReady(mapboxMap: MapboxMap) {
        this@LiveEarthActivity.mapboxMap = mapboxMap
//        mapboxMap.setStyle(Style.SATELLITE)
        setMapBoxStyle(Style.OUTDOORS, false)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initDroppedMarker(loadedMapStyle: Style) {
        // Add the marker image to map
        loadedMapStyle.addImage(
            "dropped-icon-image",
            resources.getDrawable(R.drawable.ic_pin_selected)
        )
        loadedMapStyle.addSource(GeoJsonSource("dropped-marker-source-id"))
        loadedMapStyle.addLayer(
            SymbolLayer(
                DROPPED_MARKER_LAYER_ID,
                "dropped-marker-source-id"
            ).withProperties(
                PropertyFactory.iconImage("dropped-icon-image"),
                PropertyFactory.visibility(Property.NONE),
                PropertyFactory.iconAllowOverlap(true),
                PropertyFactory.iconIgnorePlacement(true)
            )
        )
    }

    public override fun onResume() {
        super.onResume()
        mapView.onResume()
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

    private fun reverseGeocode(point: Point) {
        try {
            val client: MapboxGeocoding = MapboxGeocoding.builder()
                .accessToken(getString(R.string.mapbox_access_token))
                .query(Point.fromLngLat(point.longitude(), point.latitude()))
                .geocodingTypes(GeocodingCriteria.TYPE_ADDRESS)
                .build()
            client.enqueueCall(object : Callback<GeocodingResponse?> {
                override fun onResponse(
                    call: Call<GeocodingResponse?>?,
                    response: Response<GeocodingResponse?>
                ) {
                    if (response.body() != null) {
                        val results: List<CarmenFeature> = response.body()!!.features()
                        if (results.isNotEmpty()) {
                            val feature: CarmenFeature = results[0]

                            // If the geocoder returns a result, we take the first in the list and show a Toast with the place name.
                            mapboxMap.getStyle { style ->
                                if (style.getLayer(DROPPED_MARKER_LAYER_ID) != null) {
                                    Toast.makeText(
                                        this@LiveEarthActivity,
                                        java.lang.String.format(
                                            getString(R.string.location_picker_place_name_result),
                                            feature.placeName()
                                        ), Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        } else {
                            Toast.makeText(
                                this@LiveEarthActivity,
                                getString(R.string.location_picker_dropped_marker_snippet_no_results),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

                override fun onFailure(call: Call<GeocodingResponse?>?, throwable: Throwable) {
                    Log.e("Geocoding Failure: %s", throwable.message.toString())
                }
            })
        } catch (servicesException: ServicesException) {
            Log.e("Error geocoding: %s", servicesException.toString())
            servicesException.printStackTrace()
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

    companion object {
        private const val DROPPED_MARKER_LAYER_ID = "DROPPED_MARKER_LAYER_ID"
    }


    private fun getAddress(p: LatLng): String? {
        val geocoder = Geocoder(this, Locale.getDefault())
        val addresses: List<Address>?
        return try {
            addresses = geocoder.getFromLocation(p.latitude, p.longitude, 1)
            if (!(addresses == null || addresses.isEmpty())) {
                mapboxMap.getStyle { style ->
                    if (style.getLayer(DROPPED_MARKER_LAYER_ID) != null) {
                        Toast.makeText(
                            this@LiveEarthActivity,
                            java.lang.String.format(
                                getString(R.string.location_picker_place_name_result),
                                addresses[0].getAddressLine(0)
                            ), Toast.LENGTH_SHORT
                        ).show()
                    }
                    Log.d(Misc.logKey, addresses[0].getAddressLine(0))
                    address = addresses[0].getAddressLine(0) + "\n \n http://maps.google.com/?q=${p.latitude},${p.longitude}"
                }
                addresses[0].countryName
            } else "null"
        } catch (ignored: IOException) {
            ignored.printStackTrace()
            "Exception"
        }
    }

    private fun setMapBoxStyle(styleName: String, isThreeDView: Boolean) {
        if(isThreeDViewEnabled && isThreeDView){
            return
        }
        mapboxMap.setStyle(
            styleName
        ) { style ->
            mapBoxStyle = style
            if(isThreeDView) {
                buildingPlugin?.setVisibility(true)
                mapboxMap.animateCamera(
                    CameraUpdateFactory.newCameraPosition(
                        CameraPosition.Builder()
                            .target(mapboxMap.cameraPosition.target)
                            .zoom(17.0)
                            .tilt(60.0)
                            .build()
                    ), 4000
                )
                isThreeDViewEnabled = true
            }else{
                mapboxMap.animateCamera(
                    CameraUpdateFactory.newCameraPosition(
                        CameraPosition.Builder()
                            .target(mapboxMap.cameraPosition.target)
                            .zoom(mapboxMap.cameraPosition.zoom)
                            .tilt(0.0)
                            .build()
                    ), 4000
                )
                buildingPlugin?.setVisibility(false)
                isThreeDViewEnabled = false
            }
            mapboxMap.addOnMapClickListener(this)
            initSearchFab();

            if (isFirstTime) {
                enableLocationPlugin(style)
                val manager = getSystemService(LOCATION_SERVICE) as LocationManager
                if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    buildAlertMessageNoGps()
                }
                buildingPlugin = BuildingPlugin(mapView, mapboxMap, style)
                buildingPlugin?.setMinZoomLevel(15f)
                isFirstTime = false
            }

            hoveringMarker = ImageView(this@LiveEarthActivity)
            hoveringMarker.setImageResource(R.drawable.ic_pin)
            val params = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER
            )
            hoveringMarker.layoutParams = params
//            mapView.addView(hoveringMarker)

//            Initialize, but don't show, a SymbolLayer for the marker icon which will represent a selected location.
            initDroppedMarker(style)
        }
    }

    override fun onMapClick(point: LatLng): Boolean {
        setMarker(point)
        animateCamera(point, 14.0)
        getAddress(point)
        return true
    }

    private fun setMarker(point: LatLng) {
        hoveringMarker.visibility = View.INVISIBLE
        if (mapBoxStyle.getLayer(DROPPED_MARKER_LAYER_ID) != null) {
            val source = mapBoxStyle.getSourceAs<GeoJsonSource>("dropped-marker-source-id")
            source?.setGeoJson(
                Point.fromLngLat(
                    point.longitude,
                    point.latitude
                )
            )
            droppedMarkerLayer = mapBoxStyle.getLayer(DROPPED_MARKER_LAYER_ID)!!
            droppedMarkerLayer.setProperties(PropertyFactory.visibility(Property.VISIBLE))
            latLng = "geo:${point.latitude},${point.longitude},100"


            isBtnGenerateVisible = Misc.showView(btnGenerateQR, this, isBtnGenerateVisible)
        }
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

    override fun onBackPressed() {
        Misc.backActivity(this, Misc.isLiveEarthOnBackIntEnabled, object : ActivityOnBackPress {
            override fun onBackPress() {
                finish()
            }
        })
    }

    private fun initSearchFab() {
        svLocation.setOnClickListener {
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_AUTOCOMPLETE) {

            val selectedCarmenFeature = PlaceAutocomplete.getPlace(data)

            point.latitude = (selectedCarmenFeature.geometry() as Point?)!!.latitude()
            point.longitude = (selectedCarmenFeature.geometry() as Point?)!!.longitude()
            animateCamera(point, 10.0)
            setMarker(point)
            getAddress(point)
        }

        if (requestCode == speechRequestCode && resultCode == Activity.RESULT_OK) {
            val spokenText: String? =
                data!!.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).let { results ->
                    results?.get(0)
                }

            if (spokenText != null) {
                val geocodingClient: MapboxGeocoding = MapboxGeocoding.builder()
                    .query(spokenText)
                    .accessToken(getString(R.string.mapbox_access_token))
                    .build()

                geocodingClient.enqueueCall(object : Callback<GeocodingResponse> {
                    override fun onResponse(
                        call: Call<GeocodingResponse>,
                        response: Response<GeocodingResponse>
                    ) {
                            if(response.isSuccessful){
                                val t =  response.body()?.features()?.get(0)?.geometry()
                                val arr = t.toString().split("[", "]")
                                val p = arr[1].split(",")
                                point.latitude = p[1].toDouble()
                                point.longitude = p[0].toDouble()
                                setMarker(point)
                                animateCamera(point, 10.0)
                                for(item in arr)
                                Log.d(Misc.logKey,item)
                            }else{
                                Toast.makeText(this@LiveEarthActivity, "Place not found.", Toast.LENGTH_SHORT).show()
                            }

                    }

                    override fun onFailure(call: Call<GeocodingResponse>, t: Throwable) {
                        Toast.makeText(this@LiveEarthActivity, "Place not found.", Toast.LENGTH_SHORT).show()
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
            ), 3000
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
}
