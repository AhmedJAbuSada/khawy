package com.khawi.ui.main.orders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.khawi.R
import com.khawi.base.cancelledKey
import com.khawi.base.finishedKey
import com.khawi.base.newKey
import com.khawi.custom_view.PaginationScrollListener
import com.khawi.databinding.FragmentOrdersBinding
import com.khawi.model.Order
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class OrdersFragment : Fragment() {

    private var adapter: OrderAdapter? = null
    private var _binding: FragmentOrdersBinding? = null
    private val binding get() = _binding!!
    var tabPosition = 0

    private val viewModel: OrderViewModel by viewModels()
    private var page = 0
    private var totalPages = 0
    private var isLoading: Boolean = false
    private var isLastPage: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrdersBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(getString(R.string.orders_now)))
        binding.tabLayout.addTab(
            binding.tabLayout.newTab().setText(getString(R.string.orders_completed))
        )
        binding.tabLayout.addTab(
            binding.tabLayout.newTab().setText(getString(R.string.orders_canceled))
        )

        adapter = OrderAdapter(requireContext()) { item, _ ->
            findNavController().navigate(
                OrdersFragmentDirections.actionOrdersFragmentToRequestDetailsFragment(
                    isDeliver = item.orderType == 2,
                    isOrder = true,
                    orderStatus = item.status ?: "",
                    orderObj = item
                )
            )
        }
        binding.recyclerView.adapter = adapter
        binding.recyclerView.addOnScrollListener(object :
            PaginationScrollListener(binding.recyclerView.layoutManager as LinearLayoutManager) {
            override fun loadMoreItems() {
                page += 1
                if (!isLastPage)
                    fillList()
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

        fillList()
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                tabPosition = tab.position
                fillList()
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
            }
        })

        viewModel.progressLiveData.observe(viewLifecycleOwner) {
            isLoading = it
            binding.swipeRefreshLayout.isRefreshing = it
        }

        viewModel.successLiveData.observe(viewLifecycleOwner) {
            totalPages = it?.pagination?.totalPages ?: 0
            isLastPage = page >= totalPages
            if (it?.data?.isNotEmpty() == true) {
                adapter?.submitList(it.data)
            }
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            page = 1
            fillList()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun fillList() {
        viewModel.params["limit"] = "10"
        viewModel.params["page"] = "$page"
        viewModel.params["status"] = when (tabPosition) {
            1 -> finishedKey
            2 -> cancelledKey
            else -> newKey
        }
        viewModel.viewModelScope.launch {
            viewModel.orderList()
        }
    }
}