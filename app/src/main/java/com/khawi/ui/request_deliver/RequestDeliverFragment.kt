package com.khawi.ui.request_deliver

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.khawi.R
import com.khawi.base.deliverBottomSheet
import com.khawi.databinding.FragmentRequestDeliverBinding
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
            requireContext().deliverBottomSheet(
                layoutInflater,
                binding.container,
                getString(R.string.success_request_deliver),
                getString(R.string.show_request_details)
            ) {
                findNavController().popBackStack()
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}