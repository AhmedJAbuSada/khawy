package com.khawi.ui.request_details

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.kaopiz.kprogresshud.KProgressHUD
import com.khawi.R
import com.khawi.base.acceptedKey
import com.khawi.base.cancelledKey
import com.khawi.base.finishedKey
import com.khawi.base.formatDate
import com.khawi.base.hideDialog
import com.khawi.base.initLoading
import com.khawi.base.loadImage
import com.khawi.base.showDialog
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
                order = it.data
                fillInfo()
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
            val selectedDays = Gson().fromJson(order?.days!![0], Array<String>::class.java)
            for (value in listDays) {
                for (valueInner in selectedDays) {
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
        if (args.isDeliver) {
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
            binding.note.text = order?.notes ?: ""

        } else {
            binding.sendBtn.text = getString(R.string.join_now)
            binding.joinContainer.visibility = View.VISIBLE
            binding.sendBtn.setOnClickListener {
                findNavController().navigate(
                    RequestDetailsFragmentDirections.actionRequestDetailsFragmentToRequestJoinFragment(
                        orderObj = order
                    )
                )
            }

            binding.userImage.loadImage(requireContext(), order?.user?.image ?: "")
            binding.username.text = order?.user?.fullName ?: ""
            binding.ratingBar.rating = if (order?.user?.rate?.isNotEmpty() == true)
                order?.user?.rate?.toFloat() ?: 0f
            else 0f


            binding.carType.text = order?.user?.carType ?: ""
            binding.carModel.text = order?.user?.carModel ?: ""
            binding.carColor.text = order?.user?.carColor ?: ""
            binding.carPlate.text = order?.user?.carNumber ?: ""

            binding.seat.text = "${order?.maxPassenger ?: 0} ${getString(R.string.seats)}"
            binding.driverNote.text = order?.notes ?: ""
        }

        binding.edit.visibility = View.GONE
        binding.orderStatus.visibility = View.GONE
        binding.requestsContainer.visibility = View.GONE
        binding.rateUser.visibility = View.GONE
        binding.rateDriver.visibility = View.GONE
        binding.sendBtn.visibility = View.VISIBLE
        if (args.isOrder) {
            binding.sendBtn.visibility = View.GONE
//            if (order?.user?.id == user?.id)
//                binding.edit.visibility = View.VISIBLE

            binding.orderStatus.visibility = View.VISIBLE
            binding.orderStatus.text = when (order?.status) {
                finishedKey -> getString(R.string.finished)
                cancelledKey -> getString(R.string.cancelled)
                acceptedKey -> getString(R.string.open_order)
                else -> getString(R.string.new_order)
            }

            if (order?.status == finishedKey) {
                binding.rateUser.visibility = View.VISIBLE
                binding.rateDriver.visibility = View.VISIBLE
            }

            order?.offers?.let {
                if (it.isNotEmpty()) {
                    binding.requestsContainer.visibility = View.VISIBLE

                    val adapterUserRequest = UserRequestAdapter(requireContext()) { item, _ ->
                        findNavController().navigate(
                            RequestDetailsFragmentDirections.actionRequestDetailsFragmentToJoinDetailsFragment(
                                joinObj = item
                            )
                        )
                    }
                    adapterUserRequest.items = it
                    binding.recyclerViewRequests.adapter = adapterUserRequest
                }
            }
        }

        binding.rateUser.setOnClickListener {
            rateBottomSheet()
        }
        binding.rateDriver.setOnClickListener {
            rateBottomSheet()
        }
    }

    private fun rateBottomSheet() {
        val bottomSheet = BottomSheetDialog(requireContext())
        val rootView =
            layoutInflater.inflate(R.layout.bottomsheet_rate, binding.container, false)
        bottomSheet.setContentView(rootView)

        val userImageRate = rootView.findViewById<ImageView>(R.id.userImageRate)
        val usernameRate = rootView.findViewById<TextView>(R.id.usernameRate)
        val ratingBarRate = rootView.findViewById<ScaleRatingBar>(R.id.ratingBarRate)
        val noteETRate = rootView.findViewById<EditText>(R.id.noteETRate)

        userImageRate.loadImage(requireContext(), order?.user?.image ?: "")
        usernameRate.text = order?.user?.fullName ?: ""

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
}