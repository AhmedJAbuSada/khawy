package com.khawi.ui.request_form

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.CompositeDateValidator
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.khawi.R
import com.khawi.base.deliverBottomSheet
import com.khawi.databinding.FragmentRequestFormBinding
import com.khawi.model.Day
import com.khawi.ui.request_details.DaysAdapter
import com.khawi.ui.select_destination.SelectDestinationActivity
import com.khawi.ui.static_page.StaticContentActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class RequestFormFragment : Fragment() {
    private var _binding: FragmentRequestFormBinding? = null
    private val binding get() = _binding!!
    private val listDays = mutableListOf<Day>()
    private var adapterDays: DaysAdapter? = null
    private var isDeliver = false
    private val args: RequestFormFragmentArgs by navArgs()


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
            binding.tripMapIV.setImageResource(R.drawable.edit)
            binding.tripMapTV.text = getString(R.string.destination_selected)
            binding.tripMapTV.setTextColor(Color.parseColor("#0CB057"))
            startActivity(Intent(requireContext(), SelectDestinationActivity::class.java))
        }
        binding.tripDateContainer.setOnClickListener {
            setupDatePicker()
        }
        binding.tripTimeContainer.setOnClickListener {
            setupTimePicker()
        }

        binding.priceET.visibility = View.GONE
        binding.joinGroup.visibility = View.GONE
        if (isDeliver) {
            binding.title.text = getString(R.string.deliver_form)
            binding.priceET.visibility = View.VISIBLE
            binding.maxSeatsET.hint = getString(R.string.seats_counts)
        } else {
            binding.title.text = getString(R.string.join_form)
            binding.joinGroup.visibility = View.VISIBLE
            binding.maxSeatsET.hint = getString(R.string.maximum_seats)
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

    private fun setupDatePicker() {
        val materialDateBuilder: MaterialDatePicker.Builder<Long> =
            MaterialDatePicker.Builder.datePicker()
        materialDateBuilder.setTheme(R.style.MaterialCalendarTheme)
        materialDateBuilder.setTitleText(getString(R.string.trip_date))

        val constraintsBuilderRange = CalendarConstraints.Builder()
        val dateValidatorMin: CalendarConstraints.DateValidator =
            DateValidatorPointBackward.before(Calendar.getInstance().timeInMillis)
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
            binding.tripDate.text = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .format(calendar.time)
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
            binding.tripTime.text = String.format("%02d:%02d", selectedHour, selectedMinute)
        }
        materialTimeBuilder.show(childFragmentManager, "MATERIAL_DATE_PICKER")
    }
}