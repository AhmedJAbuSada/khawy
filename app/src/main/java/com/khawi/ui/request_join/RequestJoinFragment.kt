package com.khawi.ui.request_join

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.CompositeDateValidator
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.kaopiz.kprogresshud.KProgressHUD
import com.khawi.R
import com.khawi.base.deliverBottomSheet
import com.khawi.base.getAddress
import com.khawi.base.hideDialog
import com.khawi.base.initLoading
import com.khawi.base.parcelable
import com.khawi.base.showAlertMessage
import com.khawi.base.showDialog
import com.khawi.databinding.FragmentRequestJoinBinding
import com.khawi.model.AddOrderBody
import com.khawi.model.Day
import com.khawi.model.Order
import com.khawi.ui.request_details.DaysAdapter
import com.khawi.ui.select_destination.SelectDestinationActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class RequestJoinFragment : Fragment() {
    private var _binding: FragmentRequestJoinBinding? = null
    private val binding get() = _binding!!
    private val listDays = mutableListOf<Day>()
    private var adapterDays: DaysAdapter? = null
    private val args: RequestJoinFragmentArgs by navArgs()

    private var order: Order? = null

    private var latlngStart: LatLng? = null
    private var latlngEnd: LatLng? = null
    private var tripDate: String? = null
    private var tripTime: String? = null

    private var loading: KProgressHUD? = null
    private val viewModel: RequestJoinViewModel by viewModels()

    private val registerForActivityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                latlngStart = result.data?.parcelable(SelectDestinationActivity.latLongStartKey)
                latlngEnd = result.data?.parcelable(SelectDestinationActivity.latLongStartKey)
                if (latlngStart != null && latlngEnd != null) {
                    binding.tripMapIV.setImageResource(R.drawable.edit)
                    binding.tripMapTV.text = getString(R.string.destination_selected)
                    binding.tripMapTV.setTextColor(Color.parseColor("#0CB057"))
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRequestJoinBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        order = args.orderObj

        binding.tripMapIV.setImageResource(R.drawable.arrow_show_details)
        binding.tripMapTV.text = getString(R.string.select_the_destination_on_the_map)
        binding.tripMapTV.setTextColor(Color.parseColor("#666666"))
        binding.tripMapContainer.setOnClickListener {
            val intent = Intent(requireContext(), SelectDestinationActivity::class.java)
            if (latlngStart != null)
                intent.putExtra(SelectDestinationActivity.latLongStartKey, latlngStart)
            if (latlngEnd != null)
                intent.putExtra(SelectDestinationActivity.latLongEndKey, latlngEnd)
            registerForActivityResult.launch(intent)
        }
        binding.tripDateContainer.setOnClickListener {
            setupDatePicker()
        }
        binding.tripTimeContainer.setOnClickListener {
            setupTimePicker()
        }

        binding.back.setOnClickListener {
            findNavController().popBackStack()
        }

        val minPrice = order?.minPrice
        val maxPrice = order?.maxPrice
        val message =
            "${getString(R.string.price_must_between)} ($minPrice - $maxPrice ${getString(R.string.currancy)})"
        binding.priceRangeAlert.text = message

        binding.recyclerViewDays.visibility = View.GONE
        binding.dailyCheckBoxContainer.setOnClickListener {
            if (binding.dailyCheckBox.isChecked) {
                binding.recyclerViewDays.visibility = View.GONE
                binding.tripDate.text = getString(R.string.trip_date)
                binding.dailyCheckBox.isChecked = false
            } else {
                binding.recyclerViewDays.visibility = View.VISIBLE
                binding.tripDate.text = getString(R.string.trip_first_date)
                binding.dailyCheckBox.isChecked = true
            }
        }

//        listDays.add(Day(name = getString(R.string.saturday), select = false))
//        listDays.add(Day(name = getString(R.string.sunday), select = false))
//        listDays.add(Day(name = getString(R.string.monday), select = false))
//        listDays.add(Day(name = getString(R.string.tuesday), select = false))
//        listDays.add(Day(name = getString(R.string.wednesday), select = false))
//        listDays.add(Day(name = getString(R.string.thursday), select = false))
//        listDays.add(Day(name = getString(R.string.friday), select = false))
//        adapterDays = DaysAdapter(requireContext()) { _, position ->
//            listDays[position].select = !listDays[position].select
//            adapterDays?.notifyDataSetChanged()
//        }
//        adapterDays?.items = listDays
//        binding.recyclerViewDays.adapter = adapterDays

        binding.sendBtn.setOnClickListener {
            if (validation()) {
//                val selectedDays = listDays.filter { it.select }
//                val listDays = mutableListOf<String>()
//                for (value in selectedDays) {
//                    listDays.add(value.name ?: "")
//                }
//                val stringDays = "$listDays"
                viewModel.viewModelScope.launch {
                    viewModel.addOrder(
                        order?.id ?: "",
                        AddOrderBody(
                            dtDate = tripDate,
                            dtTime = tripTime,
                            fAddress = latlngStart?.getAddress(requireContext()),
                            tAddress = latlngEnd?.getAddress(requireContext()),
                            fLat = latlngStart?.latitude,
                            fLng = latlngStart?.longitude,
                            tLat = latlngEnd?.latitude,
                            tLng = latlngEnd?.longitude,
                            price = binding.priceET.text.toString(),
                            isRepeated = binding.dailyCheckBox.isChecked,
                            notes = binding.noteET.text.toString(),
                        )
                    )
                }
            }
        }

        loading = requireActivity().initLoading()
        viewModel.progressLiveData.observe(viewLifecycleOwner) {
            if (it) loading?.showDialog()
            else loading?.hideDialog()
        }
        viewModel.successLiveData.observe(viewLifecycleOwner) {
            if (it?.status == true) {
                requireContext().deliverBottomSheet(
                    layoutInflater,
                    binding.container,
                    getString(R.string.success_request_join_applied),
                    getString(R.string.show_request_details)
                ) {
                    findNavController().popBackStack()
                }
            } else {
                it?.message?.showAlertMessage(
                    context = requireContext(),
                    title = getString(R.string.error),
                    confirmText = getString(R.string.Ok),
                    type = SweetAlertDialog.ERROR_TYPE,
                    onCancelClick = {

                    },
                    onConfirmClick = {

                    }
                )
            }
        }
    }

    private fun setupDatePicker() {
        val materialDateBuilder: MaterialDatePicker.Builder<Long> =
            MaterialDatePicker.Builder.datePicker()
        materialDateBuilder.setTheme(R.style.MaterialCalendarTheme)
        materialDateBuilder.setTitleText(getString(R.string.trip_date))

        val constraintsBuilderRange = CalendarConstraints.Builder()
        val dateValidatorMin: CalendarConstraints.DateValidator =
            DateValidatorPointForward.now()
        val listValidators = ArrayList<CalendarConstraints.DateValidator>()
        listValidators.add(dateValidatorMin)
        val validators: CalendarConstraints.DateValidator =
            CompositeDateValidator.allOf(listValidators)
        constraintsBuilderRange.setValidator(validators)
        materialDateBuilder.setCalendarConstraints(constraintsBuilderRange.build())

        val materialDatePicker = materialDateBuilder.build()

        materialDatePicker.addOnPositiveButtonClickListener {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = it
            tripDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .format(calendar.time)
            binding.tripDate.text = tripDate
        }
        materialDatePicker.show(childFragmentManager, "MATERIAL_DATE_PICKER")
    }

    private fun setupTimePicker() {
        val materialTimeBuilder = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setHour(0)
            .setMinute(0)
//            .setTheme(R.style.MaterialCalendarTheme)
            .build()


        materialTimeBuilder.addOnPositiveButtonClickListener {
            val selectedHour = materialTimeBuilder.hour
            val selectedMinute = materialTimeBuilder.minute
            tripTime = String.format("%02d:%02d", selectedHour, selectedMinute)
            binding.tripTime.text = tripTime
        }
        materialTimeBuilder.show(childFragmentManager, "MATERIAL_DATE_PICKER")
    }

    private fun validation(): Boolean {
        if (latlngStart == null || latlngEnd == null) {
            getString(R.string.select_the_destination_on_the_map).showAlertMessage(
                context = requireContext(),
                title = getString(R.string.error),
                confirmText = getString(R.string.Ok),
                type = SweetAlertDialog.ERROR_TYPE,
                onCancelClick = {

                },
                onConfirmClick = {

                }
            )
            return false
        }
        if (binding.priceET.text.toString().isEmpty()) {
            getString(R.string.error_price_empty).showAlertMessage(
                context = requireContext(),
                title = getString(R.string.error),
                confirmText = getString(R.string.Ok),
                type = SweetAlertDialog.ERROR_TYPE,
                onCancelClick = {

                },
                onConfirmClick = {

                }
            )
            return false
        }
        if (binding.priceET.text.toString().isNotEmpty()) {
            val minPrice = order?.minPrice?.toDouble() ?: 0.0
            val maxPrice = order?.maxPrice?.toDouble() ?: 0.0
            val price = binding.priceET.text.toString().toDouble()
            if (price < minPrice || price > maxPrice) {
                val message =
                    "${getString(R.string.price_must_between)} ($minPrice - $maxPrice ${getString(R.string.currancy)})"
                message.showAlertMessage(
                    context = requireContext(),
                    title = getString(R.string.error),
                    confirmText = getString(R.string.Ok),
                    type = SweetAlertDialog.ERROR_TYPE,
                    onCancelClick = {

                    },
                    onConfirmClick = {

                    }
                )
                return false
            }
        }
        if (binding.dailyCheckBox.isChecked) {
            val selectedDays = listDays.filter { it.select }
            if (selectedDays.isEmpty())
                getString(R.string.error_select_a_day).showAlertMessage(
                    context = requireContext(),
                    title = getString(R.string.error),
                    confirmText = getString(R.string.Ok),
                    type = SweetAlertDialog.ERROR_TYPE,
                    onCancelClick = {

                    },
                    onConfirmClick = {

                    }
                )
            return false
        }
        if (tripDate.isNullOrEmpty()) {
            getString(R.string.error_date_empty).showAlertMessage(
                context = requireContext(),
                title = getString(R.string.error),
                confirmText = getString(R.string.Ok),
                type = SweetAlertDialog.ERROR_TYPE,
                onCancelClick = {

                },
                onConfirmClick = {

                }
            )
            return false
        }
        if (tripTime.isNullOrEmpty()) {
            getString(R.string.error_time_empty).showAlertMessage(
                context = requireContext(),
                title = getString(R.string.error),
                confirmText = getString(R.string.Ok),
                type = SweetAlertDialog.ERROR_TYPE,
                onCancelClick = {

                },
                onConfirmClick = {

                }
            )
            return false
        }
        return true
    }
}