package com.khawi.ui.main.home

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.khawi.R
import com.khawi.databinding.FragmentHomeBinding
import com.willy.ratingbar.ScaleRatingBar
import dagger.hilt.android.AndroidEntryPoint
import de.hdodenhof.circleimageview.CircleImageView

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val markerStates: MutableMap<Marker, Boolean> = HashMap()
    private var list = mutableListOf<LatLng>()
//    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment =
            childFragmentManager.findFragmentById(R.id.mapContainer) as? SupportMapFragment
        mapFragment?.getMapAsync {
            for ((index, dataObject) in list.withIndex()) {
                val position = LatLng(dataObject.latitude, dataObject.longitude)

                val markerView = LayoutInflater.from(requireContext())
                    .inflate(R.layout.marker_custom, null as ViewGroup?)
//                val markerIV = markerView.findViewById<ImageView>(R.id.markerIV)
//                markerIV.setImageResource(
//                    if (index % 2 == 0)
//                        R.drawable.car_marker
//                    else R.drawable.join_marker
//                )

                val markerOptions = MarkerOptions()
                    .position(position)
                    .icon(
                        BitmapDescriptorFactory.fromBitmap(
                            getSelectedMarkerBitmapFromView(
                                markerView,
                                false,
                                index % 2 == 0
                            )
                        )
                    )

                val marker = it.addMarker(markerOptions)
                marker?.tag = index

                marker?.let { markerIt ->
                    markerStates[markerIt] = false
                }

                it.setOnMarkerClickListener { clickedMarker ->
                    val currentState = markerStates[clickedMarker] ?: false

                    if (currentState) {
                        markerStates[clickedMarker] = false
                        clickedMarker.setIcon(
                            BitmapDescriptorFactory.fromBitmap(
                                getSelectedMarkerBitmapFromView(
                                    markerView,
                                    false,
                                    ((clickedMarker.tag as Int) % 2 == 0)
                                )
                            )
                        )
                    } else {
                        markerStates[clickedMarker] = true
                        clickedMarker.setIcon(
                            BitmapDescriptorFactory.fromBitmap(
                                getSelectedMarkerBitmapFromView(
                                    markerView,
                                    true,
                                    ((clickedMarker.tag as Int) % 2 == 0)
                                )
                            )
                        )
                        if ((clickedMarker.tag as Int) % 2 == 0) {
                            deliverBottomSheet()
                        } else {
                            joinBottomSheet()
                        }

                    }

                    true
                }
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getMarkerBitmapFromView(view: View): Bitmap {
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
        view.buildDrawingCache()
        val returnedBitmap = Bitmap.createBitmap(
            view.measuredWidth,
            view.measuredHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(returnedBitmap)
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.SRC_IN)
        view.draw(canvas)
        return returnedBitmap
    }

    private fun getSelectedMarkerBitmapFromView(
        view: View,
        isSelected: Boolean,
        isCarType: Boolean
    ): Bitmap {
        val carUnselected = R.drawable.car_marker_unselect
        val carSelected = R.drawable.car_marker
        val joinUnselected = R.drawable.join_marker_unselect
        val joinSelected = R.drawable.join_marker
        val markerIV = view.findViewById<ImageView>(R.id.markerIV)
        markerIV.setImageResource(
            if (isCarType) {
                if (isSelected) carSelected else carUnselected
            } else {
                if (isSelected) joinSelected else joinUnselected
            }
        )

        return getMarkerBitmapFromView(view)
    }

    private fun deliverBottomSheet() {
        val bottomSheet = BottomSheetDialog(requireContext())
        val rootView =
            layoutInflater.inflate(R.layout.bottomsheet_deliver_form, binding.container, false)
        bottomSheet.setContentView(rootView)

        val userImage = rootView.findViewById<CircleImageView>(R.id.userImage)
        val username = rootView.findViewById<TextView>(R.id.username)
        val ratingBar = rootView.findViewById<ScaleRatingBar>(R.id.ratingBar)

        val carType = rootView.findViewById<TextView>(R.id.carType)
        val carModel = rootView.findViewById<TextView>(R.id.carModel)
        val carColor = rootView.findViewById<TextView>(R.id.carColor)
        val carPlate = rootView.findViewById<TextView>(R.id.carPlate)

        val showDetails = rootView.findViewById<TextView>(R.id.showDetails)
        showDetails.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeToRequestFormFragment(isDeliver = false))
            bottomSheet.dismiss()
        }

        bottomSheet.show()
    }

    private fun joinBottomSheet() {
        val bottomSheet = BottomSheetDialog(requireContext())
        val rootView =
            layoutInflater.inflate(R.layout.bottomsheet_join_form, binding.container, false)
        bottomSheet.setContentView(rootView)

        val userImage = rootView.findViewById<CircleImageView>(R.id.userImage)
        val username = rootView.findViewById<TextView>(R.id.username)

        val tripInformation = rootView.findViewById<TextView>(R.id.tripInformation)
        val tripTime = rootView.findViewById<TextView>(R.id.tripTime)
        val tripDate = rootView.findViewById<TextView>(R.id.tripDate)

        val showDetails = rootView.findViewById<TextView>(R.id.showDetails)
        showDetails.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeToRequestFormFragment(isDeliver = true))
            bottomSheet.dismiss()
        }

        bottomSheet.show()
    }
}