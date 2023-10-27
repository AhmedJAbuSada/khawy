package com.khawi.ui.request_details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.khawi.R
import com.khawi.databinding.FragmentRequestDetailsBinding
import com.khawi.model.Day
import com.willy.ratingbar.ScaleRatingBar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RequestDetailsFragment : Fragment() {

    private var _binding: FragmentRequestDetailsBinding? = null
    private val binding get() = _binding!!
    private val listDays = mutableListOf<Day>()
    private val args: RequestDetailsFragmentArgs by navArgs()

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
        binding.sendBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.joinContainer.visibility = View.GONE
        binding.deliverContainer.visibility = View.GONE
        if (args.isDeliver) {
            binding.sendBtn.text = getString(R.string.apply_deliver)
            binding.deliverContainer.visibility = View.VISIBLE
            binding.sendBtn.setOnClickListener {
                findNavController().navigate(RequestDetailsFragmentDirections.actionRequestDetailsFragmentToRequestDeliverFragment())
            }
        } else {
            binding.sendBtn.text = getString(R.string.join_now)
            binding.joinContainer.visibility = View.VISIBLE
            binding.sendBtn.setOnClickListener {
                findNavController().navigate(RequestDetailsFragmentDirections.actionRequestDetailsFragmentToRequestJoinFragment())
            }
        }

        binding.edit.visibility = View.GONE
        binding.orderStatus.visibility = View.GONE
        binding.requestsContainer.visibility = View.GONE
        binding.rateUser.visibility = View.GONE
        binding.rateDriver.visibility = View.GONE
        binding.sendBtn.visibility = View.VISIBLE
        if (args.isOrder) {
//            args.orderStatus
            binding.edit.visibility = View.VISIBLE
            binding.orderStatus.visibility = View.VISIBLE
            binding.requestsContainer.visibility = View.VISIBLE
            binding.rateUser.visibility = View.VISIBLE
            binding.rateDriver.visibility = View.VISIBLE
            binding.sendBtn.visibility = View.GONE

            val list = mutableListOf<String>()
            list.add("")
            list.add("")
            list.add("")
            val adapterUserRequest = UserRequestAdapter(requireContext()) { _, _ ->

            }
            adapterUserRequest.items = list
            binding.recyclerViewDays.adapter = adapterUserRequest
        }

        binding.rateUser.setOnClickListener {
            rateBottomSheet()
        }
        binding.rateDriver.setOnClickListener {
            rateBottomSheet()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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

        val rateBtn = rootView.findViewById<TextView>(R.id.rateBtn)

        rateBtn.setOnClickListener {
            bottomSheet.dismiss()
        }

        bottomSheet.show()
    }
}