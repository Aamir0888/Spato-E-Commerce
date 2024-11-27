package com.ibs.spato.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.ibs.spato.R
import com.ibs.spato.activities.OrderDetailsActivity
import com.ibs.spato.adapters.MyOrdersAdapter
import com.ibs.spato.databinding.FragmentCompletedOrdersBinding
import com.ibs.spato.databinding.FragmentCurrentOrdersBinding
import com.ibs.spato.di.ApplicationComponentObject
import com.ibs.spato.responses.my_orders_responses.all_orders.MyOrdersData
import com.ibs.spato.retrofit.NetworkResult
import com.ibs.spato.utilities.Constants
import com.ibs.spato.utilities.SpatoProgressBar
import com.ibs.spato.viewmodels.MainViewModel
import javax.inject.Inject

class CompletedOrdersFragment : Fragment(), MyOrdersAdapter.ClickListener{
    private var _binding: FragmentCompletedOrdersBinding? = null
    private val binding get() = _binding!!
    @Inject
    lateinit var mainViewModel: MainViewModel
    @Inject
    lateinit var spatoProgressBar: SpatoProgressBar
    private var loading = true
    private lateinit var myOrdersAdapter: MyOrdersAdapter
    private lateinit var completedOrdersList: ArrayList<MyOrdersData>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentCompletedOrdersBinding.inflate(inflater, container, false)

        ApplicationComponentObject.create(requireContext()).injectCompletedOrders(this)
        apiCall()
        observe()

        binding.pullToRefresh.setOnRefreshListener {
            binding.pullToRefresh.isRefreshing = true
            apiCall()
        }

        return binding.root
    }

    private fun observe() {
        mainViewModel.myOrderListResponse.observe(requireActivity(), Observer {
            when (it) {
                is NetworkResult.Loading -> {
                    if (loading) {
                        spatoProgressBar.start()
                        loading = false
                    }
                }

                is NetworkResult.Success -> {
                    spatoProgressBar.stop()
                    binding.pullToRefresh.isRefreshing = false
                    if (it.data!!.data.size > 0) {
                        binding.noData.visibility = View.GONE
                        binding.completedOrdersRecyclerView.visibility = View.VISIBLE
                        completedOrdersList = ArrayList()
                        completedOrdersList = it.data.data
                        binding.completedOrdersRecyclerView.apply {
                            layoutManager = LinearLayoutManager(activity)
                            myOrdersAdapter = MyOrdersAdapter(requireContext(), completedOrdersList, this@CompletedOrdersFragment)
                            adapter = myOrdersAdapter
                        }
                    } else {
                        binding.noData.visibility = View.VISIBLE
                        binding.completedOrdersRecyclerView.visibility = View.INVISIBLE
                    }
                }

                is NetworkResult.Error -> {
                    spatoProgressBar.stop()
                    binding.pullToRefresh.isRefreshing = false
                    binding.noData.visibility = View.VISIBLE
                    binding.completedOrdersRecyclerView.visibility = View.INVISIBLE
                    Toast.makeText(activity, it.message.toString(), Toast.LENGTH_SHORT).show()
                }

                is NetworkResult.NoInternet -> {
                    spatoProgressBar.stop()
                    binding.pullToRefresh.isRefreshing = false
                    binding.noData.visibility = View.VISIBLE
                    binding.completedOrdersRecyclerView.visibility = View.INVISIBLE
                    Toast.makeText(activity, it.message.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun apiCall() {
        mainViewModel.orderList("complete")
    }

    override fun onClick(orderId: Long) {
        val intent = Intent(activity, OrderDetailsActivity::class.java)
        intent.putExtra(Constants.ORDER_ID, orderId)
        startActivity(intent)
    }
}