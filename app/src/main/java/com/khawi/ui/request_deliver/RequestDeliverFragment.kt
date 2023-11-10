package com.khawi.ui.request_deliver

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
import com.kaopiz.kprogresshud.KProgressHUD
import com.khawi.R
import com.khawi.base.deliverBottomSheet
import com.khawi.base.hideDialog
import com.khawi.base.initLoading
import com.khawi.base.showAlertMessage
import com.khawi.base.showDialog
import com.khawi.databinding.FragmentRequestDeliverBinding
import com.khawi.model.AddOrderBody
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RequestDeliverFragment : Fragment() {

    private var _binding: FragmentRequestDeliverBinding? = null
    private val binding get() = _binding!!
    private val args: RequestDeliverFragmentArgs by navArgs()

    private var loading: KProgressHUD? = null
    private val viewModel: RequestDeliverViewModel by viewModels()

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
            val price = binding.priceET.text.toString()
            if (price.isNotEmpty()) {
                viewModel.viewModelScope.launch {
                    val order = args.orderObj
                    viewModel.addOffer(
                        order?.id ?: "",
                        AddOrderBody(
                            fAddress = order?.fAddress ?: "",
                            tAddress = order?.tAddress ?: "",
                            fLat = order?.fLat ?: 0.0,
                            fLng = order?.fLng ?: 0.0,
                            tLat = order?.tLat ?: 0.0,
                            tLng = order?.tLng ?: 0.0,
                            dtDate = order?.dtDate ?: "",
                            dtTime = order?.dtTime ?: "",
                            price = price,
                            notes = binding.noteET.text.toString(),
                        )
                    )
                }
            } else {
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
            }
        }

        loading = requireContext().initLoading()
        viewModel.progressLiveData.observe(viewLifecycleOwner) {
            if (it) loading?.showDialog()
            else loading?.hideDialog()
        }
        viewModel.successLiveData.observe(viewLifecycleOwner) {
            if (it?.status == true) {
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

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}