package com.ibs.spato.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.ibs.spato.R
import com.ibs.spato.adapters.ChooseAddressAdapter
import com.ibs.spato.adapters.MyAddressesAdapter
import com.ibs.spato.databinding.ActivityChooseAddressBinding
import com.ibs.spato.di.ApplicationComponentObject
import com.ibs.spato.responses.my_addresses.address_list.AddressListData
import com.ibs.spato.retrofit.NetworkResult
import com.ibs.spato.utilities.Constants
import com.ibs.spato.utilities.SpatoProgressBar
import com.ibs.spato.utilities.Utils
import com.ibs.spato.viewmodels.MainViewModel
import javax.inject.Inject

class ChooseAddressActivity : AppCompatActivity(), ChooseAddressAdapter.ClickListener {
    private lateinit var binding: ActivityChooseAddressBinding
    @Inject
    lateinit var mainViewModel: MainViewModel
    @Inject
    lateinit var spatoProgressBar: SpatoProgressBar
    private var loading = true
    private lateinit var myAddressList : ArrayList<AddressListData>
    private lateinit var chooseAddressAdapter: ChooseAddressAdapter
    private var selectedAddressId: Long = -1
    private var cartId: Long = 0
    private var totalAmount: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChooseAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ApplicationComponentObject.create(this).injectChooseAddress(this)
        cartId = intent.getLongExtra(Constants.CART_ID, 0)
        totalAmount = intent.getDoubleExtra(Constants.TOTAL_AMOUNT,0.0)
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

        binding.addAddress.setOnClickListener {
            val intent = Intent(this, AddUpdateAddressActivity::class.java)
            intent.putExtra(Constants.ADD_UPDATE_ADDRESS_KEY, Constants.ADD_ADDRESS)
            resultLauncher.launch(intent)
        }

        binding.llNext.setOnClickListener {
            if (selectedAddressId.compareTo(-1) == 0){
                Toast.makeText(this, getString(R.string.please_select_any_address), Toast.LENGTH_SHORT).show()
            } else{
                val intent = Intent(this, ChoosePaymentMethodActivity::class.java)
                intent.putExtra(Constants.CART_ID, cartId)
                intent.putExtra(Constants.TOTAL_AMOUNT, totalAmount)
                intent.putExtra(Constants.ADDRESS_ID, selectedAddressId)
                startActivity(intent)
             }
        }
    }

    private fun apiCall() {
        mainViewModel.myAddressList()
    }

    private fun observe() {
        mainViewModel.addressListResponse.observe(this, Observer {
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
                        binding.noData.visibility = View.INVISIBLE
                        binding.recyclerView.visibility = View.VISIBLE
                        binding.backButton.visibility = View.VISIBLE
                        binding.addAddress.visibility = View.VISIBLE
                        binding.chooseAddressText.visibility = View.VISIBLE
                        binding.llNext.visibility = View.VISIBLE
                        myAddressList = ArrayList()
                        myAddressList = it.data.data
                        binding.recyclerView.apply {
                            layoutManager = LinearLayoutManager(this@ChooseAddressActivity)
                            chooseAddressAdapter = ChooseAddressAdapter(this@ChooseAddressActivity, myAddressList, this@ChooseAddressActivity)
                            adapter = chooseAddressAdapter
                        }
                    } else {
                        binding.noData.visibility = View.VISIBLE
                        binding.recyclerView.visibility = View.INVISIBLE
                        binding.backButton.visibility = View.VISIBLE
                        binding.addAddress.visibility = View.VISIBLE
                        binding.chooseAddressText.visibility = View.VISIBLE
                        binding.llNext.visibility = View.INVISIBLE
                    }
                }

                is NetworkResult.Error -> {
                    spatoProgressBar.stop()
                    binding.pullToRefresh.isRefreshing = false
                    binding.noData.visibility = View.VISIBLE
                    binding.recyclerView.visibility = View.INVISIBLE
                    binding.backButton.visibility = View.VISIBLE
                    binding.addAddress.visibility = View.VISIBLE
                    binding.chooseAddressText.visibility = View.VISIBLE
                    binding.llNext.visibility = View.INVISIBLE
                    Toast.makeText(this, it.message.toString(), Toast.LENGTH_SHORT).show()
                }

                is NetworkResult.NoInternet -> {
                    spatoProgressBar.stop()
                    binding.pullToRefresh.isRefreshing = false
                    binding.noData.visibility = View.VISIBLE
                    binding.recyclerView.visibility = View.INVISIBLE
                    binding.backButton.visibility = View.VISIBLE
                    binding.addAddress.visibility = View.INVISIBLE
                    binding.chooseAddressText.visibility = View.VISIBLE
                    binding.llNext.visibility = View.INVISIBLE
                    Toast.makeText(this, it.message.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    override fun onClick(addressId: Long) {
        selectedAddressId = addressId
    }

    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            if (data!!.getStringExtra(Constants.ADDRESS_ADDED_UPDATED_KEY) == Constants.ADDRESS_ADDED_UPDATED_VALUE){
                apiCall()
            }
        }
    }
}