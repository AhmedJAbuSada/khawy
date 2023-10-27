package com.khawi.ui.request_deliver

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.khawi.R
import com.khawi.databinding.FragmentRequestDeliverBinding
import com.khawi.databinding.FragmentRequestFormBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RequestDeliverFragment : Fragment() {

    private var _binding: FragmentRequestDeliverBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRequestDeliverBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.back.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.sendBtn.setOnClickListener {
            deliverBottomSheet()
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

        successContent.text = getString(R.string.success_request_deliver)
        bottomSheet.show()
    }
}