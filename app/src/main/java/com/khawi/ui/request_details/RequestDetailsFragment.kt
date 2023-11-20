package com.khawi.ui.request_details

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.kaopiz.kprogresshud.KProgressHUD
import com.khawi.R
import com.khawi.base.acceptOfferKey
import com.khawi.base.acceptedKey
import com.khawi.base.addOfferKey
import com.khawi.base.cancelByDriverKey
import com.khawi.base.cancelByUserKey
import com.khawi.base.finishKey
import com.khawi.base.finishedKey
import com.khawi.base.formatDate
import com.khawi.base.hideDialog
import com.khawi.base.initLoading
import com.khawi.base.loadImage
import com.khawi.base.newKey
import com.khawi.base.ratedKey
import com.khawi.base.showDialog
import com.khawi.base.startKey
import com.khawi.base.startTrackingService
import com.khawi.base.stopTrackingService
import com.khawi.databinding.FragmentRequestDetailsBinding
import com.khawi.model.Day
import com.khawi.model.Order
import com.khawi.model.db.user.UserModel
import com.khawi.ui.select_destination.SelectDestinationActivity
import com.willy.ratingbar.ScaleRatingBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class RequestDetailsFragment : Fragment() {

    private var _binding: FragmentRequestDetailsBinding? = null
    private val binding get() = _binding!!
    private val listDays = mutableListOf<Day>()
    private val args: RequestDetailsFragmentArgs by navArgs()
    private var order: Order? = null
    private var user: UserModel? = null

    private var loading: KProgressHUD? = null
    private val viewModel: RequestDetailsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRequestDetailsBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.userMutableLiveData.observe(viewLifecycleOwner) {
            it?.let {
                user = it
            }
        }

        loading = requireActivity().initLoading()
        viewModel.progressLiveData.observe(viewLifecycleOwner) {
            if (it) loading?.showDialog()
            else loading?.hideDialog()
        }
        viewModel.successLiveData.observe(viewLifecycleOwner) {
            if (it?.status == true) {
                if (it.data != null && it.data?.id != null) {
                    order = it.data
                    fillInfo()
                } else {
                    viewModel.viewModelScope.launch {
                        viewModel.getOrders(args.orderObj?.id ?: "")
                    }
                }
            }
        }

        viewModel.viewModelScope.launch {
            viewModel.getOrders(args.orderObj?.id ?: "")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun fillInfo() {
        binding.groupDays.visibility = View.GONE
        if (order?.days?.isNotEmpty() == true) {
            binding.groupDays.visibility = View.VISIBLE
            listDays.clear()
            listDays.add(Day(name = getString(R.string.saturday), select = false))
            listDays.add(Day(name = getString(R.string.sunday), select = false))
            listDays.add(Day(name = getString(R.string.monday), select = false))
            listDays.add(Day(name = getString(R.string.tuesday), select = false))
            listDays.add(Day(name = getString(R.string.wednesday), select = false))
            listDays.add(Day(name = getString(R.string.thursday), select = false))
            listDays.add(Day(name = getString(R.string.friday), select = false))
            for (value in listDays) {
                for (valueInner in order?.days!!) {
                    if (valueInner.contains(value.name ?: ""))
                        value.select = true
                }
            }
            val adapter = DaysAdapter(requireContext()) { _, _ ->

            }
            adapter.items = listDays
            binding.recyclerViewDays.adapter = adapter
        }

        binding.tripDateTop.text = order?.dtDate?.formatDate() ?: ""
        binding.tripName.text = "${getString(R.string.trip_title)}: ${order?.title ?: ""}"
        binding.tripInformation.text =
            "${getString(R.string.from)}: ${order?.fAddress}\n${getString(R.string.to)}: ${order?.tAddress}"
        binding.tripTime.text = order?.dtTime ?: ""
        binding.tripDate.text = order?.dtDate?.formatDate() ?: ""

        binding.back.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.showMap.setOnClickListener {
            startActivity(
                Intent(requireContext(), SelectDestinationActivity::class.java)
                    .putExtra(SelectDestinationActivity.orderKey, order)
                    .putExtra(SelectDestinationActivity.isPreviewKey, true)
                    .putExtra(
                        SelectDestinationActivity.latLongStartKey, LatLng(
                            order?.fLat ?: 0.0,
                            order?.fLng ?: 0.0,
                        )
                    )
                    .putExtra(
                        SelectDestinationActivity.latLongEndKey, LatLng(
                            order?.tLat ?: 0.0,
                            order?.tLng ?: 0.0,
                        )
                    )
            )
        }
        binding.sendBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.joinContainer.visibility = View.GONE
        binding.deliverContainer.visibility = View.GONE
        if (order?.orderType == 2) {
            binding.sendBtn.text = getString(R.string.apply_deliver)
            binding.deliverContainer.visibility = View.VISIBLE
            binding.sendBtn.setOnClickListener {
                findNavController().navigate(
                    RequestDetailsFragmentDirections.actionRequestDetailsFragmentToRequestDeliverFragment(
                        orderObj = order
                    )
                )
            }

            binding.personImage.loadImage(requireContext(), order?.user?.image ?: "")
            binding.personName.text = order?.user?.fullName ?: ""
            binding.seatRequest.text = "${order?.maxPassenger ?: 0} ${getString(R.string.seats)}"
            binding.price.text = "${order?.price ?: ""} ${getString(R.string.currancy)}"
            binding.note.text = order?.notes ?: ""
            if ((order?.notes ?: "").isEmpty())
                binding.noteGroup.visibility = View.GONE

            binding.callClient.visibility = View.GONE
            if (order?.status == startKey || order?.status == acceptedKey)
                binding.callClient.visibility = View.VISIBLE

            binding.callClient.setOnClickListener {
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${user?.phoneNumber}"))
                startActivity(intent)
            }

        } else if (order?.orderType == 1) {
            binding.sendBtn.text = getString(R.string.join_now)
            binding.sendBtn.setOnClickListener {
                findNavController().navigate(
                    RequestDetailsFragmentDirections.actionRequestDetailsFragmentToRequestJoinFragment(
                        orderObj = order
                    )
                )
            }
            showDriverInfo(
                order?.user,
                "${order?.maxPassenger ?: 0} ${getString(R.string.seats)}",
                order?.notes ?: "",
                "(${order?.minPrice ?: ""} - ${order?.maxPrice ?: ""}) ${getString(R.string.currancy)}"
            )
        }

        binding.priceOfferContainer.visibility = View.GONE
        val offersAccepted = order?.offers?.filter { it.status == acceptOfferKey }
        if (offersAccepted?.isNotEmpty() == true) {
            val myOffer = offersAccepted[0]
            if ((myOffer.user?.id ?: "") == (user?.id ?: "")) {
                binding.priceOfferContainer.visibility = View.VISIBLE
                binding.priceOffer.text = "${myOffer.price} ${getString(R.string.currancy)}"
            }
        }

        binding.edit.visibility = View.GONE
        binding.orderStatus.visibility = View.GONE
        binding.requestsContainer.visibility = View.GONE
        binding.rateUser.visibility = View.GONE
        binding.rateDriver.visibility = View.GONE
        binding.startBtn.visibility = View.GONE
        binding.sendBtn.visibility = View.VISIBLE
        if (args.isOrder) {
            binding.sendBtn.visibility = View.GONE
            if (canCancel()) {
                binding.sendBtn.visibility = View.VISIBLE
                binding.sendBtn.text = getString(R.string.cancel_trip)
                binding.sendBtn.setOnClickListener {
                    cancelBottomSheet()
                }
            }
            if (endTripStatus()) {
                binding.sendBtn.visibility = View.VISIBLE
                binding.sendBtn.text = getString(R.string.end_trip)
                binding.sendBtn.setOnClickListener {
                    requireActivity().stopTrackingService()
                    viewModel.viewModelScope.launch {
                        viewModel.changeOrderStatusBody(
                            orderId = order?.id ?: "",
                            status = finishKey
                        )
                    }
                }
            }
            if (newStatusWithOfferAccepted()) {
                binding.startBtn.visibility = View.VISIBLE
                binding.startBtn.setOnClickListener {
                    requireActivity().startTrackingService(order)
                    viewModel.viewModelScope.launch {
                        viewModel.changeOrderStatusBody(
                            orderId = order?.id ?: "",
                            status = startKey
                        )
                    }
                }
            }

            binding.orderStatus.visibility = View.VISIBLE
            when (order?.status) {
                ratedKey -> {
                    binding.orderStatus.text = getString(R.string.finished)
                    binding.orderStatus.background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.bg_green_corner_all_12r
                    )
                    binding.orderStatus.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.green2
                        )
                    )
                }

                finishedKey -> {
                    binding.orderStatus.text = getString(R.string.finished)
                    binding.orderStatus.background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.bg_green_corner_all_12r
                    )
                    binding.orderStatus.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.green2
                        )
                    )
                }

                cancelByUserKey -> {
                    binding.orderStatus.text = getString(R.string.cancelled)
                    binding.orderStatus.background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.bg_red_corner_all_12r
                    )
                    binding.orderStatus.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.red2
                        )
                    )
                }

                cancelByDriverKey -> {
                    binding.orderStatus.text = getString(R.string.cancelled)
                    binding.orderStatus.background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.bg_red_corner_all_12r
                    )
                    binding.orderStatus.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.red2
                        )
                    )
                }

                acceptedKey -> {
                    binding.orderStatus.text = getString(R.string.open_order)
                    binding.orderStatus.background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.bg_blue_corner_all_12r
                    )
                    binding.orderStatus.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.blue
                        )
                    )
                }

                else -> {
                    binding.orderStatus.text = getString(R.string.new_order)
                    binding.orderStatus.background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.bg_blue_corner_all_12r
                    )
                    binding.orderStatus.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.blue
                        )
                    )
                }
            }


            if (order?.status == finishedKey && ((order?.user?.id ?: "") != (user?.id ?: ""))) {
//                binding.rateUser.visibility = View.VISIBLE
                binding.rateDriver.visibility = View.VISIBLE
            }

            if ((order?.user?.id ?: "") == (user?.id ?: "")) {
                order?.offers?.let {
                    if (it.isNotEmpty()) {
                        val offers = it.filter { it.status == addOfferKey }.toMutableList()
                        val offersAcceptedList =
                            it.filter { it.status == acceptOfferKey }.toMutableList()

                        if (offers.isNotEmpty()) {
                            binding.requestsContainer.visibility = View.VISIBLE
                            binding.requestsTitle.text =
                                if (order?.orderType == 2)
                                    getString(R.string.requests_delivery)
                                else
                                    getString(R.string.requests_join)
                            val adapterUserRequest =
                                UserRequestAdapter(
                                    requireContext(),
                                    false
                                ) { item, _, type ->
                                    if (type == UserRequestAdapter.ClickType.OPEN) {
                                        findNavController().navigate(
                                            RequestDetailsFragmentDirections.actionRequestDetailsFragmentToJoinDetailsFragment(
                                                orderObj = order,
                                                joinObj = item,
                                                isOfferDeliver = order?.orderType == 2
                                            )
                                        )
                                    }
                                }
                            adapterUserRequest.items = offers
                            binding.recyclerViewRequests.adapter = adapterUserRequest
                        }

                        if (offersAcceptedList.isNotEmpty()) {
                            if (order?.orderType == 1) {
//                                val isOrderFinished =
//                                    (order?.status == finishedKey && order?.orderType == 2)
                                binding.requestsAcceptedContainer.visibility = View.VISIBLE
                                val adapterUserRequestAccepted =
                                    UserRequestAdapter(
                                        requireContext(),
                                        false
                                    ) { item, _, type ->
                                        if (type == UserRequestAdapter.ClickType.OPEN) {
                                            findNavController().navigate(
                                                RequestDetailsFragmentDirections.actionRequestDetailsFragmentToJoinDetailsFragment(
                                                    orderObj = order,
                                                    joinObj = item,
                                                    isOfferDeliver = order?.orderType == 2
                                                )
                                            )
                                        } else if (type == UserRequestAdapter.ClickType.RATE) {
                                            rateBottomSheet(item.user)
                                        }
                                    }
                                adapterUserRequestAccepted.items = offersAcceptedList
                                binding.recyclerViewRequestsAccepted.adapter =
                                    adapterUserRequestAccepted
                            } else {
                                binding.requestsContainer.visibility = View.GONE
                                binding.requestsAcceptedContainer.visibility = View.GONE
                                showDriverInfo(
                                    offersAcceptedList[0].user,
                                    "${order?.maxPassenger ?: 0} ${getString(R.string.seats)}",
                                    offersAcceptedList[0].notes ?: "",
                                    "${offersAcceptedList[0].price ?: ""} ${getString(R.string.currancy)}"
                                )
                            }
                        }
                    }
                }
            }
        }

        binding.rateUser.setOnClickListener {
            rateBottomSheet(order?.user)
        }
        binding.rateDriver.setOnClickListener {
            rateBottomSheet(order?.user)
        }
    }

    private fun showDriverInfo(
        user: UserModel?,
        seatsText: String,
        noteText: String,
        priceText: String
    ) {
        binding.joinContainer.visibility = View.VISIBLE
        binding.userImage.loadImage(requireContext(), user?.image ?: "")
        binding.username.text = user?.fullName ?: ""
        binding.ratingBar.rating = if (user?.rate?.isNotEmpty() == true)
            user.rate.toFloat()
        else 0f

        binding.carType.text = user?.carType ?: ""
        binding.carModel.text = user?.carModel ?: ""
        binding.carColor.text = user?.carColor ?: ""
        binding.carPlate.text = user?.carNumber ?: ""

        binding.seat.text = seatsText
        binding.driverNote.text = noteText
        if (noteText.isEmpty())
            binding.driverNoteGroup.visibility = View.GONE
        binding.price.text = priceText

        binding.callDriver.visibility = View.GONE
        if (order?.status == startKey || order?.status == acceptedKey)
            binding.callDriver.visibility = View.VISIBLE
        binding.callDriver.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${user?.phoneNumber}"))
            startActivity(intent)
        }
    }

    private fun cancelBottomSheet() {
        val bottomSheet = BottomSheetDialog(requireContext(), R.style.AppBottomSheetDialogTheme)
        val rootView =
            layoutInflater.inflate(R.layout.bottomsheet_cancel, binding.container, false)
        bottomSheet.setContentView(rootView)

        val reasonETRate = rootView.findViewById<EditText>(R.id.reasonETRate)

        val sendCancelBtn = rootView.findViewById<TextView>(R.id.sendCancelBtn)

        sendCancelBtn.setOnClickListener {
            viewModel.viewModelScope.launch {
                viewModel.changeOrderStatusBody(
                    order?.id ?: "",
                    if (order?.orderType == 1 && ((order?.user?.id ?: "") == (user?.id ?: "")))
                        cancelByDriverKey
                    else
                        cancelByUserKey,
                    reasonETRate.text.toString()
                )
            }
            bottomSheet.dismiss()
        }

        bottomSheet.show()
    }

    private fun rateBottomSheet(userModel: UserModel?) {
        val bottomSheet = BottomSheetDialog(requireContext(), R.style.AppBottomSheetDialogTheme)
        val rootView =
            layoutInflater.inflate(R.layout.bottomsheet_rate, binding.container, false)
        bottomSheet.setContentView(rootView)

        val userImageRate = rootView.findViewById<ImageView>(R.id.userImageRate)
        val usernameRate = rootView.findViewById<TextView>(R.id.usernameRate)
        val ratingBarRate = rootView.findViewById<ScaleRatingBar>(R.id.ratingBarRate)
        val noteETRate = rootView.findViewById<EditText>(R.id.noteETRate)

        userImageRate.loadImage(requireContext(), userModel?.image ?: "")
        usernameRate.text = userModel?.fullName ?: ""

        val rateBtn = rootView.findViewById<TextView>(R.id.rateBtn)

        rateBtn.setOnClickListener {
            viewModel.viewModelScope.launch {
                viewModel.addRate(
                    order?.id ?: "",
                    ratingBarRate.rating.toString(),
                    noteETRate.text.toString()
                )
            }
            bottomSheet.dismiss()
        }

        bottomSheet.show()
    }

    private fun canCancel(): Boolean {
        return ((order?.user?.id ?: "") == (user?.id ?: "") && order?.status == newKey)
                || (order?.status == acceptedKey)
    }

    private fun endTripStatus(): Boolean {
        return order?.status == startKey
    }

    private fun newStatusWithOfferAccepted(): Boolean {
        val offersAccepted = order?.offers?.filter { it.status == acceptOfferKey }
        return if (order?.orderType == 2 && offersAccepted?.isNotEmpty() == true && order?.status == acceptedKey) {
            ((offersAccepted[0].user?.id ?: "") == (user?.id ?: ""))
        } else if (order?.orderType == 1 && offersAccepted?.isNotEmpty() == true && order?.status == newKey) {
            ((order?.user?.id ?: "") == (user?.id ?: ""))
        } else
            false
    }
}