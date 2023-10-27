package com.khawi.ui.join_details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.khawi.R
import com.khawi.databinding.FragmentJoinDetailsBinding
import com.khawi.model.Day
import com.khawi.ui.request_details.DaysAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class JoinDetailsFragment : Fragment() {

    private var _binding: FragmentJoinDetailsBinding? = null
    private val binding get() = _binding!!
    private val listDays = mutableListOf<Day>()

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

        listDays.add(Day(name = getString(R.string.saturday), select = true))
        listDays.add(Day(name = getString(R.string.sunday), select = true))
        listDays.add(Day(name = getString(R.string.monday), select = false))
        listDays.add(Day(name = getString(R.string.tuesday), select = true))
        listDays.add(Day(name = getString(R.string.wednesday), select = false))
        listDays.add(Day(name = getString(R.string.thursday), select = true))
        listDays.add(Day(name = getString(R.string.friday), select = false))
        val adapter = DaysAdapter(requireContext()) { _, _ ->

        }
        adapter.items = listDays
        binding.recyclerViewDays.adapter = adapter

        binding.back.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.acceptBtn.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.rejectBtn.setOnClickListener {
            findNavController().popBackStack()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}