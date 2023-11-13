package com.khawi.ui.wallet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.khawi.R
import com.khawi.base.formatDate
import com.khawi.base.showAlertMessage
import com.khawi.custom_view.PaginationScrollListener
import com.khawi.databinding.FragmentWalletBinding
import com.khawi.model.db.user.UserModel
import com.payment.paymentsdk.PaymentSdkActivity.Companion.startCardPayment
import com.payment.paymentsdk.PaymentSdkConfigBuilder
import com.payment.paymentsdk.integrationmodels.PaymentSdkBillingDetails
import com.payment.paymentsdk.integrationmodels.PaymentSdkError
import com.payment.paymentsdk.integrationmodels.PaymentSdkLanguageCode
import com.payment.paymentsdk.integrationmodels.PaymentSdkTransactionDetails
import com.payment.paymentsdk.sharedclasses.interfaces.CallbackPaymentInterface
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

@AndroidEntryPoint
class WalletFragment : Fragment() {

    private var adapter: WalletAdapter? = null
    private var user: UserModel? = null
    private var _binding: FragmentWalletBinding? = null
    private val binding get() = _binding!!

    private val viewModel: WalletViewModel by viewModels()
    private var page = 0
    private var totalPages = 0
    private var isLoading: Boolean = false
    private var isLastPage: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWalletBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = WalletAdapter(requireContext()) { _, _ ->

        }
        binding.recyclerView.adapter = adapter
        binding.recyclerView.addOnScrollListener(object :
            PaginationScrollListener(binding.recyclerView.layoutManager as LinearLayoutManager) {
            override fun loadMoreItems() {
                page += 1
                if (!isLastPage)
                    callRequest()
            }

            override fun getTotalPageCount(): Int {
                return totalPages
            }

            override fun isLastPage(): Boolean {
                return isLastPage
            }

            override fun isLoading(): Boolean {
                return isLoading
            }

        })

        callRequest()

        binding.back.setOnClickListener {
            findNavController().popBackStack()
        }

        viewModel.userMutableLiveData.observe(viewLifecycleOwner) {
            user = it
        }


        viewModel.progressLiveData.observe(viewLifecycleOwner) {
            isLoading = it
            binding.swipeRefreshLayout.isRefreshing = it
        }

        viewModel.successLiveDataList.observe(viewLifecycleOwner) {
            totalPages = it?.pagination?.totalPages ?: 0
            isLastPage = page >= totalPages
            if (it?.data?.isNotEmpty() == true) {
                adapter?.submitList(it.data)
            }
            it?.total?.let { total ->
                binding.totalBalance.text = "$total ${getString(R.string.currancy)}"
            }
            it?.lastDate?.let { lastDate ->
                if (lastDate.isNotEmpty() && lastDate != "0")
                    binding.lastTransactionDate.text = lastDate.formatDate() ?: ""
            }
        }

        viewModel.successLiveData.observe(viewLifecycleOwner) {
            if (it?.status == true) {
                callRequest()
            }
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            page = 1
            callRequest()
        }

        binding.addAmountTV.setOnClickListener {
            addAmountBottomSheet()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun callRequest() {
        viewModel.viewModelScope.launch {
            viewModel.params["limit"] = "10"
            viewModel.params["page"] = "$page"
            viewModel.walletList()
        }
    }

    private fun addAmountBottomSheet() {
        val bottomSheet = BottomSheetDialog(requireContext(), R.style.AppBottomSheetDialogTheme)
        val rootView =
            layoutInflater.inflate(R.layout.bottomsheet_add_amount, binding.container, false)
        bottomSheet.setContentView(rootView)

        val amountET = rootView.findViewById<EditText>(R.id.amountET)
        val sendBtn = rootView.findViewById<TextView>(R.id.sendBtn)
        sendBtn.setOnClickListener {
            val amount = amountET.text.toString()
            if (amount.isNotEmpty()) {
                viewModel.viewModelScope.launch {
                    viewModel.addAmount(amount)
                }
//                openPaymentGateway(amount)
                bottomSheet.dismiss()
            } else {
                getString(R.string.error_amount_empty).showAlertMessage(context = requireContext(),
                    title = getString(R.string.error),
                    confirmText = getString(R.string.Ok),
                    type = SweetAlertDialog.ERROR_TYPE,
                    onCancelClick = {

                    },
                    onConfirmClick = {

                    })
            }
        }

        bottomSheet.show()
    }

    private fun openPaymentGateway(amount: String) {
        try {
            val profileId = "88646" // test
//            val profileId = "94514" // live
            val serverKey = "SKJN2DKWTD-JGLHWBTNWK-JKMJHJW2ZD"
            val clientKey = "CRKMQQ-R77P6T-BHV27Q-TV2RN2"
            val locale = PaymentSdkLanguageCode.AR
            val screenTitle = getString(R.string.app_name)
            val cartDesc = getString(R.string.app_name)
            val currency = "SAR"

            val billingData = PaymentSdkBillingDetails(
                "saudi",
                "SA",
                user?.email ?: "",
                user?.fullName ?: "",
                user?.phoneNumber ?: "",
                "saudi",
                "address",
                "00966"
            )

            val configData =
                PaymentSdkConfigBuilder(
                    profileId,
                    serverKey,
                    clientKey,
                    amount.toDouble(),
                    currency
                )
                    .setCartDescription(cartDesc)
                    .setLanguageCode(locale)
                    .setMerchantIcon(
                        ContextCompat.getDrawable(
                            requireContext(),
                            R.mipmap.ic_launcher
                        )
                    )
                    .setBillingData(billingData)
                    .setMerchantCountryCode("SA")
                    .setCartId("12345")
                    .showBillingInfo(false)
                    .showShippingInfo(false)
                    .forceShippingInfo(false)
                    .setScreenTitle(screenTitle)
                    .build()
            startCardPayment(requireActivity(), configData, object : CallbackPaymentInterface {
                override fun onError(error: PaymentSdkError) {

                }

                override fun onPaymentCancel() {

                }

                override fun onPaymentFinish(paymentSdkTransactionDetails: PaymentSdkTransactionDetails) {
                    viewModel.viewModelScope.launch {
                        viewModel.addAmount(amount)
                    }
                }

            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}