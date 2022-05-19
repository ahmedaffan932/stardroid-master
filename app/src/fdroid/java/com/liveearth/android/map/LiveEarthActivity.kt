package com.liveearth.android.map

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.speech.RecognizerIntent
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import com.android.volley.DefaultRetryPolicy
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.liveearth.android.map.clasess.Ads
import com.liveearth.android.map.clasess.Misc
import com.liveearth.android.map.interfaces.*
import com.mapbox.api.directions.v5.DirectionsCriteria
import com.mapbox.api.directions.v5.MapboxDirections
import com.mapbox.api.directions.v5.models.DirectionsResponse
import com.mapbox.api.geocoding.v5.MapboxGeocoding
import com.mapbox.api.geocoding.v5.models.GeocodingResponse
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
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
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute
import com.tarek360.instacapture.Instacapture
import com.tarek360.instacapture.listener.SimpleScreenCapturingListener
import fr.arnaudguyon.xmltojsonlib.XmlToJson
import kotlinx.android.synthetic.main.activity_live_earth.*
import kotlinx.android.synthetic.main.activity_live_earth.btnGetCurrentLocation
import kotlinx.android.synthetic.main.activity_live_earth.btnScreenShot
import kotlinx.android.synthetic.main.activity_live_earth.btnZoomIn
import kotlinx.android.synthetic.main.activity_live_earth.btnZoomOut
import kotlinx.android.synthetic.main.activity_live_earth.llDefault
import kotlinx.android.synthetic.main.activity_live_earth.llHybrid
import kotlinx.android.synthetic.main.activity_live_earth.llSatellite
import kotlinx.android.synthetic.main.activity_live_earth.llTerrain
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.*

@SuppressLint("LogNotTimber")
class LiveEarthActivity : AppCompatActivity(), OnMapReadyCallback,
    MapboxMap.OnMapClickListener, AdapterView.OnItemClickListener {

    private var address = ""
    private var point = LatLng()
    var placesArray = JSONArray()
    private var btnClickCount = 0
    private var isFirstTime = true
    private var latLng: String = ""
    private var isRouteAdded = false
    private var loc: Location? = null
    private val speechRequestCode = 0
    private var isCameraFocused = false
    private lateinit var mapView: MapView
    private var currentLocation = LatLng()
    private var isThreeDViewEnabled = false
    private lateinit var mapBoxStyle: Style
    private var isBtnGenerateVisible = true
    private val places = ArrayList<String>()
    private lateinit var mapboxMap: MapboxMap
    private lateinit var hoveringMarker: ImageView
    private lateinit var droppedMarkerLayer: Layer
    private var buildingPlugin: BuildingPlugin? = null
    private lateinit var navMapRoute: NavigationMapRoute
    private lateinit var locationCallback: LocationCallback

   override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token))
        setContentView(R.layout.activity_live_earth)

        mapView = findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

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
//            manageBtnClickInterstitial()
        }

        btnGetDirection.setOnClickListener {
            if (!Misc.manageNavigationLimit(this) && !Misc.getPurchasedStatus(this)) {
                AlertDialog.Builder(this@LiveEarthActivity)
                    .setTitle("Upgrade to pro.")
                    .setMessage("Your free navigation limit is exceeded. Would you like upgrade? ")
                    .setPositiveButton("Yes") { dialog, _ ->
                        dialog.dismiss()
                        val intent =
                            Intent(this@LiveEarthActivity, ProScreenActivity::class.java)
                        intent.putExtra(Misc.data, Misc.data)
                        startActivity(intent)
                    }
                    .setNegativeButton("May be later.", null)
                    .setIcon(R.drawable.ic_diamond)
                    .show()
            } else {
                if (loc != null) {
                    currentLocation.latitude = loc!!.latitude
                    currentLocation.longitude = loc!!.longitude
                } else {
                    buildAlertMessageNoGps()
                }

                val destination = Point.fromLngLat(point.longitude, point.latitude)
                val origin = Point.fromLngLat(currentLocation.longitude, currentLocation.latitude)
                getRoute(origin, destination)
//                manageBtnClickInterstitial()
            }
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
            Ads.showInterstitial(this, Misc.generateQRIntAm_Al, object : InterstitialCallBack {
                override fun onDismiss() {
                    startActivity(intent)
                }
            })
        }

        btnGetCurrentLocation.setOnClickListener {
            val manager = getSystemService(LOCATION_SERVICE) as LocationManager
            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                buildAlertMessageNoGps()
            } else {
                try {
                    point.latitude = loc!!.latitude
                    point.longitude = loc!!.longitude

                    currentLocation.latitude = loc!!.latitude
                    currentLocation.longitude = loc!!.longitude

                    setMarker(point)
                    animateCamera(point, 14.0)
                    getAddress(point)
                } catch (e: Exception) {
                    Toast.makeText(this, "Sorry, unable to find your location", Toast.LENGTH_SHORT)
                        .show()
                    e.printStackTrace()
                }
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
            setMapBoxStyle(Style.MAPBOX_STREETS, true)
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
//                            manageBtnClickInterstitial()
                        }
                    })
                }
            })
        }

    }

    override fun onMapReady(mapboxMap: MapboxMap) {
        this@LiveEarthActivity.mapboxMap = mapboxMap
        textSatellite.setTextColor(ContextCompat.getColor(this, R.color.pink))
        setMapBoxStyle(Style.SATELLITE, false)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initDroppedMarker(loadedMapStyle: Style) {
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
        btnStartNavigation.visibility = View.GONE
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

    @SuppressLint("MissingSuperCall")
    override fun onSaveInstanceState(outState: Bundle) {
        mapView.onSaveInstanceState(outState)
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

    companion object {
        const val DROPPED_MARKER_LAYER_ID = "DROPPED_MARKER_LAYER_ID"
    }


    private fun getAddress(p: LatLng): String? {
        val geocoder = Geocoder(this, Locale.getDefault())
        val addresses: List<Address>?
        return try {
            addresses = geocoder.getFromLocation(p.latitude, p.longitude, 1)
            if (!(addresses == null || addresses.isEmpty())) {
                mapboxMap.getStyle { style ->
                    Log.d(Misc.logKey, addresses[0].getAddressLine(0))
                    address =
                        addresses[0].getAddressLine(0) + "\n \n http://maps.google.com/?q=${p.latitude},${p.longitude}"
                }
                addresses[0].countryName
            } else "null"
        } catch (ignored: IOException) {
            ignored.printStackTrace()
            "Exception"
        }
    }

    @SuppressLint("MissingPermission")
    private fun setMapBoxStyle(styleName: String, isThreeDView: Boolean) {
        mapboxMap.setStyle(
            styleName
        ) { style ->
            mapBoxStyle = style
            if (isThreeDView) {
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
            } else {
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

            if (isFirstTime) {
                val manager = getSystemService(LOCATION_SERVICE) as LocationManager
                if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    buildAlertMessageNoGps()
                } else {
                    locationCallback = object : LocationCallback() {
                        @SuppressLint("SetTextI18n", "LogNotTimber")
                        override fun onLocationResult(p0: LocationResult) {
                            super.onLocationResult(p0)
                            loc = p0.lastLocation
                            if (!isCameraFocused) {
                                currentLocation.latitude = loc!!.latitude
                                currentLocation.longitude = loc!!.longitude

                                point.latitude = loc!!.latitude
                                point.longitude = loc!!.longitude

                                animateCamera(point, 14.0)
                                getAddress(point)
                                isCameraFocused = true
                            }
                        }
                    }

                    val locationRequest = LocationRequest.create()
                    locationRequest.fastestInterval = 1000
                    locationRequest.interval = 5000
                    LocationServices.getFusedLocationProviderClient(this)
                        .requestLocationUpdates(
                            locationRequest,
                            locationCallback,
                            Looper.getMainLooper()
                        )
                }

                if (loc != null) {
                    currentLocation.latitude = loc!!.latitude
                    currentLocation.longitude = loc!!.longitude

                    point.latitude = loc!!.latitude
                    point.longitude = loc!!.longitude

                    animateCamera(point, 14.0)
                    getAddress(point)
                }

                buildingPlugin = BuildingPlugin(mapView, mapboxMap, style)
                buildingPlugin?.setMinZoomLevel(15f)
                isFirstTime = false
            }
//            else {
//                manageBtnClickInterstitial()
//            }

            hoveringMarker = ImageView(this@LiveEarthActivity)
            hoveringMarker.setImageResource(R.drawable.ic_pin)
            val params = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER
            )
            hoveringMarker.layoutParams = params
            initDroppedMarker(style)
        }
    }

    override fun onMapClick(point: LatLng): Boolean {
        setMarker(point)
        animateCamera(point, 14.0)
        getAddress(point)
        if (isRouteAdded) {
            navMapRoute.removeRoute()
            isRouteAdded = false
            btnStartNavigation.visibility = View.GONE
        }
        this.point = point
        return true
    }

    private fun setMarker(point: LatLng) {
        try {
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
        } catch (e: Exception) {
            e.printStackTrace()
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
            navMapRoute.removeRoute()
            Misc.hideShowView(btnGetDirection, this, true)
            Misc.hideShowView(btnStartNavigation, this, true)
            isRouteAdded = false
        } else {
//            super.onBackPressed()
            Ads.showInterstitial(
                this,
                Misc.liveEarthOnBackIntAm_Al,
                object : InterstitialCallBack {
                    override fun onDismiss() {
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

                svLocation.setText(spokenText, true)
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
        val client: MapboxDirections = MapboxDirections.builder()
            .origin(origin)
            .destination(destination)
            .accessToken(getString(R.string.mapbox_access_token))
            .overview(DirectionsCriteria.OVERVIEW_FULL)
            .profile(DirectionsCriteria.PROFILE_DRIVING)
            .steps(true)
            .voiceInstructions(true)
            .bannerInstructions(true)
            .build()


        client.enqueueCall(object : Callback<DirectionsResponse> {
            override fun onResponse(
                call: Call<DirectionsResponse>,
                response: Response<DirectionsResponse>
            ) {

                if (response.body() == null) {
                    Log.e(
                        Misc.logKey,
                        "No routes found, make sure you set the right user and access token."
                    )
                    return
                } else if (response.body()!!.routes().size < 1) {
                    Log.e(Misc.logKey, "No routes found")
                    return
                }

                val currentRoute = response.body()!!.routes()[0]
                //                Log.d(Misc.logKey, currentRoute.toString())
                Log.d(Misc.logKey, "Here I am.")

                if (this@LiveEarthActivity::navMapRoute.isInitialized) {
                    navMapRoute.removeRoute()
                    isRouteAdded = false
                } else {
                    navMapRoute = NavigationMapRoute(
                        null,
                        mapView,
                        mapboxMap,
                        R.style.NavigationLocationLayerStyle
                    )
                }

                navMapRoute.addRoute(currentRoute)

                Misc.route = currentRoute
                if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.S) {
                    Misc.showView(btnStartNavigation, this@LiveEarthActivity, false)
                    btnStartNavigation.setOnClickListener {
                        if (Misc.manageNavigationLimit(this@LiveEarthActivity) || Misc.getPurchasedStatus(
                                this@LiveEarthActivity
                            )
                        ) {
                            startActivity(
                                Intent(
                                    this@LiveEarthActivity,
                                    NavigationActivity::class.java
                                )
                            )
                        } else {
                            AlertDialog.Builder(this@LiveEarthActivity)
                                .setTitle("Upgrade to pro.")
                                .setMessage("Your free navigation limit is exceeded. Would you like upgrade? ")
                                .setPositiveButton("Yes") { dialog, _ ->
                                    dialog.dismiss()
                                    val intent =
                                        Intent(
                                            this@LiveEarthActivity,
                                            ProScreenActivity::class.java
                                        )
                                    intent.putExtra(Misc.data, Misc.data)
                                    startActivity(intent)
                                }
                                .setNegativeButton("May be later.", null)
                                .setIcon(R.drawable.ic_diamond)
                                .show()
                        }
                    }
                }
                isRouteAdded = true

            }

            override fun onFailure(call: Call<DirectionsResponse>, throwable: Throwable) {
                Log.e(Misc.logKey, "Error: " + throwable.message)

            }
        })
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
        val urlPriceList = if (BuildConfig.DEBUG)
            "http://api.geonames.org/search?name_startsWith=$place&maxRows=10&username=ahmedaffan932"
        else
            "http://api.geonames.org/search?name_startsWith=$place&maxRows=10&username=xtreamappssolutions"
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
                if (!places.contains(jsonObject.getString("name")))
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
            if(svLocation.text.toString().isNotEmpty()){
                if (actionId == EditorInfo.IME_ACTION_DONE) {
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
                            try {
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
                            } catch (e: Exception) {
                                e.printStackTrace()
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
