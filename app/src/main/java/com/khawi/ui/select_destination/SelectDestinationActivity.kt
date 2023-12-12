package com.khawi.ui.select_destination

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.birjuvachhani.locus.Locus
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.maps.DirectionsApi
import com.google.maps.GeoApiContext
import com.khawi.R
import com.khawi.base.BaseActivity
import com.khawi.base.getAddress
import com.khawi.base.startKey
import com.khawi.base.trackingTable
import com.khawi.databinding.ActivitySelectDestinationBinding
import com.khawi.model.Order
import com.khawi.model.TrackingLocation
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SelectDestinationActivity : BaseActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivitySelectDestinationBinding
    private var googleMap: GoogleMap? = null
    private var isFirst = true
    private var isPreview = false
    private var latlngMyLocation: LatLng? = null
    private var latlngStart: LatLng? = null
    private var latlngEnd: LatLng? = null
    private var order: Order? = null
    private var marker: Marker? = null

    companion object {
        const val latLongStartKey = "lat_long_start"
        const val latLongEndKey = "lat_long_end"
        const val isPreviewKey = "is_preview"
        const val orderKey = "order"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectDestinationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        order = intent.getParcelableExtra(orderKey) as? Order
        isPreview = intent.getBooleanExtra(isPreviewKey, false)
        binding.buttonGroup.visibility =
            if (isPreview)
                View.GONE
            else
                View.VISIBLE
        latlngStart = intent.getParcelableExtra(latLongStartKey) as? LatLng
        latlngEnd = intent.getParcelableExtra(latLongEndKey) as? LatLng

        Locus.getCurrentLocation(this) { result ->
            result.location?.let {
                latlngMyLocation = LatLng(it.latitude, it.longitude)
                if (latlngStart == null)
                    latlngStart = latlngMyLocation
                handleMap()
            }
            result.error?.let { }
        }

        binding.back.setOnClickListener {
            finish()
        }

        binding.yourLocation.setOnClickListener {
            latlngMyLocation?.let { lat ->
                googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(lat, 15f))
            }
        }

        binding.startDestinationET.setOnClickListener {
            latlngEnd = null
            latlngStart = null
            handleMarkers()
        }

        binding.endDestinationET.setOnClickListener {
            latlngEnd = null
            handleMarkers()
        }

        binding.selectBtn.setOnClickListener {
            if (latlngStart != null && latlngEnd != null) {
                val intent = Intent()
                intent.putExtra(latLongStartKey, latlngStart)
                intent.putExtra(latLongEndKey, latlngEnd)
                setResult(RESULT_OK, intent)
                finish()
            }
            when {
                latlngStart == null -> {
                    latlngStart = googleMap?.cameraPosition?.target ?: LatLng(0.0, 0.0)
                }

                latlngEnd == null -> {
                    latlngEnd = googleMap?.cameraPosition?.target ?: LatLng(0.0, 0.0)
                }
            }
            handleMarkers()
        }

        trackingDriver()
    }

    override fun onMapReady(p0: GoogleMap) {
        googleMap = p0
        if (isFirst)
            latlngMyLocation?.let { lat ->
                googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(lat, 15f))
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
            return
        }
        googleMap?.isMyLocationEnabled = true
        handleMarkers()
    }

    private fun handleMap() {
        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
    }

    private fun handleMarkers() {

        binding.endDestinationGroup.visibility = View.GONE
        binding.markerIV.visibility = View.VISIBLE

        binding.startDestinationET.text = ""
        binding.startDestinationET.setCompoundDrawablesRelativeWithIntrinsicBounds(
            0, 0, 0, 0
        )
        binding.endDestinationET.text = ""
        binding.endDestinationET.setCompoundDrawablesRelativeWithIntrinsicBounds(
            0, 0, 0, 0
        )
        if (!isPreview)
            binding.markerIV.setImageResource(R.drawable.selecting_start_marker)
        else
            binding.markerIV.visibility = View.GONE
        binding.selectBtn.text = getString(R.string.select_start)
        binding.selectBtn.background = ContextCompat.getDrawable(this , R.drawable.button_blue)

        latlngStart?.let { latlngStart ->
            binding.markerIV.setImageResource(R.drawable.end_marker)
            binding.selectBtn.text = getString(R.string.select_end)
            binding.selectBtn.background = ContextCompat.getDrawable(this , R.drawable.button_main)
            binding.startDestinationET.text = latlngStart.getAddress(this)
            if (!isPreview)
                binding.startDestinationET.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    R.drawable.edit, 0, 0, 0
                )
            googleMap?.clear()
            val markerOptions = MarkerOptions()
                .position(latlngStart)
                .icon(generateBitmapDescriptorFromRes(R.drawable.selecting_start_marker))
            googleMap?.addMarker(markerOptions)
            binding.endDestinationGroup.visibility = View.VISIBLE
        }

        latlngEnd?.let { latlngEnd ->
            binding.markerIV.visibility = View.GONE
            binding.selectBtn.text = getString(R.string.save_changes)
            binding.selectBtn.background = ContextCompat.getDrawable(this , R.drawable.button_main)
            binding.endDestinationET.text = latlngEnd.getAddress(this)
            if (!isPreview)
                binding.endDestinationET.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    R.drawable.edit, 0, 0, 0
                )
            val markerOptions = MarkerOptions()
                .position(latlngEnd)
                .icon(generateBitmapDescriptorFromRes(R.drawable.end_marker))
            googleMap?.addMarker(markerOptions)
            drawRout()
        }
    }


    private fun drawRout() {
        // Define list to get all LatLng for the route
        val path: MutableList<LatLng> = ArrayList()

        // Execute Directions API request
        val context = GeoApiContext.Builder()
            .apiKey(getString(R.string.api_key))
            .build()
        val req = DirectionsApi.getDirections(
            context,
            "${latlngStart?.latitude},${latlngStart?.longitude}",
            "${latlngEnd?.latitude},${latlngEnd?.longitude}"
        )
        try {
            val res = req.await()

            // Loop through legs and steps to get encoded polylines of each step
            if (res.routes != null && res.routes.isNotEmpty()) {
                val route = res.routes[0]

                if (route.legs != null) {
                    for (i in route.legs.indices) {
                        val leg = route.legs[i]
                        if (leg.steps != null) {
                            for (j in leg.steps.indices) {
                                val step = leg.steps[j]
                                if (step.steps != null && step.steps.isNotEmpty()) {
                                    for (k in step.steps.indices) {
                                        val step1 = step.steps[k]
                                        val points1 = step1.polyline
                                        if (points1 != null) {
                                            // Decode polyline and add points to list of route coordinates
                                            val coords1 = points1.decodePath()
                                            for (coord1 in coords1) {
                                                path.add(LatLng(coord1.lat, coord1.lng))
                                            }
                                        }
                                    }
                                } else {
                                    val points = step.polyline
                                    if (points != null) {
                                        // Decode polyline and add points to list of route coordinates
                                        val coords = points.decodePath()
                                        for (coord in coords) {
                                            path.add(LatLng(coord.lat, coord.lng))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        // Draw the polyline
        if (path.isNotEmpty()) {
            val opts = PolylineOptions().addAll(path).color(Color.parseColor("#006E85")).width(10f)
            googleMap?.addPolyline(opts)
            val bounds = LatLngBounds.builder()
                .include(latlngStart!!)
                .include(latlngEnd!!).build()
            googleMap?.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 80))
        }


    }

    private fun generateBitmapDescriptorFromRes(resId: Int): BitmapDescriptor {
        val drawable = ContextCompat.getDrawable(this, resId)
        drawable!!.setBounds(
            0,
            0,
            drawable.intrinsicWidth,
            drawable.intrinsicHeight
        )
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    private fun trackingDriver() {
        if (order?.status == startKey) {
            val database = FirebaseDatabase.getInstance().getReference(trackingTable)
            database.child(order?.id ?: "").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (dataSnapshotChild in dataSnapshot.children) {
                        val data = dataSnapshotChild.getValue(TrackingLocation::class.java)
                        if (data != null) {
                            if (marker == null) {
                                val markerOptions = MarkerOptions()
                                    .position(LatLng(data.lat ?: 0.0, data.lng ?: 0.0))
                                    .icon(generateBitmapDescriptorFromRes(R.drawable.car))
                                marker = googleMap?.addMarker(markerOptions)
                            } else {
                                marker?.position = LatLng(data.lat ?: 0.0, data.lng ?: 0.0)
                            }
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    databaseError.toException().printStackTrace()
                }
            })
        }
    }
}