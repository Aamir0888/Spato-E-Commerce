package com.ibs.spato.activities

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.ibs.spato.R
import com.ibs.spato.adapters.MyCartAdapter
import com.ibs.spato.databinding.ActivityMyCartBinding
import com.ibs.spato.di.ApplicationComponentObject
import com.ibs.spato.responses.cart_list.MyCartItems
import com.ibs.spato.retrofit.NetworkResult
import com.ibs.spato.utilities.SpatoProgressBar
import com.ibs.spato.utilities.Utils
import com.ibs.spato.viewmodels.MainViewModel
import javax.inject.Inject

class MyCartActivity : AppCompatActivity(), MyCartAdapter.CartItemClickListener{
    private lateinit var binding: ActivityMyCartBinding
    @Inject
    lateinit var mainViewModel: MainViewModel
    @Inject
    lateinit var spatoProgressBar: SpatoProgressBar
    private lateinit var myCartAdapter: MyCartAdapter
    private lateinit var cartList: ArrayList<MyCartItems>
    private var loading = true
    private var position = 0
    private var liveDataObserver = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ApplicationComponentObject.create(this).injectMyCart(this)
        observe()
        onClicks()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observe() {
        mainViewModel.cartList()
        mainViewModel.cartListResponse.observe(this, Observer {
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
                    if (it.data!!.data.items.size > 0) {
                        binding.noData.visibility = View.GONE
                        binding.constraintLayout.visibility = View.VISIBLE
                        cartList = ArrayList()
                        liveDataObserver = true
                        cartList = it.data.data.items
                        binding.totalAmount.text = it.data.data.getGrandTotal.toString()
                        binding.recyclerView.apply {
                            isNestedScrollingEnabled = false
                            layoutManager = LinearLayoutManager(this@MyCartActivity)
                            myCartAdapter = MyCartAdapter(this@MyCartActivity, cartList, this@MyCartActivity)
                            adapter = myCartAdapter
                        }
                    } else {
                        binding.noData.visibility = View.VISIBLE
                        binding.constraintLayout.visibility = View.INVISIBLE
                    }
                }

                is NetworkResult.Error -> {
                    spatoProgressBar.stop()
                    binding.pullToRefresh.isRefreshing = false
                    binding.noData.visibility = View.VISIBLE
                    binding.constraintLayout.visibility = View.INVISIBLE
                    Toast.makeText(this, it.message.toString(), Toast.LENGTH_SHORT).show()
                }

                is NetworkResult.NoInternet -> {
                    spatoProgressBar.stop()
                    binding.pullToRefresh.isRefreshing = false
                    binding.noData.visibility = View.VISIBLE
                    binding.constraintLayout.visibility = View.INVISIBLE
                    Toast.makeText(this, it.message.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        })

        Utils.swipeToDelete(this, binding.recyclerView, R.drawable.delete_in_red, R.color.transparent)
        Utils.position.observe(this, Observer {
            if (liveDataObserver){
                mainViewModel.deleteCartItem(cartList[it].item_id)
                position = it
            }
        })

        mainViewModel.deleteCartItemResponse.observe(this, Observer {
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
                    Toast.makeText(this, it.message.toString(), Toast.LENGTH_SHORT).show()
                }

                is NetworkResult.NoInternet -> {
                    spatoProgressBar.stop()
                    myCartAdapter.notifyDataSetChanged()
                    Toast.makeText(this, it.message.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        })

        mainViewModel.commonResponse.observe(this, Observer {
            when (it) {
                is NetworkResult.Loading -> {
                    spatoProgressBar.start()
                }

                is NetworkResult.Success -> {
                    mainViewModel.cartList()
                }

                is NetworkResult.Error -> {
                    spatoProgressBar.stop()
                    Toast.makeText(this, it.message.toString(), Toast.LENGTH_SHORT).show()
                }

                is NetworkResult.NoInternet -> {
                    spatoProgressBar.stop()
                    Toast.makeText(this, it.message.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun onClicks() {
        binding.backButton.setOnClickListener {
            finish()
        }

        binding.pullToRefresh.setOnRefreshListener {
            binding.pullToRefresh.isRefreshing = true
            mainViewModel.cartList()
        }
    }

    override fun add(myCartItems: MyCartItems) {
        mainViewModel.updateCartItem(myCartItems.item_id, (myCartItems.qty+1), myCartItems.product_id)
    }

    override fun subtract(myCartItems: MyCartItems) {
        mainViewModel.updateCartItem(myCartItems.item_id, (myCartItems.qty-1), myCartItems.product_id)
    }

    override fun showMessage() {
        Toast.makeText(this, getString(R.string.quantity_cant_be_less_than_one), Toast.LENGTH_SHORT).show()
    }
}