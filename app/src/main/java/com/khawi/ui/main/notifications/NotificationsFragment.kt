package com.khawi.ui.main.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.khawi.R
import com.khawi.base.formatDate
import com.khawi.base.safeNavigate
import com.khawi.custom_view.PaginationScrollListener
import com.khawi.databinding.FragmentNotificationsBinding
import com.khawi.model.Order
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!

    private var adapter: NotificationAdapter? = null
    private val viewModel: NotificationViewModel by viewModels()
    private var page = 0
    private var totalPages = 0
    private var isLoading: Boolean = false
    private var isLastPage: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = NotificationAdapter(requireContext()) { item, _ ->
            if (item.type == 1)
                findNavController().safeNavigate(
                    NotificationsFragmentDirections.actionNotificationsToRequestDetailsFragment(
                        isOrder = true,
                        orderObj = Order(id = item.bodyParams),
                    )
                )
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

        viewModel.progressLiveData.observe(viewLifecycleOwner) {
            isLoading = it
            binding.swipeRefreshLayout.isRefreshing = it
        }

        viewModel.successLiveDataList.observe(viewLifecycleOwner) {
            totalPages = it?.pagination?.totalPages ?: 0
            isLastPage = page >= totalPages
            adapter?.submitList(it?.data)
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            page = 1
            callRequest()
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
            viewModel.notificationList()
        }
    }
}