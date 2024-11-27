package com.ibs.spato.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.ibs.spato.R
import com.ibs.spato.activities.ChooseAddressActivity
import com.ibs.spato.adapters.MyCartAdapter
import com.ibs.spato.databinding.ActivityMyCartBinding
import com.ibs.spato.databinding.FragmentCartsBinding
import com.ibs.spato.databinding.FragmentProductBinding
import com.ibs.spato.di.ApplicationComponentObject
import com.ibs.spato.responses.cart_list.MyCartItems
import com.ibs.spato.retrofit.NetworkResult
import com.ibs.spato.utilities.Constants
import com.ibs.spato.utilities.SpatoProgressBar
import com.ibs.spato.utilities.Utils
import com.ibs.spato.viewmodels.MainViewModel
import javax.inject.Inject

class CartsFragment : Fragment(), MyCartAdapter.CartItemClickListener {
    private lateinit var binding: FragmentCartsBinding
    @Inject
    lateinit var mainViewModel: MainViewModel
    @Inject
    lateinit var spatoProgressBar: SpatoProgressBar
    private lateinit var myCartAdapter: MyCartAdapter
    private lateinit var cartList: ArrayList<MyCartItems>
    private var loading = true
    private var position = 0
    private var liveDataObserver = false
    private var cartId: Long = 0
    private var totalAmount: Double = 0.0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentCartsBinding.inflate(inflater, container, false)

        ApplicationComponentObject.create(requireContext()).injectCartsFragment(this)
        apiCall()
        observe()
        onClicks()
        binding.pullToRefresh.setOnRefreshListener {
            binding.pullToRefresh.isRefreshing = true
            apiCall()
        }

        return binding.root
    }

    private fun onClicks() {
        binding.llProceedToCheckout.setOnClickListener {
            val intent = Intent(activity, ChooseAddressActivity::class.java)
            intent.putExtra(Constants.CART_ID, cartId)
            intent.putExtra(Constants.TOTAL_AMOUNT, totalAmount)
            startActivity(intent)
        }
    }

    private fun apiCall() {
        mainViewModel.cartList()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observe() {
        mainViewModel.cartListResponse.observe(requireActivity(), Observer {
            when (it) {
                is NetworkResult.Loading -> {
                    if (loading) {
                        spatoProgressBar.start()
                        loading = false
                    }
                }

                is NetworkResult.Success -> {
                    if (it.data!!.data.items.size > 0) {
                        binding.noData.visibility = View.GONE
                        binding.constraintLayout.visibility = View.VISIBLE
                        cartList = ArrayList()
                        cartId = it.data.data.cart_id
                        cartList = it.data.data.items
                        binding.totalAmount.text = it.data.data.getGrandTotal.toString()
                        totalAmount = it.data.data.getGrandTotal
                        if (isAdded){
                            binding.recyclerView.apply {
                                isNestedScrollingEnabled = false
                                layoutManager = LinearLayoutManager(activity)
                                myCartAdapter = MyCartAdapter(requireContext(), cartList, this@CartsFragment)
                                adapter = myCartAdapter
                            }
                        }
                        liveDataObserver = true
                    } else {
                        binding.noData.visibility = View.VISIBLE
                        binding.constraintLayout.visibility = View.INVISIBLE
                    }
                    spatoProgressBar.stop()
                    binding.pullToRefresh.isRefreshing = false
                }

                is NetworkResult.Error -> {
                    spatoProgressBar.stop()
                    binding.pullToRefresh.isRefreshing = false
                    binding.noData.visibility = View.VISIBLE
                    binding.constraintLayout.visibility = View.INVISIBLE
                    Toast.makeText(activity, it.message.toString(), Toast.LENGTH_SHORT).show()
                }

                is NetworkResult.NoInternet -> {
                    spatoProgressBar.stop()
                    binding.pullToRefresh.isRefreshing = false
                    binding.noData.visibility = View.VISIBLE
                    binding.constraintLayout.visibility = View.INVISIBLE
                    Toast.makeText(activity, it.message.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        })

        Utils.swipeToDelete(requireContext(), binding.recyclerView, R.drawable.delete_in_red, R.color.transparent)
        Utils.position.observe(requireActivity(), Observer {
            if (liveDataObserver){
                mainViewModel.deleteCartItem(cartList[it].item_id)
                position = it
            }
        })

        mainViewModel.deleteCartItemResponse.observe(requireActivity(), Observer {
            when (it) {
                is NetworkResult.Loading -> {
                    spatoProgressBar.start()
                }

                is NetworkResult.Success -> {
                    spatoProgressBar.stop()
                    mainViewModel.cartList()
                    cartList.remove(cartList[position])
                    myCartAdapter.notifyItemRemoved(position)
                    myCartAdapter.notifyItemRangeChanged(position, cartList.size)
                    if (cartList.isEmpty() || cartList.size == 0) {
                        binding.noData.visibility = View.VISIBLE
                        binding.constraintLayout.visibility = View.GONE
                    }
                }

                is NetworkResult.Error -> {
                    spatoProgressBar.stop()
                    myCartAdapter.notifyDataSetChanged()
                    Toast.makeText(activity, it.message.toString(), Toast.LENGTH_SHORT).show()
                }

                is NetworkResult.NoInternet -> {
                    spatoProgressBar.stop()
                    myCartAdapter.notifyDataSetChanged()
                    Toast.makeText(activity, it.message.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        })

        mainViewModel.commonResponse.observe(requireActivity(), Observer {
            when (it) {
                is NetworkResult.Loading -> {
                    spatoProgressBar.start()
                }

                is NetworkResult.Success -> {
                    mainViewModel.cartList()
                }

                is NetworkResult.Error -> {
                    spatoProgressBar.stop()
                    Toast.makeText(activity, it.message.toString(), Toast.LENGTH_SHORT).show()
                }

                is NetworkResult.NoInternet -> {
                    spatoProgressBar.stop()
                    Toast.makeText(activity, it.message.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    override fun add(myCartItems: MyCartItems) {
        mainViewModel.updateCartItem(myCartItems.item_id, (myCartItems.qty+1), myCartItems.product_id)
    }

    override fun subtract(myCartItems: MyCartItems) {
        mainViewModel.updateCartItem(myCartItems.item_id, (myCartItems.qty-1), myCartItems.product_id)
    }

    override fun showMessage() {
        Toast.makeText(activity, getString(R.string.quantity_cant_be_less_than_one), Toast.LENGTH_SHORT).show()
    }

    override fun onDetach() {
        super.onDetach()
        spatoProgressBar.stop()
        liveDataObserver = false
    }
}