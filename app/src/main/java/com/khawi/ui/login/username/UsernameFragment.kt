package com.khawi.ui.login.username

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import cn.pedant.SweetAlert.SweetAlertDialog
import com.kaopiz.kprogresshud.KProgressHUD
import com.khawi.MainActivity
import com.khawi.R
import com.khawi.base.errorMessage
import com.khawi.base.hideDialog
import com.khawi.base.initLoading
import com.khawi.base.showAlertMessage
import com.khawi.base.showDialog
import com.khawi.databinding.FragmentUsernameBinding
import com.khawi.network_base.model.BaseState
import com.khawi.ui.login.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UsernameFragment : Fragment() {

    private var _binding: FragmentUsernameBinding? = null
    private val binding get() = _binding!!
    private val loginViewModel: LoginViewModel by activityViewModels()
    private val viewModel: UsernameViewModel by viewModels()
    private var loading: KProgressHUD? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUsernameBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loading = requireActivity().initLoading()
        binding.goBtn.setOnClickListener {
            val username = binding.usernameET.text.toString()
            if (username.isNotEmpty()) {
                loading?.showDialog()
                viewModel.viewModelScope.launch {
                    viewModel.updateUser(name = username).collect {
                        when (it) {
                            is BaseState.NetworkError -> {
                                loading?.hideDialog()
                            }

                            is BaseState.EmptyResult -> {
                                loading?.hideDialog()
                            }

                            is BaseState.ItemsLoaded -> {
                                loading?.hideDialog()
                                if (it.items?.status == true) {
                                    it.items.data?.let { item ->
                                        viewModel.addUser(item)
                                        startActivity(
                                            Intent(requireContext(), MainActivity::class.java)
                                        )
                                        requireActivity().finishAffinity()
                                    }

                                } else {
                                    it.items?.message?.errorMessage(requireContext())
                                }
                            }

                            else -> {
                                loading?.hideDialog()
                            }
                        }
                    }
                }
            } else {
                getString(R.string.error_username_empty).showAlertMessage(
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}