package com.khawi.ui.select_destination

import android.os.Bundle
import android.view.View
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.khawi.R
import com.khawi.base.BaseActivity
import com.khawi.databinding.ActivitySelectDestinationBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SelectDestinationActivity : BaseActivity() {
    private lateinit var binding: ActivitySelectDestinationBinding
    private var step = 0
    private var googleMap: GoogleMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectDestinationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        MapsInitializer.initialize(applicationContext)
        binding.back.setOnClickListener {
            finish()
        }
        binding.yourLocation.setOnClickListener {

        }

        binding.endDestinationGroup.visibility = View.GONE

        binding.startDestinationET.setCompoundDrawablesRelativeWithIntrinsicBounds(
            0, 0, 0, 0
        )
        binding.endDestinationET.setCompoundDrawablesRelativeWithIntrinsicBounds(
            0, 0, 0, 0
        )

        binding.markerIV.setImageResource(R.drawable.selecting_start_marker)
        binding.selectBtn.text = getString(R.string.select_start)

        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.mapContainer) as? SupportMapFragment
        mapFragment?.getMapAsync {
            googleMap = it
        }
//"https://maps.googleapis.com/maps/api/geocode/json?latlng=44.4647452,7.3553838&key=AIzaSyDimezKyWNvM-1jEsjfQHvhYW3oBnaf67c"
        binding.selectBtn.setOnClickListener {
            if (step == 1) {
                finish()
            } else {
                step = 1
                binding.markerIV.setImageResource(R.drawable.end_marker)
                binding.selectBtn.text = getString(R.string.select_end)
                binding.startDestinationET.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    R.drawable.edit, 0, 0, 0
                )

                val markerOptions = MarkerOptions()
                    .position(googleMap?.cameraPosition?.target ?: LatLng(0.0, 0.0))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.selected_start_marker))

                googleMap?.addMarker(markerOptions)
                binding.startDestinationET.setText("")
                binding.endDestinationGroup.visibility = View.VISIBLE
            }
        }
    }
}