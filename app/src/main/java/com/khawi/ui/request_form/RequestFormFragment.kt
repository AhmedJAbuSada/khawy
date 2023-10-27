package com.khawi.ui.request_form

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.khawi.R
import com.khawi.databinding.FragmentRequestFormBinding
import com.khawi.model.Day
import com.willy.ratingbar.ScaleRatingBar
import dagger.hilt.android.AndroidEntryPoint
import de.hdodenhof.circleimageview.CircleImageView

@AndroidEntryPoint
class RequestFormFragment : Fragment() {

    private var _binding: FragmentRequestFormBinding? = null
    private val binding get() = _binding!!
    private val listDays = mutableListOf<Day>()
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
                findNavController().navigate(RequestFormFragmentDirections.actionRequestFormFragmentToRequestDeliverFragment())
            }
        } else {
            binding.sendBtn.text = getString(R.string.join_now)
            binding.joinContainer.visibility = View.VISIBLE
            binding.sendBtn.setOnClickListener {
                deliverBottomSheet()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun deliverBottomSheet() {
        val bottomSheet = BottomSheetDialog(requireContext())
        val rootView =
            layoutInflater.inflate(R.layout.bottomsheet_success_request, binding.container, false)
        bottomSheet.setContentView(rootView)

        val successContent = rootView.findViewById<TextView>(R.id.successContent)

        val doneBtn = rootView.findViewById<TextView>(R.id.doneBtn)

        doneBtn.setOnClickListener {
            findNavController().popBackStack()
            bottomSheet.dismiss()
        }

        successContent.text = getString(R.string.success_request_join)

        bottomSheet.show()
    }
}