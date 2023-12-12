package com.khawi.ui.join_details

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.android.gms.maps.model.LatLng
import com.kaopiz.kprogresshud.KProgressHUD
import com.khawi.R
import com.khawi.base.acceptOfferKey
import com.khawi.base.addOfferKey
import com.khawi.base.formatDate
import com.khawi.base.hideDialog
import com.khawi.base.initLoading
import com.khawi.base.loadImage
import com.khawi.base.rejectOfferKey
import com.khawi.base.showAlertMessage
import com.khawi.base.showDialog
import com.khawi.databinding.FragmentJoinDetailsBinding
import com.khawi.model.Offer
import com.khawi.ui.select_destination.SelectDestinationActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class JoinDetailsFragment : Fragment() {

    private var _binding: FragmentJoinDetailsBinding? = null
    private val binding get() = _binding!!
//    private val listDays = mutableListOf<Day>()

    private var offer: Offer? = null
    private var loading: KProgressHUD? = null
    private val viewModel: JoinDetailsViewModel by viewModels()
    private val args: JoinDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentJoinDetailsBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        offer = args.joinObj
        if (offer != null)
            fillInfo()

//        listDays.add(Day(name = getString(R.string.saturday), select = true))
//        listDays.add(Day(name = getString(R.string.sunday), select = true))
//        listDays.add(Day(name = getString(R.string.monday), select = false))
//        listDays.add(Day(name = getString(R.string.tuesday), select = true))
//        listDays.add(Day(name = getString(R.string.wednesday), select = false))
//        listDays.add(Day(name = getString(R.string.thursday), select = true))
//        listDays.add(Day(name = getString(R.string.friday), select = false))
//        val adapter = DaysAdapter(requireContext()) { _, _ ->
//
//        }
//        adapter.items = listDays
//        binding.recyclerViewDays.adapter = adapter

        binding.back.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.acceptBtn.setOnClickListener {
            getString(R.string.are_you_want_accept).showAlertMessage(context = requireContext(),
                title = getString(R.string.alert),
                confirmText = getString(R.string.Ok),
                cancelText = getString(R.string.close_),
                type = SweetAlertDialog.WARNING_TYPE,
                onCancelClick = {

                },
                onConfirmClick = {
                    viewModel.viewModelScope.launch {
                        viewModel.changeOfferStatusBody(
                            args.orderObj?.id ?: "",
                            offer?.id ?: "",
                            acceptOfferKey
                        )
                    }
                })
        }
        binding.rejectBtn.setOnClickListener {
            getString(R.string.are_you_want_reject).showAlertMessage(context = requireContext(),
                title = getString(R.string.alert),
                confirmText = getString(R.string.Ok),
                cancelText = getString(R.string.close_),
                type = SweetAlertDialog.WARNING_TYPE,
                onCancelClick = {

                },
                onConfirmClick = {
                    viewModel.viewModelScope.launch {
                        viewModel.changeOfferStatusBody(
                            args.orderObj?.id ?: "",
                            offer?.id ?: "",
                            rejectOfferKey
                        )
                    }
                })
        }

        loading = requireActivity().initLoading()
        viewModel.progressLiveData.observe(viewLifecycleOwner) {
            if (it) loading?.showDialog()
            else loading?.hideDialog()
        }
        viewModel.successLiveData.observe(viewLifecycleOwner) {
            if (it?.status == true) {
                it.message?.showAlertMessage(context = requireContext(),
                    title = getString(R.string.success),
                    confirmText = getString(R.string.Ok),
                    type = SweetAlertDialog.SUCCESS_TYPE,
                    onCancelClick = {},
                    onConfirmClick = {
                        findNavController().popBackStack()
                    })
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun fillInfo() {
        val user = offer?.user
        binding.username.text = user?.fullName ?: ""
        binding.userImage.loadImage(user?.image ?: "")
        binding.tripInformation.text =
            "${getString(R.string.from)}: ${offer?.fAddress}\n${getString(R.string.to)}: ${offer?.tAddress}"
        binding.tripTime.text = offer?.dtTime ?: ""
        binding.tripDate.text = offer?.dtDate?.formatDate() ?: ""
        binding.price.text = "${offer?.price} ${getString(R.string.currency)}"
        binding.note.text = offer?.notes ?: ""
        if ((offer?.notes ?: "").isEmpty())
            binding.groupNote.visibility = View.GONE

        binding.showMap.setOnClickListener {
            startActivity(
                Intent(requireContext(), SelectDestinationActivity::class.java)
                    .putExtra(SelectDestinationActivity.isPreviewKey, true)
                    .putExtra(
                        SelectDestinationActivity.latLongStartKey, LatLng(
                            offer?.fLat ?: 0.0,
                            offer?.fLng ?: 0.0,
                        )
                    )
                    .putExtra(
                        SelectDestinationActivity.latLongEndKey, LatLng(
                            offer?.tLat ?: 0.0,
                            offer?.tLng ?: 0.0,
                        )
                    )
            )
        }
        binding.acceptBtn.visibility = View.GONE
        binding.rejectBtn.visibility = View.GONE
        if (offer?.status == addOfferKey) {
            binding.acceptBtn.visibility = View.VISIBLE
            binding.rejectBtn.visibility = View.VISIBLE
        }

        binding.carInformationContainer.visibility = View.GONE
        if (args.isOfferDeliver) {
            binding.carInformationContainer.visibility = View.VISIBLE
            binding.carType.text = offer?.user?.carType ?: ""
            binding.carModel.text = offer?.user?.carModel ?: ""
            binding.carColor.text = offer?.user?.carColor ?: ""
            binding.carPlate.text = offer?.user?.carNumber ?: ""
        }
    }
}