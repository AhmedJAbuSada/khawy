package com.khawi.ui.wallet

import android.os.Bundle
import android.util.Log
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
import com.khawi.base.errorMessage
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
import company.tap.gosellapi.GoSellSDK
import company.tap.gosellapi.internal.api.callbacks.GoSellError
import company.tap.gosellapi.internal.api.models.Authorize
import company.tap.gosellapi.internal.api.models.Charge
import company.tap.gosellapi.internal.api.models.PhoneNumber
import company.tap.gosellapi.internal.api.models.Token
import company.tap.gosellapi.open.controllers.SDKSession
import company.tap.gosellapi.open.controllers.ThemeObject
import company.tap.gosellapi.open.delegate.SessionDelegate
import company.tap.gosellapi.open.enums.AppearanceMode
import company.tap.gosellapi.open.enums.TransactionMode
import company.tap.gosellapi.open.models.CardsList
import company.tap.gosellapi.open.models.Customer
import company.tap.gosellapi.open.models.TapCurrency
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.math.BigDecimal

@AndroidEntryPoint
class WalletFragment : Fragment(), SessionDelegate {

    private var adapter: WalletAdapter? = null
    private var user: UserModel? = null
    private var _binding: FragmentWalletBinding? = null
    private val binding get() = _binding!!

    private val viewModel: WalletViewModel by viewModels()
    private var page = 0
    private var totalPages = 0
    private var isLoading: Boolean = false
    private var isLastPage: Boolean = false
    private var amount = ""
    private var bottomSheet: BottomSheetDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
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
                if (!isLastPage) callRequest()
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
            binding.recyclerView.smoothScrollToPosition(0)
            if (it?.data?.isNotEmpty() == true) {
                adapter?.submitList(it.data)
            }
            it?.total?.let { total ->
                binding.totalBalance.text = "$total ${getString(R.string.currency)}"
            }
            it?.lastDate?.let { lastDate ->
                if (lastDate.isNotEmpty() && lastDate != "0") binding.lastTransactionDate.text =
                    lastDate.formatDate() ?: ""
            }
        }

        viewModel.successLiveData.observe(viewLifecycleOwner) {
            if (it?.status == true) {
                amount = ""
                callRequest()
            } else {
                it?.message?.errorMessage(requireContext())
            }
        }

        viewModel.successLiveDataCoupon.observe(viewLifecycleOwner) {
            if (it?.status == true) {
                openPaymentGateway("${it.data?.finalTotal ?: 0.0}")
            } else {
                it?.message?.errorMessage(requireContext())
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

    private var sendBtn: TextView? = null
    private fun addAmountBottomSheet() {
        bottomSheet = BottomSheetDialog(requireContext(), R.style.AppBottomSheetDialogTheme)
        val rootView =
            layoutInflater.inflate(R.layout.bottomsheet_add_amount, binding.container, false)
        bottomSheet?.setContentView(rootView)

        val amountET = rootView.findViewById<EditText>(R.id.amountET)
        val couponET = rootView.findViewById<EditText>(R.id.couponET)
        sendBtn = rootView.findViewById(R.id.sendBtn)
        sendBtn?.setOnClickListener {
            amount = amountET.text.toString()
            val coupon = couponET.text.toString()
            if (amount.isNotEmpty()) {
                if (coupon.isNotEmpty()) {
                    viewModel.viewModelScope.launch {
                        viewModel.checkCoupon(amount, coupon)
                    }
                } else {
                    openPaymentGateway(amount)
                }
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

        bottomSheet?.show()
    }

    private var sdkSession: SDKSession? = null
    private val customer: Customer
        get() {
            val phoneNumber = PhoneNumber("966", (user?.phoneNumber ?: "").replaceFirst("966", ""))
            return Customer.CustomerBuilder(null).email(user?.email ?: "")
                .firstName(user?.fullName ?: "").lastName(user?.fullName ?: "").metadata("")
                .phone(PhoneNumber(phoneNumber.countryCode, phoneNumber.number))
                .middleName(user?.fullName ?: "").build()
        }

    private fun configureApp() {
        /*
        * Prod - Public Key pk_live_vukIzhG0C3DFsmtyoJ86URZA
Test - Public Key pk_test_fNFbCgDQXoxWRSO6hd29pKe8
Prod - Secret Key sk_live_qvDzyURKZYp3saVHF9t6bNlc
Test - Secret Key sk_test_DMOhSWJzsVTKubUEGZpRY0tg
        * */
        GoSellSDK.init(requireContext(), "sk_live_qvDzyURKZYp3saVHF9t6bNlc", "com.khawi")
        GoSellSDK.setLocale("ar")
    }

    private fun configureSDKThemeObject() {
        ThemeObject.getInstance().setAppearanceMode(AppearanceMode.FULLSCREEN_MODE)
            .setSdkLanguage("ar").setPayButtonLoaderVisible(true)
            .setPayButtonSecurityIconVisible(true)
    }

    private fun configureSDKSession(amount: String) {

        // Instantiate SDK Session
        sdkSession = SDKSession()   //** Required **

        // pass your activity as a session delegate to listen to SDK internal payment process follow
        sdkSession?.addSessionDelegate(this)    //** Required **

        // initiate PaymentDataSource
        sdkSession?.instantiatePaymentDataSource()    //** Required **

        // set transaction currency associated to your account
        sdkSession?.setTransactionCurrency(TapCurrency("SAR"))   //** Required **

        // Using static CustomerBuilder method available inside TAP Customer Class you can populate TAP Customer object and pass it to SDK
        sdkSession?.setCustomer(customer)    //** Required **

        // Set Total Amount. The Total amount will be recalculated according to provided Taxes and Shipping
        sdkSession?.setAmount(BigDecimal(amount.toInt()))  //** Required **

        // Enable or Disable Saving Card
        sdkSession?.isUserAllowedToSaveCard(true) //  ** Required ** you can pass boolean

        // Enable or Disable 3DSecure
        sdkSession?.isRequires3DSecure(true)
        sdkSession?.setPaymentType("CARD")   //** Merchant can customize payment options [WEB/CARD] for each transaction or it will show all payment options granted to him.
        sdkSession?.transactionMode = TransactionMode.PURCHASE //** Required **
        sdkSession?.start(requireActivity())
    }

    private fun initPayButton() {
        sdkSession?.setButtonView(sendBtn, requireActivity(), 120)

    }

    private fun openPaymentGateway(amount: String) {
        try {
            configureApp()
            configureSDKThemeObject()
            configureSDKSession(amount)
            initPayButton()
        } catch (e: Exception) {
            e.printStackTrace()
        }
//        try {
////            // test
////            val profileId = "88646"
////            val serverKey = "SZJN2DKWTG-JDHMTD6T2L-KZJKMN6GKH"
////            val clientKey = "C7KMQQ-R7BR6D-HN7DG7-KQKMRQ"
//            // live
//            val profileId = "94514"
//            val serverKey = "SKJN2DKWTD-JGLHWBTNWK-JKMJHJW2ZD"
//            val clientKey = "CRKMQQ-R77P6T-BHV27Q-TV2RN2"
//            val locale = PaymentSdkLanguageCode.AR
//            val screenTitle = getString(R.string.app_name)
//            val cartDesc = getString(R.string.app_name)
//            val currency = "SAR"
//
//            val billingData = PaymentSdkBillingDetails(
//                "saudi",
//                "SA",
//                user?.email ?: "",
//                user?.fullName ?: "",
//                user?.phoneNumber ?: "",
//                "saudi",
//                "address",
//                "00966"
//            )
//
//            val configData =
//                PaymentSdkConfigBuilder(
//                    profileId,
//                    serverKey,
//                    clientKey,
//                    amount.toDouble(),
//                    currency
//                )
//                    .setCartDescription(cartDesc)
//                    .setLanguageCode(locale)
//                    .setMerchantIcon(
//                        ContextCompat.getDrawable(
//                            requireContext(),
//                            R.mipmap.ic_launcher
//                        )
//                    )
//                    .setBillingData(billingData)
//                    .setMerchantCountryCode("SA")
//                    .setCartId("12345")
//                    .showBillingInfo(false)
//                    .showShippingInfo(false)
//                    .forceShippingInfo(false)
//                    .setScreenTitle(screenTitle)
//                    .build()
//            startCardPayment(requireActivity(), configData, object : CallbackPaymentInterface {
//                override fun onError(error: PaymentSdkError) {
//                    Log.e("error payment", "msg: ${error.msg ?: ""}")
//                    getString(R.string.error_paymet).showAlertMessage(context = requireContext(),
//                        title = getString(R.string.error),
//                        confirmText = getString(R.string.Ok),
//                        type = SweetAlertDialog.ERROR_TYPE,
//                        onCancelClick = {
//
//                        },
//                        onConfirmClick = {
//
//                        })
//                }
//
//                override fun onPaymentCancel() {
//
//                }
//
//                override fun onPaymentFinish(paymentSdkTransactionDetails: PaymentSdkTransactionDetails) {
//                    viewModel.viewModelScope.launch {
//                        viewModel.addAmount(amount)
//                    }
//                }
//
//            })
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
    }

    private fun onPaymentSuccess() {
        bottomSheet?.dismiss()
        viewModel.viewModelScope.launch {
            viewModel.addAmount(amount)
        }
    }

    private fun onPaymentFail() {
        getString(R.string.error_paymet).showAlertMessage(context = requireContext(),
            title = getString(R.string.error),
            confirmText = getString(R.string.Ok),
            type = SweetAlertDialog.ERROR_TYPE,
            onCancelClick = {

            },
            onConfirmClick = {

            })
    }

    override fun paymentSucceed(charge: Charge) {
        onPaymentSuccess()
    }

    override fun paymentFailed(charge: Charge?) {
        Log.e("paymentFailed", charge?.toString()?:"")
        onPaymentFail()
    }

    override fun authorizationSucceed(authorize: Authorize) {

    }

    override fun authorizationFailed(authorize: Authorize?) {
        Log.e("authorizationFailed", authorize?.toString()?:"")
        onPaymentFail()
    }

    override fun cardSaved(charge: Charge) {

    }

    override fun cardSavingFailed(charge: Charge) {
        Log.e("cardSavingFailed", charge?.toString()?:"")
        onPaymentFail()
    }

    override fun cardTokenizedSuccessfully(token: Token) {

    }

    override fun cardTokenizedSuccessfully(token: Token, saveCardEnabled: Boolean) {

    }

    override fun savedCardsList(cardsList: CardsList) {

    }

    override fun sdkError(goSellError: GoSellError?) {
        Log.e("sdkError", goSellError?.errorMessage?:"")
        onPaymentFail()
    }

    override fun sessionIsStarting() {

    }

    override fun sessionHasStarted() {

    }

    override fun sessionCancelled() {

    }

    override fun sessionFailedToStart() {
        Log.e("sessionFailedToStart", "sessionFailedToStart")
        onPaymentFail()
    }

    override fun invalidCardDetails() {

    }

    override fun backendUnknownError(message: String?) {

    }

    override fun invalidTransactionMode() {

    }

    override fun invalidCustomerID() {

    }

    override fun userEnabledSaveCardOption(saveCardEnabled: Boolean) {

    }

    override fun asyncPaymentStarted(charge: Charge) {

    }

    override fun paymentInitiated(charge: Charge?) {

    }
}