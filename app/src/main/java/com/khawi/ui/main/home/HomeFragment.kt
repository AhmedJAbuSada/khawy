package com.khawi.ui.main.home

import android.Manifest
import android.app.Activity
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
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.database.FirebaseDatabase
import com.khawi.R
import com.khawi.base.checkTime
import com.khawi.base.formatDate
import com.khawi.base.loadImage
import com.khawi.base.safeNavigate
import com.khawi.base.userLocationTable
import com.khawi.databinding.FragmentHomeBinding
import com.khawi.model.Order
import com.khawi.model.UserLocation
import com.khawi.model.db.user.UserModel
import com.willy.ratingbar.ScaleRatingBar
import dagger.hilt.android.AndroidEntryPoint
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.launch
import java.util.Locale

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

    private val startAutocomplete =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                if (intent != null) {
                    val place = Autocomplete.getPlaceFromIntent(intent)
                    if (place.latLng != null)
                        binding.searchET.text = place.address
                    if (binding.searchET.text.toString().isNotEmpty()) {
                        binding.clearSearchBtn.visibility = View.VISIBLE
                        googleMap?.animateCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                LatLng(
                                    place.latLng?.latitude ?: 0.0,
                                    place.latLng?.longitude ?: 0.0
                                ),
                                15f
                            )
                        )
                        getOrders()
                    }
                }
            }
        }

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
        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), getString(R.string.api_key), Locale.ENGLISH);
        }
        Locus.getCurrentLocation(requireContext()) { result ->
            result.location?.let {
                if (isFirst) {
                    latitude = it.latitude
                    longitude = it.longitude
                    addUserLocation()
                    handleMap()
                }
            }
            result.error?.let { }
        }

        viewModel.userMutableLiveData.observe(viewLifecycleOwner) {
            it?.let {
                user = it
                binding.userImg.loadImage(user?.image ?: "")
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

        binding.searchET.setOnClickListener {
            searchPlace()
        }

        binding.clearSearchBtn.setOnClickListener {
            binding.searchET.text = ""
            binding.clearSearchBtn.visibility = View.GONE
            getOrders()
        }
    }

    private fun searchPlace() {
        val fields = listOf(
            Place.Field.ID,
            Place.Field.NAME,
            Place.Field.ADDRESS,
            Place.Field.ADDRESS_COMPONENTS,
            Place.Field.LAT_LNG,
            Place.Field.TYPES
        )
        // Start the autocomplete intent.
        val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
            .build(requireContext())
        startAutocomplete.launch(intent)
    }

    private fun addUserLocation() {
        val database = FirebaseDatabase.getInstance().getReference(userLocationTable)
        database.child(user?.id ?: "").setValue(
            UserLocation(
                driverName = user?.fullName,
                driverPhone = user?.phoneNumber,
                g = generateRandomString(),
                l = mutableListOf(latitude, longitude)
            )
        )
    }

    private fun generateRandomString(): String {
        val charset = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        return (1..10)
            .map { charset.random() }
            .joinToString("")
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
        val bottomSheet = BottomSheetDialog(requireContext(), R.style.AppBottomSheetDialogTheme)
        val rootView =
            layoutInflater.inflate(R.layout.bottomsheet_deliver_form, binding.container, false)
        bottomSheet.setContentView(rootView)

        val userImage = rootView.findViewById<CircleImageView>(R.id.userImage)
        val username = rootView.findViewById<TextView>(R.id.username)
        val ratingBar = rootView.findViewById<ScaleRatingBar>(R.id.ratingBar)

        val ownerUser = order.user
        userImage.loadImage(ownerUser?.image ?: "")
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
            findNavController().safeNavigate(
                HomeFragmentDirections.actionHomeToRequestDetailsFragment(
                    orderObj = order,
                    isOrder = order.user?.id == user?.id
                )
            )
            bottomSheet.dismiss()
        }

        bottomSheet.show()
    }

    private fun joinBottomSheet(order: Order) {
        val bottomSheet = BottomSheetDialog(requireContext(), R.style.AppBottomSheetDialogTheme)
        val rootView =
            layoutInflater.inflate(R.layout.bottomsheet_join_form, binding.container, false)
        bottomSheet.setContentView(rootView)

        val ownerUser = order.user

        val userImage = rootView.findViewById<CircleImageView>(R.id.userImage)
        val username = rootView.findViewById<TextView>(R.id.username)

        userImage.loadImage(ownerUser?.image ?: "")
        username.text = ownerUser?.fullName ?: ""

        val tripInformation = rootView.findViewById<TextView>(R.id.tripInformation)
        val tripTime = rootView.findViewById<TextView>(R.id.tripTime)
        val tripTimeZone = rootView.findViewById<TextView>(R.id.tripTimeZone)
        val tripDate = rootView.findViewById<TextView>(R.id.tripDate)
        tripInformation.text =
            "${getString(R.string.from)}: ${order.fAddress}\n${getString(R.string.to)}: ${order.tAddress}"
        tripTime.text = (order.dtTime ?: "").checkTime(tripTimeZone)
//            if ((order.dtTime ?: "").contains("م")) {
//                tripTimeZone.text = "م"
//                (order.dtTime ?: "").replace("م", "")
//            } else if ((order.dtTime ?: "").contains("ص")) {
//                tripTimeZone.text = "ص"
//                (order.dtTime ?: "").replace("ص", "")
//            } else {
//                order.dtTime ?: ""
//            }

        tripDate.text = order.dtDate?.formatDate() ?: ""

        val showDetails = rootView.findViewById<TextView>(R.id.showDetails)
        showDetails.setOnClickListener {
            findNavController().safeNavigate(
                HomeFragmentDirections.actionHomeToRequestDetailsFragment(
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
                            dataObject.orderType == 2
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
            getOrders()
        }
    }

    override fun onResume() {
        super.onResume()
        if (_binding != null)
            getOrders()
    }

    private fun getOrders() {
        val latlng = googleMap?.cameraPosition?.target
//        val address = if (binding.searchET.text.toString().isNotEmpty())
//            binding.searchET.text.toString()
//        else
//            null
        latlng?.let {
            viewModel.viewModelScope.launch {
                viewModel.getOrders(it.latitude.toString(), it.longitude.toString(), null)
            }
        }
    }
}