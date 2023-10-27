package com.khawi.ui.select_destination

import android.os.Bundle
import android.view.View
import com.google.android.gms.maps.SupportMapFragment
import com.khawi.R
import com.khawi.base.BaseActivity
import com.khawi.databinding.ActivitySelectDestinationBinding
import com.khawi.databinding.ActivitySplashBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SelectDestinationActivity : BaseActivity() {
    private lateinit var binding: ActivitySelectDestinationBinding
    private var step = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectDestinationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.back.setOnClickListener {
            finish()
        }
        binding.yourLocation.setOnClickListener {

        }

        binding.endDestinationGroup.visibility = View.VISIBLE

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

        }

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
            }
        }
    }
}