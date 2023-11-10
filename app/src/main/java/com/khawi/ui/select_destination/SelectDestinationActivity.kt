package com.khawi.ui.select_destination

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.beust.klaxon.array
import com.beust.klaxon.get
import com.beust.klaxon.obj
import com.beust.klaxon.string
import com.birjuvachhani.locus.Locus
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.khawi.R
import com.khawi.base.BaseActivity
import com.khawi.base.getAddress
import com.khawi.base.parcelable
import com.khawi.databinding.ActivitySelectDestinationBinding
import dagger.hilt.android.AndroidEntryPoint
import org.jetbrains.anko.async
import org.jetbrains.anko.uiThread
import java.net.URL


@AndroidEntryPoint
class SelectDestinationActivity : BaseActivity() {
    private lateinit var binding: ActivitySelectDestinationBinding
    private var googleMap: GoogleMap? = null
    private var isFirst = true
    private var isPreview = false
    private var latlngMyLocation: LatLng? = null
    private var latlngStart: LatLng? = null
    private var latlngEnd: LatLng? = null

    companion object {
        const val latLongStartKey = "lat_long_start"
        const val latLongEndKey = "lat_long_end"
        const val isPreviewKey = "is_preview"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectDestinationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        isPreview = intent.getBooleanExtra(isPreviewKey, false)
        binding.buttonGroup.visibility =
            if (isPreview)
                View.GONE
            else
                View.VISIBLE
        latlngStart = intent.parcelable(latLongStartKey)
        latlngEnd = intent.parcelable(latLongEndKey)

        Locus.getCurrentLocation(this) { result ->
            result.location?.let {
                latlngMyLocation = LatLng(it.latitude, it.longitude)
            }
            result.error?.let { }
        }

        binding.back.setOnClickListener {
            finish()
        }

        binding.yourLocation.setOnClickListener {
            latlngMyLocation?.let { lat ->
                googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(lat, 15f))
            }
        }

        handleMarkers()

        binding.startDestinationET.setOnClickListener {
            latlngEnd = null
            latlngStart = null
            handleMarkers()
        }

        binding.endDestinationET.setOnClickListener {
            latlngEnd = null
            handleMarkers()
        }

        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync {
            googleMap = it
            if (isFirst)
                latlngMyLocation?.let { lat ->
                    googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(lat, 15f))
                    isFirst = false
                }

            googleMap?.uiSettings?.isCompassEnabled = true
            googleMap?.uiSettings?.isZoomGesturesEnabled = true
            googleMap?.uiSettings?.isZoomControlsEnabled = true
            googleMap?.uiSettings?.isRotateGesturesEnabled = false
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return@getMapAsync
            }
            googleMap?.isMyLocationEnabled = true
        }

        binding.selectBtn.setOnClickListener {
            when {
                latlngStart == null -> {
                    latlngStart = googleMap?.cameraPosition?.target ?: LatLng(0.0, 0.0)
                }

                latlngEnd == null -> {
                    latlngEnd = googleMap?.cameraPosition?.target ?: LatLng(0.0, 0.0)
                }
            }
            handleMarkers()
            if (latlngStart != null && latlngEnd != null) {
                val intent = Intent()
                intent.putExtra(latLongStartKey, latlngStart)
                intent.putExtra(latLongEndKey, latlngEnd)
                setResult(RESULT_OK, intent)
                finish()
            }
        }
    }

    private fun handleMarkers() {

        binding.endDestinationGroup.visibility = View.GONE

        binding.startDestinationET.text = ""
        binding.startDestinationET.setCompoundDrawablesRelativeWithIntrinsicBounds(
            0, 0, 0, 0
        )
        binding.endDestinationET.text = ""
        binding.endDestinationET.setCompoundDrawablesRelativeWithIntrinsicBounds(
            0, 0, 0, 0
        )
        binding.markerIV.setImageResource(R.drawable.selecting_start_marker)

        latlngStart?.let { latlngStart ->
            binding.markerIV.setImageResource(R.drawable.end_marker)
            binding.selectBtn.text = getString(R.string.select_end)
            binding.startDestinationET.text = latlngStart.getAddress(this)
            if (!isPreview)
                binding.startDestinationET.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    R.drawable.edit, 0, 0, 0
                )
            googleMap?.clear()
            val markerOptions = MarkerOptions()
                .position(latlngStart)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.selected_start_marker))
            googleMap?.addMarker(markerOptions)
            binding.endDestinationGroup.visibility = View.VISIBLE
        }

        latlngEnd?.let { latlngEnd ->
            binding.markerIV.setImageResource(R.drawable.end_marker)
            binding.selectBtn.text = getString(R.string.save_changes)
            binding.endDestinationET.text = latlngEnd.getAddress(this)
            if (!isPreview)
                binding.endDestinationET.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    R.drawable.edit, 0, 0, 0
                )
            val markerOptions = MarkerOptions()
                .position(latlngEnd)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.end_marker))
            googleMap?.addMarker(markerOptions)
            drawRout()
        }
    }


    private fun drawRout() {
        val url = getURL(latlngStart!!, latlngEnd!!)

        val LatLongB = LatLngBounds.Builder()
        val options = PolylineOptions()
        options.color(Color.parseColor("#006E85"))
        options.width(5f)
        async {
            // Connect to URL, download content and convert into string asynchronously
            val result = URL(url).readText()
            uiThread {
                // When API call is done, create parser and convert into JsonObjec
                val parser = Parser()
                val stringBuilder: StringBuilder = StringBuilder(result)
                val json: JsonObject = parser.parse(stringBuilder) as JsonObject
                // get to the correct element in JsonObject
                val routes = json.array<JsonObject>("routes")
                val points = routes!!["legs"]["steps"][0] as JsonArray<JsonObject>
                // For every element in the JsonArray, decode the polyline string and pass all points to a List
                val polypts = points.flatMap { decodePoly(it.obj("polyline")?.string("points")!!) }
                // Add  points to polyline and bounds
                options.add(latlngStart)
                LatLongB.include(latlngStart!!)
                for (point in polypts) {
                    options.add(point)
                    LatLongB.include(point)
                }
                options.add(latlngEnd)
                LatLongB.include(latlngEnd!!)
                // build bounds
                val bounds = LatLongB.build()
                // add polyline to the map
                googleMap?.addPolyline(options)
                // show map with route centered
                googleMap?.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
            }
        }
    }

    private fun getURL(from: LatLng, to: LatLng): String {
        val origin = "origin=" + from.latitude + "," + from.longitude
        val dest = "destination=" + to.latitude + "," + to.longitude
        val sensor = "sensor=false"
        val params = "$origin&$dest&$sensor"
        return "https://maps.googleapis.com/maps/api/directions/json?$params"
    }

    private fun decodePoly(encoded: String): List<LatLng> {
        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat

            shift = 0
            result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            val p = LatLng(
                lat.toDouble() / 1E5,
                lng.toDouble() / 1E5
            )
            poly.add(p)
        }

        return poly
    }
}