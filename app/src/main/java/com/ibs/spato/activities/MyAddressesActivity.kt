package com.ibs.spato.activities

import android.annotation.SuppressLint
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
import com.ibs.spato.adapters.MyAddressesAdapter
import com.ibs.spato.databinding.ActivityMyAddressesBinding
import com.ibs.spato.di.ApplicationComponentObject
import com.ibs.spato.responses.my_addresses.address_list.AddressListData
import com.ibs.spato.retrofit.NetworkResult
import com.ibs.spato.utilities.Constants
import com.ibs.spato.utilities.SpatoProgressBar
import com.ibs.spato.utilities.Utils
import com.ibs.spato.viewmodels.MainViewModel
import javax.inject.Inject

class MyAddressesActivity : AppCompatActivity(), MyAddressesAdapter.ClickListener {
    private lateinit var binding: ActivityMyAddressesBinding
    private lateinit var myAddressesAdapter: MyAddressesAdapter
    private lateinit var myAddressList : ArrayList<AddressListData>
    @Inject
    lateinit var mainViewModel: MainViewModel
    @Inject
    lateinit var spatoProgressBar: SpatoProgressBar
    private var loading = true
    private var position = 0
    private var liveDataObserver = false

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyAddressesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ApplicationComponentObject.create(this).injectMyAddress(this)
        apiCall()
        observe()

        binding.pullToRefresh.setOnRefreshListener {
            binding.pullToRefresh.isRefreshing = true
            apiCall()
        }

        binding.llAddNewAddress.setOnClickListener {
            val intent = Intent(this, AddUpdateAddressActivity::class.java)
            intent.putExtra(Constants.ADD_UPDATE_ADDRESS_KEY, Constants.ADD_ADDRESS)
            resultLauncher.launch(intent)
        }

        binding.backButton.setOnClickListener {
            finish()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
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
                    binding.llAddNewAddress.visibility = View.VISIBLE
                    if (it.data!!.data.size > 0) {
                        binding.noData.visibility = View.INVISIBLE
                        binding.recyclerView.visibility = View.VISIBLE
                        myAddressList = ArrayList()
                        liveDataObserver = true
                        myAddressList = it.data.data
                        binding.recyclerView.apply {
                            layoutManager = LinearLayoutManager(this@MyAddressesActivity)
                            myAddressesAdapter = MyAddressesAdapter(this@MyAddressesActivity, myAddressList, this@MyAddressesActivity)
                            adapter = myAddressesAdapter
                        }
                    } else {
                        binding.noData.visibility = View.VISIBLE
                        binding.recyclerView.visibility = View.INVISIBLE
                    }
                }

                is NetworkResult.Error -> {
                    spatoProgressBar.stop()
                    binding.pullToRefresh.isRefreshing = false
                    binding.noData.visibility = View.VISIBLE
                    binding.recyclerView.visibility = View.INVISIBLE
                    binding.llAddNewAddress.visibility = View.VISIBLE
                    Toast.makeText(this, it.message.toString(), Toast.LENGTH_SHORT).show()
                }

                is NetworkResult.NoInternet -> {
                    spatoProgressBar.stop()
                    binding.pullToRefresh.isRefreshing = false
                    binding.noData.visibility = View.VISIBLE
                    binding.recyclerView.visibility = View.INVISIBLE
                    binding.llAddNewAddress.visibility = View.INVISIBLE
                    Toast.makeText(this, it.message.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        })

        Utils.swipeToDelete(this, binding.recyclerView, R.drawable.delete_in_red, R.color.transparent)
        Utils.position.observe(this, Observer {
            if (liveDataObserver){
                mainViewModel.deleteAddress(myAddressList[it].addressId)
                position = it
            }
        })

        mainViewModel.commonResponse.observe(this, Observer {
            when (it) {
                is NetworkResult.Loading -> {
                    spatoProgressBar.start()
                }

                is NetworkResult.Success -> {
                    spatoProgressBar.stop()
                    myAddressList.remove(myAddressList[position])
                    myAddressesAdapter.notifyItemRemoved(position)
                    myAddressesAdapter.notifyItemRangeChanged(position, myAddressList.size)
                    if (myAddressList.isEmpty() || myAddressList.size == 0) {
                        binding.noData.visibility = View.VISIBLE
                        binding.recyclerView.visibility = View.INVISIBLE
                    }
                }

                is NetworkResult.Error -> {
                    spatoProgressBar.stop()
                    myAddressesAdapter.notifyDataSetChanged()
                    Toast.makeText(this, it.message.toString(), Toast.LENGTH_SHORT).show()
                }

                is NetworkResult.NoInternet -> {
                    spatoProgressBar.stop()
                    myAddressesAdapter.notifyDataSetChanged()
                    Toast.makeText(this, it.message.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun apiCall() {
        mainViewModel.myAddressList()
    }

    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            if (data!!.getStringExtra(Constants.ADDRESS_ADDED_UPDATED_KEY) == Constants.ADDRESS_ADDED_UPDATED_VALUE){
                apiCall()
            }
        }
    }

    override fun onClick(addressListData: AddressListData) {
        val intent = Intent(this, AddUpdateAddressActivity::class.java)
        val bundle = Bundle()
        intent.putExtra(Constants.ADD_UPDATE_ADDRESS_KEY, Constants.UPDATE_ADDRESS)
        bundle.putSerializable(Constants.UPDATE_ADDRESS, addressListData)
        intent.putExtras(bundle)
        resultLauncher.launch(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        liveDataObserver = false
    }
}