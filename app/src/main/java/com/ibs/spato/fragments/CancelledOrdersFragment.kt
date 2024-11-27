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
import com.ibs.spato.databinding.FragmentCancelledOrdersBinding
import com.ibs.spato.databinding.FragmentCompletedOrdersBinding
import com.ibs.spato.di.ApplicationComponentObject
import com.ibs.spato.responses.my_orders_responses.all_orders.MyOrdersData
import com.ibs.spato.retrofit.NetworkResult
import com.ibs.spato.utilities.Constants
import com.ibs.spato.utilities.SpatoProgressBar
import com.ibs.spato.viewmodels.MainViewModel
import javax.inject.Inject

class CancelledOrdersFragment : Fragment(), MyOrdersAdapter.ClickListener{
    private var _binding: FragmentCancelledOrdersBinding? = null
    private val binding get() = _binding!!
    @Inject
    lateinit var mainViewModel: MainViewModel
    @Inject
    lateinit var spatoProgressBar: SpatoProgressBar
    private var loading = true
    private lateinit var myOrdersAdapter: MyOrdersAdapter
    private lateinit var cancelledOrdersList: ArrayList<MyOrdersData>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentCancelledOrdersBinding.inflate(inflater, container, false)

        ApplicationComponentObject.create(requireContext()).injectCancelledOrders(this)
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
                        binding.cancelledOrdersRecyclerView.visibility = View.VISIBLE
                        cancelledOrdersList = ArrayList()
                        cancelledOrdersList = it.data.data
                        binding.cancelledOrdersRecyclerView.apply {
                            layoutManager = LinearLayoutManager(activity)
                            myOrdersAdapter = MyOrdersAdapter(requireContext(), cancelledOrdersList, this@CancelledOrdersFragment)
                            adapter = myOrdersAdapter
                        }
                    } else {
                        binding.noData.visibility = View.VISIBLE
                        binding.cancelledOrdersRecyclerView.visibility = View.INVISIBLE
                    }
                }

                is NetworkResult.Error -> {
                    spatoProgressBar.stop()
                    binding.pullToRefresh.isRefreshing = false
                    binding.noData.visibility = View.VISIBLE
                    binding.cancelledOrdersRecyclerView.visibility = View.INVISIBLE
                    Toast.makeText(activity, it.message.toString(), Toast.LENGTH_SHORT).show()
                }

                is NetworkResult.NoInternet -> {
                    spatoProgressBar.stop()
                    binding.pullToRefresh.isRefreshing = false
                    binding.noData.visibility = View.VISIBLE
                    binding.cancelledOrdersRecyclerView.visibility = View.INVISIBLE
                    Toast.makeText(activity, it.message.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun apiCall() {
        mainViewModel.orderList("canceled")
    }

    override fun onClick(orderId: Long) {
        val intent = Intent(activity, OrderDetailsActivity::class.java)
        intent.putExtra(Constants.ORDER_ID, orderId)
        startActivity(intent)
    }
}