package com.khawi.ui.main.home

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.birjuvachhani.locus.Locus
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.khawi.R
import com.khawi.base.formatDate
import com.khawi.base.loadImage
import com.khawi.databinding.FragmentHomeBinding
import com.khawi.model.Order
import com.khawi.model.db.user.UserModel
import com.willy.ratingbar.ScaleRatingBar
import dagger.hilt.android.AndroidEntryPoint
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var list = mutableListOf<Order>()
    private val viewModel: HomeViewModel by viewModels()
    private var isFirst = true
    private var latitude = 0.0
    private var longitude = 0.0
    private var user: UserModel? = null

    //    private var selectedMarker: Marker? = null
    private var googleMap: GoogleMap? = null

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
        Locus.getCurrentLocation(requireContext()) { result ->
            result.location?.let {
                if (isFirst) {
                    latitude = it.latitude
                    longitude = it.longitude
                    handleMap()
                }
            }
            result.error?.let { }
        }

        viewModel.userMutableLiveData.observe(viewLifecycleOwner) {
            it?.let {
                user = it
                binding.userImg.loadImage(requireContext(), user?.image?:"")
                binding.username.text = "${user?.fullName} .."
            }
        }


        viewModel.successLiveData.observe(viewLifecycleOwner) {
            it?.let { response ->
                if (response.status == true) {
                    response.data?.let { data ->
                        list = data
                        handleMap()
                    }
                }

            }
        }

        binding.yourLocation.setOnClickListener {
            googleMap?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(latitude, longitude),
                    15f
                )
            )
        }
    }

    private fun handleMap() {
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
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
        isCarType: Boolean
    ): Bitmap {
//        val carSelected = R.drawable.car_marker_unselect
        val carSelected = R.drawable.car_marker
//        val joinSelected = R.drawable.join_marker_unselect
        val joinSelected = R.drawable.join_marker
        val markerIV = view.findViewById<ImageView>(R.id.markerIV)
        markerIV.setImageResource(
            if (isCarType) {
                carSelected
//                if (isSelected) carSelected else carUnselected
            } else {
                joinSelected
//                if (isSelected) joinSelected else joinUnselected
            }
        )

        return getMarkerBitmapFromView(view)
    }

    private fun deliverBottomSheet(order: Order) {
        val bottomSheet = BottomSheetDialog(requireContext())
        val rootView =
            layoutInflater.inflate(R.layout.bottomsheet_deliver_form, binding.container, false)
        bottomSheet.setContentView(rootView)

        val userImage = rootView.findViewById<CircleImageView>(R.id.userImage)
        val username = rootView.findViewById<TextView>(R.id.username)
        val ratingBar = rootView.findViewById<ScaleRatingBar>(R.id.ratingBar)

        val ownerUser = order.user
        userImage.loadImage(requireContext(), ownerUser?.image ?: "")
        username.text = ownerUser?.fullName ?: ""
        ratingBar.rating = if (ownerUser?.rate?.isNotEmpty() == true)
            ownerUser.rate.toFloat()
        else
            0f

        val carType = rootView.findViewById<TextView>(R.id.carType)
        val carModel = rootView.findViewById<TextView>(R.id.carModel)
        val carColor = rootView.findViewById<TextView>(R.id.carColor)
        val carPlate = rootView.findViewById<TextView>(R.id.carPlate)

        carType.text = ownerUser?.carType ?: ""
        carModel.text = ownerUser?.carModel ?: ""
        carColor.text = ownerUser?.carColor ?: ""
        carPlate.text = ownerUser?.carNumber ?: ""

        val showDetails = rootView.findViewById<TextView>(R.id.showDetails)
        showDetails.setOnClickListener {
            findNavController().navigate(
                HomeFragmentDirections.actionHomeToRequestDetailsFragment(
                    isDeliver = order.orderType == 2,
                    orderObj = order,
                    isOrder = order.user?.id == user?.id
                )
            )
            bottomSheet.dismiss()
        }

        bottomSheet.show()
    }

    private fun joinBottomSheet(order: Order) {
        val bottomSheet = BottomSheetDialog(requireContext())
        val rootView =
            layoutInflater.inflate(R.layout.bottomsheet_join_form, binding.container, false)
        bottomSheet.setContentView(rootView)

        val ownerUser = order.user

        val userImage = rootView.findViewById<CircleImageView>(R.id.userImage)
        val username = rootView.findViewById<TextView>(R.id.username)

        userImage.loadImage(requireContext(), ownerUser?.image ?: "")
        username.text = ownerUser?.fullName ?: ""

        val tripInformation = rootView.findViewById<TextView>(R.id.tripInformation)
        val tripTime = rootView.findViewById<TextView>(R.id.tripTime)
        val tripDate = rootView.findViewById<TextView>(R.id.tripDate)
        tripInformation.text =
            "${getString(R.string.from)}: ${order.fAddress}\n${getString(R.string.to)}: ${order.tAddress}"
        tripTime.text = order.dtTime ?: ""
        tripDate.text = order.dtDate?.formatDate() ?: ""

        val showDetails = rootView.findViewById<TextView>(R.id.showDetails)
        showDetails.setOnClickListener {
            findNavController().navigate(
                HomeFragmentDirections.actionHomeToRequestDetailsFragment(
                    isDeliver = order.orderType == 2,
                    orderObj = order,
                    isOrder = order.user?.id == user?.id
                )
            )
            bottomSheet.dismiss()
        }

        bottomSheet.show()
    }

    override fun onMapReady(mMap: GoogleMap) {
        googleMap = mMap
        if (isFirst) {
            googleMap?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(latitude, longitude),
                    15f
                )
            )
            isFirst = false
        }

        googleMap?.uiSettings?.isCompassEnabled = true
        googleMap?.uiSettings?.isZoomGesturesEnabled = true
        googleMap?.uiSettings?.isZoomControlsEnabled = true
        googleMap?.uiSettings?.isRotateGesturesEnabled = false
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        googleMap?.isMyLocationEnabled = true
        googleMap?.clear()


        for (dataObject in list) {
            val position = LatLng(dataObject.fLat ?: 0.0, dataObject.fLng ?: 0.0)

            val markerView = LayoutInflater.from(requireContext())
                .inflate(R.layout.marker_custom, null as ViewGroup?)

            val markerOptions = MarkerOptions()
                .position(position)
                .icon(
                    BitmapDescriptorFactory.fromBitmap(
                        getSelectedMarkerBitmapFromView(
                            markerView,
                            dataObject.orderType == 1
                        )
                    )
                )

            val marker = googleMap?.addMarker(markerOptions)
            marker?.tag = dataObject

            googleMap?.setOnMarkerClickListener { clickedMarker ->
                val clickedOrder = (clickedMarker.tag as Order)
                if (clickedOrder.orderType == 1) {
                    deliverBottomSheet(clickedOrder)
                } else {
                    joinBottomSheet(clickedOrder)
                }
//                if (selectedMarker != null
//                    && ((selectedMarker?.tag as Order).id == clickedOrder.id)
//                ) {
//                    clickedMarker.setIcon(
//                        BitmapDescriptorFactory.fromBitmap(
//                            getSelectedMarkerBitmapFromView(
//                                markerView,
//                                (clickedOrder.orderType == 1)
//                            )
//                        )
//                    )
//                    selectedMarker = null
//                } else {
//                    selectedMarker = clickedMarker
//                    clickedMarker.setIcon(
//                        BitmapDescriptorFactory.fromBitmap(
//                            getSelectedMarkerBitmapFromView(
//                                markerView,
//                                (clickedOrder.orderType == 1)
//                            )
//                        )
//                    )
//                    if (clickedOrder.orderType == 1) {
//                        deliverBottomSheet(clickedOrder)
//                    } else {
//                        joinBottomSheet(clickedOrder)
//                    }
//
//                }

                true
            }
        }

        googleMap?.setOnCameraIdleListener {
            Handler(Looper.getMainLooper()).postDelayed({}, 2000)
            val latlng = googleMap?.cameraPosition?.target
            latlng?.let {
                viewModel.viewModelScope.launch {
                    viewModel.getOrders(it.latitude.toString(), it.longitude.toString())
                }
            }
        }
    }
}