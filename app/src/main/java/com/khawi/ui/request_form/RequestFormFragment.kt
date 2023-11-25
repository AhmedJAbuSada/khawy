package com.khawi.ui.request_form

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
import com.khawi.base.showAlertMessage
import com.khawi.base.showDialog
import com.khawi.databinding.FragmentRequestFormBinding
import com.khawi.model.AddOrderBody
import com.khawi.model.Day
import com.khawi.ui.request_details.DaysAdapter
import com.khawi.ui.select_destination.SelectDestinationActivity
import com.khawi.ui.static_page.StaticContentActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class RequestFormFragment : Fragment() {
    private var _binding: FragmentRequestFormBinding? = null
    private val binding get() = _binding!!
    private val listDays = mutableListOf<Day>()
    private var adapterDays: DaysAdapter? = null
    private var isDeliver = false
    private val args: RequestFormFragmentArgs by navArgs()
    private var latlngStart: LatLng? = null
    private var latlngEnd: LatLng? = null
    private var tripDate: String? = null
    private var tripTime: String? = null

    private var loading: KProgressHUD? = null
    private val viewModel: RequestFormViewModel by viewModels()

    private val registerForActivityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                if (result.data?.hasExtra(SelectDestinationActivity.latLongStartKey) == true) {
                    latlngStart =
                        result.data?.getParcelableExtra(SelectDestinationActivity.latLongStartKey) as? LatLng
                }
                if (result.data?.hasExtra(SelectDestinationActivity.latLongEndKey) == true) {
                    latlngEnd =
                        result.data?.getParcelableExtra(SelectDestinationActivity.latLongEndKey) as? LatLng
                }
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
        _binding = FragmentRequestFormBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        isDeliver = args.isDeliver

        binding.back.setOnClickListener {
            findNavController().popBackStack()
        }

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

        binding.priceET.visibility = View.GONE
        binding.joinGroup.visibility = View.GONE
        binding.dailyCheckBoxContainer.visibility = View.VISIBLE
        if (isDeliver) {
            binding.dailyCheckBoxContainer.visibility = View.GONE
            binding.title.text = getString(R.string.deliver_form)
            binding.priceET.visibility = View.VISIBLE
            binding.maxSeatsET.hint = getString(R.string.seats_counts)
            binding.sendBtn.text = getString(R.string.apply_deliver)
        } else {
            binding.title.text = getString(R.string.join_form)
            binding.joinGroup.visibility = View.VISIBLE
            binding.maxSeatsET.hint = getString(R.string.maximum_seats)
            binding.sendBtn.text = getString(R.string.apply_join)
        }

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

        listDays.add(Day(name = getString(R.string.saturday), select = false))
        listDays.add(Day(name = getString(R.string.sunday), select = false))
        listDays.add(Day(name = getString(R.string.monday), select = false))
        listDays.add(Day(name = getString(R.string.tuesday), select = false))
        listDays.add(Day(name = getString(R.string.wednesday), select = false))
        listDays.add(Day(name = getString(R.string.thursday), select = false))
        listDays.add(Day(name = getString(R.string.friday), select = false))
        adapterDays = DaysAdapter(requireContext()) { _, position ->
            listDays[position].select = !listDays[position].select
            adapterDays?.notifyDataSetChanged()
        }
        adapterDays?.items = listDays
        binding.recyclerViewDays.adapter = adapterDays

        binding.termsTV.setOnClickListener {
            startActivity(
                Intent(requireContext(), StaticContentActivity::class.java)
                    .putExtra(StaticContentActivity.terms, StaticContentActivity.terms)
            )
        }
        binding.termsCheckBoxContainer.setOnClickListener {
            binding.termsCheckBox.isChecked = !binding.termsCheckBox.isChecked
        }

        binding.sendBtn.setOnClickListener {
            if (validation()) {
                val maxPrice =
                    if (!isDeliver)
                        binding.maximumPriceET.text.toString()
                    else
                        null
                val minPrice =
                    if (!isDeliver)
                        binding.minimumPriceET.text.toString()
                    else
                        null
                val price =
                    if (isDeliver)
                        binding.priceET.text.toString()
                    else
                        null
                val selectedDays = listDays.filter { it.select }
                val listDays = mutableListOf<String>()
                for (value in selectedDays) {
                    listDays.add(value.name ?: "")
                }
                viewModel.viewModelScope.launch {
                    viewModel.addOrder(
                        AddOrderBody(
                            couponCode = "",
                            paymentType = 1,
                            dtDate = tripDate,
                            dtTime = tripTime,
                            fAddress = latlngStart?.getAddress(requireContext()),
                            tAddress = latlngEnd?.getAddress(requireContext()),
                            fLat = latlngStart?.latitude,
                            fLng = latlngStart?.longitude,
                            tLat = latlngEnd?.latitude,
                            tLng = latlngEnd?.longitude,
                            title = binding.tripSubjectET.text.toString(),
                            maxPrice = maxPrice,
                            minPrice = minPrice,
                            price = price,
                            isRepeated = binding.dailyCheckBox.isChecked,
                            days = listDays,
                            orderType = if (isDeliver) 2 else 1,
                            maxPassenger = (binding.maxSeatsET.text.toString()).toInt(),
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
                    if (isDeliver)
                        getString(R.string.success_request_deliver_create)
                    else
                        getString(R.string.success_request_join_create),
                    getString(R.string.show_request_details)
                ) {
                    findNavController().popBackStack()
                }
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
        if (binding.tripSubjectET.text.toString().isEmpty()) {
            getString(R.string.error_trip_subject_empty).showAlertMessage(
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
        if (isDeliver && binding.priceET.text.toString().isEmpty()) {
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
        if (!isDeliver
            && (binding.minimumPriceET.text.toString().isEmpty()
                    || binding.maximumPriceET.text.toString().isEmpty())
        ) {
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
        if (binding.dailyCheckBox.isChecked) {
            val selectedDays = listDays.filter { it.select }
            if (selectedDays.isEmpty()) {
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
        if (binding.maxSeatsET.text.toString().isEmpty()) {
            getString(R.string.error_seats_empty).showAlertMessage(
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
        if (!binding.termsCheckBox.isChecked) {
            getString(R.string.accept_terms).showAlertMessage(
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