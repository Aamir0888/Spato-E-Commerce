package com.ibs.spato.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.ibs.spato.R
import com.ibs.spato.adapters.TrackOrderAdapter
import com.ibs.spato.databinding.ActivityTrackOrderBinding
import com.ibs.spato.di.ApplicationComponentObject
import com.ibs.spato.responses.my_orders_responses.order_details.OrderDetailsProduct
import com.ibs.spato.responses.my_orders_responses.track_order.Track
import com.ibs.spato.retrofit.NetworkResult
import com.ibs.spato.utilities.Constants
import com.ibs.spato.utilities.SpatoProgressBar
import com.ibs.spato.utilities.Utils
import com.ibs.spato.viewmodels.MainViewModel
import javax.inject.Inject

class TrackOrderActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTrackOrderBinding
    @Inject
    lateinit var mainViewModel: MainViewModel
    @Inject
    lateinit var spatoProgressBar: SpatoProgressBar
    private var orderId: Long = 0
    private var loading = true
    private lateinit var trackOrderAdapter: TrackOrderAdapter
    private lateinit var trackList: ArrayList<Track>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrackOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ApplicationComponentObject.create(this).injectTrackOrder(this)
        orderId = intent.getLongExtra(Constants.ORDER_ID, 0)
        apiCall()
        observe()
        onClicks()

        binding.pullToRefresh.setOnRefreshListener {
            binding.pullToRefresh.isRefreshing = true
            apiCall()
        }
    }

    private fun onClicks() {
        binding.backButton.setOnClickListener {
            finish()
        }
        binding.topView.setOnClickListener {
            //  do nothing
        }
    }

    private fun observe() {
        mainViewModel.trackOrderResponse.observe(this, Observer {
            when (it) {
                is NetworkResult.Loading -> {
                    if (loading) {
                        loading = false
                        spatoProgressBar.start()
                    }
                }

                is NetworkResult.Success -> {
                    if (it.data!!.data.product[0].track.isNotEmpty()){
                        trackList = ArrayList()
                        val product = it.data.data.product[0]
                        trackList = product.track
                        Utils.loadImage(this, product.image, binding.productImage)
                        binding.productName.text = product.name
                        binding.productPrice.text = product.price.toString()
                        binding.recyclerView.apply {
                            isNestedScrollingEnabled = false
                            layoutManager = LinearLayoutManager(this@TrackOrderActivity)
                            trackOrderAdapter = TrackOrderAdapter(this@TrackOrderActivity, trackList)
                            adapter = trackOrderAdapter
                        }
                        stopProgressBar(Constants.SUCCESS)
                    } else{
                        stopProgressBar(Constants.FAILURE)
                    }

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
        mainViewModel.trackOrder(orderId)
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