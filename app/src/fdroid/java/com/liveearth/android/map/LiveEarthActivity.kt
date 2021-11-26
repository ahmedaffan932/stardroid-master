package com.liveearth.android.map

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import com.android.volley.DefaultRetryPolicy
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.liveearth.android.map.clasess.Misc
import com.liveearth.android.map.interfaces.ActivityOnBackPress
import com.liveearth.android.map.interfaces.OnImageSaveCallBack
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.api.directions.v5.models.RouteOptions
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
import com.mapbox.mapboxsdk.plugins.building.BuildingPlugin
import com.mapbox.mapboxsdk.style.layers.Layer
import com.mapbox.mapboxsdk.style.layers.Property
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.gestures.OnMapClickListener
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.navigation.base.extensions.applyDefaultNavigationOptions
import com.mapbox.navigation.base.options.NavigationOptions
import com.mapbox.navigation.base.route.RouterCallback
import com.mapbox.navigation.base.route.RouterFailure
import com.mapbox.navigation.base.route.RouterOrigin
import com.mapbox.navigation.ui.maps.route.line.api.MapboxRouteLineApi
import com.mapbox.navigation.ui.maps.route.line.api.MapboxRouteLineView
import com.mapbox.navigation.ui.maps.route.line.model.MapboxRouteLineOptions
import com.mapbox.navigation.ui.maps.route.line.model.RouteLine
import com.mapbox.services.android.navigation.v5.navigation.MapboxNavigation
import com.tarek360.instacapture.Instacapture
import com.tarek360.instacapture.listener.SimpleScreenCapturingListener
import fr.arnaudguyon.xmltojsonlib.XmlToJson
import kotlinx.android.synthetic.main.activity_altitude.*
import kotlinx.android.synthetic.main.activity_live_earth.*
import kotlinx.android.synthetic.main.activity_live_earth.btnGetCurrentLocation
import kotlinx.android.synthetic.main.activity_live_earth.btnScreenShot
import kotlinx.android.synthetic.main.activity_live_earth.btnSpeakSearchLocation
import kotlinx.android.synthetic.main.activity_live_earth.btnZoomIn
import kotlinx.android.synthetic.main.activity_live_earth.btnZoomOut
import kotlinx.android.synthetic.main.activity_live_earth.llDefault
import kotlinx.android.synthetic.main.activity_live_earth.llHybrid
import kotlinx.android.synthetic.main.activity_live_earth.llSatellite
import kotlinx.android.synthetic.main.activity_live_earth.llTerrain
import kotlinx.android.synthetic.main.activity_live_earth.svLocation
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.*

@SuppressLint("LogNotTimber")
class LiveEarthActivity : AppCompatActivity(), PermissionsListener, OnMapReadyCallback,
    MapboxMap.OnMapClickListener, AdapterView.OnItemClickListener {
    private val places = ArrayList<String>()
    var placesArray = JSONArray()

    private val speechRequestCode = 0
    private var buildingPlugin: BuildingPlugin? = null
    private var isThreeDViewEnabled = false

    private var address = ""
    private var isFirstTime = true
    private var isBtnGenerateVisible = true
    private lateinit var mapView: com.mapbox.maps.MapView
    private lateinit var mapboxMap: MapboxMap
    private lateinit var mapBoxStyle: Style
    private lateinit var permissionsManager: PermissionsManager
    private lateinit var hoveringMarker: ImageView
    private lateinit var droppedMarkerLayer: Layer
    private var latLng: String = ""
    private var point = LatLng()
    private var currentLocation = LatLng()
    private lateinit var navMapRoute: MapboxNavigation
    private var isRouteAdded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token))
        setContentView(R.layout.activity_live_earth)
        mapView = findViewById(R.id.mapView)
        mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS)

        val annotationApi = mapView.annotations

        mapView = findViewById(R.id.mapView)
        mapView.gestures.pinchToZoomEnabled = true

        mapView.gestures.addOnMapClickListener { point ->
            annotationApi.cleanup()
            val pointAnnotationManager = annotationApi.createPointAnnotationManager(mapView)
            val pointAnnotationOptions: PointAnnotationOptions = PointAnnotationOptions()
                .withPoint(Point.fromLngLat(point.longitude(), point.latitude()))
                .withIconImage(BitmapFactory.decodeResource(resources, R.drawable.placeholder))
            pointAnnotationManager.create(pointAnnotationOptions)
            this.point.latitude = point.latitude()
            this.point.longitude = point.longitude()
//            animateCamera(this.point, 14.0)
            getAddress(this.point)
            true
        }

        Misc.hideShowView(btnGetDirection, this, isBtnGenerateVisible)
        Misc.hideShowView(btnStartNavigation, this, isBtnGenerateVisible)
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

        btnGetDirection.setOnClickListener {
            val locationComponent = mapboxMap.locationComponent
            val lastKnownLocation = locationComponent.lastKnownLocation

            if (lastKnownLocation != null) {
                currentLocation.latitude = lastKnownLocation.latitude
                currentLocation.longitude = lastKnownLocation.longitude
            } else {
                buildAlertMessageNoGps()
            }

            val destination = Point.fromLngLat(point.longitude, point.latitude)
            val origin = Point.fromLngLat(currentLocation.longitude, currentLocation.latitude)

            getRoute(origin, destination)
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

                currentLocation.latitude = lastKnownLocation.latitude
                currentLocation.longitude = lastKnownLocation.longitude

                setMarker(point)
                animateCamera(point, 14.0)
                getAddress(point)
            }
        }

        btnSpeakSearchLocation.setOnClickListener {
            displaySpeechRecognizer()
        }

        searchSuggestions()

        btnTraffic.setOnClickListener {
            setBtnTextWhiteColor()
            setMapBoxStyle(Style.TRAFFIC_DAY, false)
            textTraffic.setTextColor(ContextCompat.getColor(this, R.color.pink))
        }

        btnThreeDView.setOnClickListener {
            setBtnTextWhiteColor()
            setMapBoxStyle(Style.SATELLITE_STREETS, true)
            text3d.setTextColor(ContextCompat.getColor(this, R.color.pink))
        }

        llDefault.setOnClickListener {
            setBtnTextWhiteColor()
            setMapBoxStyle(Style.OUTDOORS, false)
            textStreet.setTextColor(ContextCompat.getColor(this, R.color.pink))
        }

        llSatellite.setOnClickListener {
            setBtnTextWhiteColor()
            setMapBoxStyle(Style.SATELLITE, false)
            textSatellite.setTextColor(ContextCompat.getColor(this, R.color.pink))
        }
        llTerrain.setOnClickListener {
            setBtnTextWhiteColor()
            setMapBoxStyle(Style.SATELLITE_STREETS, false)
            textTerrain.setTextColor(ContextCompat.getColor(this, R.color.pink))
        }
        llHybrid.setOnClickListener {
            setBtnTextWhiteColor()
            setMapBoxStyle(Style.DARK, false)
            textHybrid.setTextColor(ContextCompat.getColor(this, R.color.pink))
        }

        btnScreenShot.setOnClickListener {
            Instacapture.capture(this, object : SimpleScreenCapturingListener() {
                override fun onCaptureComplete(bitmap: Bitmap) {

                    Misc.saveImageToExternal(this@LiveEarthActivity, bitmap, object :
                        OnImageSaveCallBack {
                        override fun onImageSaved() {
                            Toast.makeText(
                                this@LiveEarthActivity,
                                "Screen Shot Saved in Gallery. ",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })

                }
            })
        }

    }

    override fun onMapReady(mapboxMap: MapboxMap) {
        this@LiveEarthActivity.mapboxMap = mapboxMap
//        mapboxMap.setStyle(Style.SATELLITE)
        textSatellite.setTextColor(ContextCompat.getColor(this, R.color.pink))
        setMapBoxStyle(Style.SATELLITE, false)
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
//        mapView.onResume()
        btnStartNavigation.visibility = View.GONE
    }

    @SuppressLint("Lifecycle")
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
//        mapView.onPause()
    }

    @SuppressLint("MissingSuperCall")
    override fun onSaveInstanceState(outState: Bundle) {
//        mapView.onSaveInstanceState(outState)
        super.onSaveInstanceState(outState)
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
                Log.d(Misc.logKey, addresses[0].getAddressLine(0))
                address =
                    addresses[0].getAddressLine(0) + "\n \n http://maps.google.com/?q=${p.latitude},${p.longitude}"
                addresses[0].countryName
            } else "null"
        } catch (ignored: IOException) {
            ignored.printStackTrace()
            "Exception"
        }
    }

    private fun setMapBoxStyle(styleName: String, isThreeDView: Boolean) {
        mapView.getMapboxMap().loadStyleUri(styleName)

        if (isThreeDView) {
            mapView.getMapboxMap().setCamera(
                CameraOptions.Builder()
                    .center(Point.fromLngLat(-74.0066, 40.7135))
                    .pitch(45.0)
                    .zoom(15.5)
                    .bearing(-17.6)
                    .build()
            )
        }else{
            mapView.getMapboxMap().setCamera(
                CameraOptions.Builder()
                    .center(Point.fromLngLat(-74.0066, 40.7135))
                    .pitch(0.0)
                    .zoom(15.5)
                    .bearing(0.0)
                    .build()
            )
        }
    }

    override fun onMapClick(point: LatLng): Boolean {
        setMarker(point)
        animateCamera(point, 14.0)
        getAddress(point)
        if (isRouteAdded) {
//            navMapRoute.removeRoute()
            isRouteAdded = false
        }
        this.point = point
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


            Misc.showView(btnGetDirection, this, false)
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
        if (isRouteAdded) {
//            navMapRoute.removeRoute()
            Misc.hideShowView(btnGetDirection, this, true)
            Misc.hideShowView(btnStartNavigation, this, true)
            isRouteAdded = false
        } else {
            Misc.backActivity(this, Misc.isLiveEarthOnBackIntEnabled, object : ActivityOnBackPress {
                override fun onBackPress() {
                    finish()
                }
            })
        }
    }

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
                    .accessToken(getString(R.string.mapbox_access_token))
                    .build()

                geocodingClient.enqueueCall(object : Callback<GeocodingResponse> {
                    @SuppressLint("LogNotTimber")
                    override fun onResponse(
                        call: Call<GeocodingResponse>,
                        response: Response<GeocodingResponse>
                    ) {
                        if (response.isSuccessful) {
                            val t = response.body()?.features()?.get(0)?.geometry()
                            val arr = t.toString().split("[", "]")
                            val p = arr[1].split(",")
                            point.latitude = p[1].toDouble()
                            point.longitude = p[0].toDouble()
                            setMarker(point)
                            animateCamera(point, 10.0)
                            for (item in arr)
                                Log.d(Misc.logKey, item)
                        } else {
                            Toast.makeText(
                                this@LiveEarthActivity,
                                "Place not found.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    }

                    override fun onFailure(call: Call<GeocodingResponse>, t: Throwable) {
                        Toast.makeText(
                            this@LiveEarthActivity,
                            "Place not found.",
                            Toast.LENGTH_SHORT
                        ).show()
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

    private fun getRoute(origin: Point, destination: Point) {

//        val client = MapboxDirections.builder()
//            .origin(origin)
//            .destination(destination)
//            .accessToken(getString(R.string.mapbox_access_token))
//            .overview(DirectionsCriteria.OVERVIEW_FULL)
//            .profile(DirectionsCriteria.PROFILE_DRIVING)
//            .steps(true)
//            .voiceInstructions(true)
//            .bannerInstructions(true)
//            .build()

        val navigationOptions = NavigationOptions.Builder(this)
            .accessToken(getString(R.string.mapbox_access_token))
            .build()
//        val mapboxNavigation = MapboxNavigationProvider.create(navigationOptions)
//        navMapRoute = MapboxNavigation(navigationOptions)
//        navMapRoute.requestRoutes(
//            RouteOptions.builder()
//                .applyDefaultNavigationOptions()
//                .coordinatesList(listOf(origin, destination))
//                .build(),
//            object : RouterCallback {
//                override fun onCanceled(routeOptions: RouteOptions, routerOrigin: RouterOrigin) {
//                    TODO("Not yet implemented")
//                }
//
//                override fun onFailure(reasons: List<RouterFailure>, routeOptions: RouteOptions) {
//                    Toast.makeText(
//                        this@LiveEarthActivity,
//                        "Some error occurred in route generation.",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                    for (reason in reasons)
//                        Log.d(Misc.logKey, reason.message)
//                }
//
//                override fun onRoutesReady(
//                    routes: List<DirectionsRoute>,
//                    routerOrigin: RouterOrigin
//                ) {
//                    val routeLineOptions = MapboxRouteLineOptions.Builder(this@LiveEarthActivity).build()
//                    val routeLineApi = MapboxRouteLineApi(routeLineOptions)
//                    val routeLineView = MapboxRouteLineView(routeLineOptions)
//
//                    val routeLines = routes.map { RouteLine(it, null) }
//                    routeLineApi.setRoutes(routeLines) { value ->
////                        val s = Style
//                        routeLineView.renderRouteDrawData(mapBoxStyle, value)
//                    }
//                }
//
//            }
//        )

    }

    private fun setBtnTextWhiteColor() {
        text3d.setTextColor(Color.parseColor("#ffffff"))
        textDirection.setTextColor(Color.parseColor("#ffffff"))
        textHybrid.setTextColor(Color.parseColor("#ffffff"))
        textLocation.setTextColor(Color.parseColor("#ffffff"))
        textQR.setTextColor(Color.parseColor("#ffffff"))
        textSatellite.setTextColor(Color.parseColor("#ffffff"))
        textStreet.setTextColor(Color.parseColor("#ffffff"))
        textTerrain.setTextColor(Color.parseColor("#ffffff"))
        textTraffic.setTextColor(Color.parseColor("#ffffff"))
    }

    private fun getSuggestedPlaces(place: String) {
        val urlPriceList =
            "http://api.geonames.org/search?name_startsWith=$place&maxRows=10&username=ahmedaffan932"
        val stringRequest: StringRequest =
            object : StringRequest(
                Method.GET,
                urlPriceList,
                com.android.volley.Response.Listener { response ->
                    try {
                        val json: XmlToJson = XmlToJson.Builder(response.toString()).build()
                        json.toJson()?.let { setAdapter(it) }
                    } catch (e: JSONException) {
                        Log.d(Misc.logKey, e.printStackTrace().toString())
                    }
                },
                com.android.volley.Response.ErrorListener { error ->
                    Log.d(Misc.logKey, error.toString())
                }) {

            }
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)
        stringRequest.retryPolicy = DefaultRetryPolicy(
            0,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
    }


    fun setAdapter(j: JSONObject): Int {
        if (j.getJSONObject("geonames").getInt("totalResultsCount") != 0) {
            val jArray = j.getJSONObject("geonames").getJSONArray("geoname")
            places.clear()
            for (n in 0 until jArray.length()) {
                val jsonObject = jArray.getJSONObject(n)
                places.add(jsonObject.getString("name"))
            }
            placesArray = jArray
            val adapter: ArrayAdapter<String> = ArrayAdapter(
                this,
                R.layout.custom_list_item, R.id.text_view_list_item, places
            )
            svLocation.setAdapter(adapter)
            adapter.notifyDataSetChanged()
        }
        return j.getJSONObject("geonames").getInt("totalResultsCount")
    }

    private fun searchSuggestions() {
        svLocation.setOnEditorActionListener { _, actionId, _ ->
            var handled = false
            if (actionId == EditorInfo.IME_ACTION_DONE) {
//                getSearchedPlace(svLocation.text.toString())
                hideSoftKeyboard()
                svLocation.clearFocus()

                val geocodingClient: MapboxGeocoding = MapboxGeocoding.builder()
                    .query(svLocation.text.toString())
                    .accessToken(getString(R.string.mapbox_access_token))
                    .build()

                geocodingClient.enqueueCall(object : Callback<GeocodingResponse> {
                    @SuppressLint("LogNotTimber")
                    override fun onResponse(
                        call: Call<GeocodingResponse>,
                        response: Response<GeocodingResponse>
                    ) {
                        if (response.isSuccessful) {
                            val t = response.body()?.features()?.get(0)?.geometry()
                            val arr = t.toString().split("[", "]")
                            val p = arr[1].split(",")
                            point.latitude = p[1].toDouble()
                            point.longitude = p[0].toDouble()
                            setMarker(point)
                            animateCamera(point, 10.0)
                            for (item in arr)
                                Log.d(Misc.logKey, item)
                        } else {
                            Toast.makeText(
                                this@LiveEarthActivity,
                                "Place not found.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    }

                    override fun onFailure(call: Call<GeocodingResponse>, t: Throwable) {
                        Toast.makeText(
                            this@LiveEarthActivity,
                            "Place not found.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                })
                handled = true
            }
            handled
        }

        svLocation.onItemClickListener = this

        svLocation.addTextChangedListener {
            getSuggestedPlaces(svLocation.text.toString())
        }
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

        val geocodingClient: MapboxGeocoding = MapboxGeocoding.builder()
            .query(svLocation.text.toString())
            .accessToken(getString(R.string.mapbox_access_token))
            .build()
        hideSoftKeyboard()

        geocodingClient.enqueueCall(object : Callback<GeocodingResponse> {
            @SuppressLint("LogNotTimber")
            override fun onResponse(
                call: Call<GeocodingResponse>,
                response: Response<GeocodingResponse>
            ) {
                if (response.isSuccessful) {
                    val t = response.body()?.features()?.get(0)?.geometry()
                    val arr = t.toString().split("[", "]")
                    val p = arr[1].split(",")
                    point.latitude = p[1].toDouble()
                    point.longitude = p[0].toDouble()
                    setMarker(point)
                    animateCamera(point, 10.0)
                    for (item in arr)
                        Log.d(Misc.logKey, item)
                } else {
                    Toast.makeText(
                        this@LiveEarthActivity,
                        "Place not found.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }

            override fun onFailure(call: Call<GeocodingResponse>, t: Throwable) {
                Toast.makeText(
                    this@LiveEarthActivity,
                    "Place not found.",
                    Toast.LENGTH_SHORT
                ).show()
            }

        })
    }


    fun hideSoftKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}
