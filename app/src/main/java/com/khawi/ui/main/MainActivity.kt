package com.khawi.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable
import com.khawi.R
import com.khawi.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var navController: NavController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_main) as NavHostFragment
        navController = navHostFragment.navController

        binding.bottomView.itemIconTintList = null

        navController?.addOnDestinationChangedListener { _, destination, _ ->
            binding.bottomView.visibility =
                if (destination.id == R.id.home
                    || destination.id == R.id.orders
                    || destination.id == R.id.notifications
                    || destination.id == R.id.settings
                )
                    View.VISIBLE
                else
                    View.GONE
        }

        navController?.let {
            binding.bottomView.setupWithNavController(it)
        }

        val bottomNavigationViewBackground = binding.bottomView.background as MaterialShapeDrawable
        bottomNavigationViewBackground.shapeAppearanceModel =
            bottomNavigationViewBackground.shapeAppearanceModel.toBuilder()
                .setTopRightCorner(CornerFamily.ROUNDED, 16f)
                .setTopLeftCorner(CornerFamily.ROUNDED, 16f)
                .build()
    }
}