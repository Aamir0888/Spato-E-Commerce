package com.ibs.spato.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import com.ibs.spato.databinding.ActivityOrderDetailsBinding
import com.ibs.spato.di.ApplicationComponentObject
import com.ibs.spato.responses.my_orders_responses.order_details.OrderDetailsProduct
import com.ibs.spato.retrofit.NetworkResult
import com.ibs.spato.utilities.Constants
import com.ibs.spato.utilities.SpatoProgressBar
import com.ibs.spato.utilities.Utils
import com.ibs.spato.viewmodels.MainViewModel
import javax.inject.Inject

class OrderDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOrderDetailsBinding
    @Inject
    lateinit var mainViewModel: MainViewModel
    @Inject
    lateinit var spatoProgressBar: SpatoProgressBar
    private var loading = true
    private var orderId: Long = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ApplicationComponentObject.create(this).injectOrderDetails(this)
        orderId = intent.getLongExtra(Constants.ORDER_ID, 0)
        apiCall()
        observe()
        onClicks()

        binding.pullToRefresh.setOnRefreshListener {
            binding.pullToRefresh.isRefreshing = true
            apiCall()
        }
    }

    private fun observe() {
        mainViewModel.orderDetailsResponse.observe(this, Observer {
            when (it) {
                is NetworkResult.Loading -> {
                    if (loading) {
                        loading = false
                        spatoProgressBar.start()
                    }
                }

                is NetworkResult.Success -> {
                    val product: OrderDetailsProduct = it.data!!.data.product[0]
                    Utils.loadImage(this, product.image, binding.productImage)
                    binding.productName.text = product.name
                    binding.productPrice.text = product.price
                    binding.productDescription.text = product.description
                    binding.dateTime.text = it.data.data.date
                    stopProgressBar(Constants.SUCCESS)
                }

                is NetworkResult.Error -> {
                    stopProgressBar(Constants.FAILURE)
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }

                is NetworkResult.NoInternet -> {
                    stopProgressBar(Constants.FAILURE)
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun apiCall() {
        mainViewModel.orderDetails(orderId)
    }

    private fun onClicks() {
        binding.backButton.setOnClickListener {
            finish()
        }
//        binding.llTrackYourShipment.setOnClickListener {
//            val intent = Intent(this, TrackOrderActivity::class.java)
//            intent.putExtra(Constants.ORDER_ID, orderId)
//            startActivity(intent)
//        }

        binding.topView.setOnClickListener {
            // do nothing
        }
    }

    private fun stopProgressBar(status: String) {
        spatoProgressBar.stop()
        binding.pullToRefresh.isRefreshing = false
        if (status == Constants.FAILURE){
            binding.topView.visibility = View.VISIBLE
            binding.noData.visibility = View.VISIBLE
        } else if (status == Constants.SUCCESS){
            binding.topView.visibility = View.INVISIBLE
            binding.noData.visibility = View.INVISIBLE
        }
    }
}