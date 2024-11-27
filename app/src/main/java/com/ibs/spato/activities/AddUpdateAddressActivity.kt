package com.ibs.spato.activities

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hbb20.CountryCodePicker.OnCountryChangeListener
import com.ibs.spato.R
import com.ibs.spato.adapters.AddAddressStateListAdapter
import com.ibs.spato.databinding.ActivityAddUpdateAddressBinding
import com.ibs.spato.di.ApplicationComponentObject
import com.ibs.spato.responses.my_addresses.address_list.AddressListData
import com.ibs.spato.responses.my_addresses.get_region_id.GetRegionIdResponse
import com.ibs.spato.retrofit.NetworkResult
import com.ibs.spato.utilities.Constants
import com.ibs.spato.utilities.SpatoProgressBar
import com.ibs.spato.viewmodels.MainViewModel
import javax.inject.Inject

class AddUpdateAddressActivity : AppCompatActivity(), AddAddressStateListAdapter.StateItemClickListener {
    private lateinit var binding: ActivityAddUpdateAddressBinding
    private var countryNameCode = ""
    private lateinit var alertDialog: AlertDialog
    private lateinit var alertView: View
    private lateinit var recyclerView: RecyclerView
    @Inject
    lateinit var mainViewModel: MainViewModel
    @Inject
    lateinit var spatoProgressBar: SpatoProgressBar
    private lateinit var addAddressStateListAdapter: AddAddressStateListAdapter
    private lateinit var stateList: ArrayList<GetRegionIdResponse.Data.Region>
    private var regionId = 0
    private lateinit var selectedStateName: String
    private var addressId: Long? = null
    private lateinit var activityStatus: String
    private var isDeliverable = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddUpdateAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ApplicationComponentObject.create(this).injectAddUpdateAddress(this)
        activityStatus = intent.getStringExtra(Constants.ADD_UPDATE_ADDRESS_KEY).toString()
        if (activityStatus == Constants.UPDATE_ADDRESS){
            val updateData : AddressListData = intent.extras!!.getSerializable(Constants.UPDATE_ADDRESS) as AddressListData
            updateAddress(updateData)
        } else{
            addAddress()
        }
        observe()
        onClicks()
        dialog()
    }

    private fun addAddress() {
        binding.saveUpdate.text = getString(R.string.save_address)
        binding.addressAddUpdate.text = getString(R.string.add_new_address)
        countryNameCode = binding.ccp.selectedCountryNameCode
        apiCall()
    }

    private fun updateAddress(updateData: AddressListData) {
        binding.saveUpdate.text = getString(R.string.update_address)
        binding.addressAddUpdate.text = getString(R.string.update_address)
        addressId = updateData.addressId
        binding.ccp.setDefaultCountryUsingNameCode(updateData.country)
        binding.etFirstName.setText(updateData.fname)
        binding.etLastName.setText(updateData.lname)
        binding.etMobileNumber.setText(updateData.contactNo)
        binding.etPinCode.setText(updateData.pincode)
        binding.etAreaStreetSector.setText(updateData.address[0])
        binding.etTownCity.setText(updateData.city)
        binding.tvState.text = updateData.state
        selectedStateName = updateData.state
        countryNameCode = binding.ccp.selectedCountryNameCode
        apiCall()
    }

    private fun observe() {
        mainViewModel.regionIdResponse.observe(this, Observer {
            when (it) {
                is NetworkResult.Loading -> {
                    spatoProgressBar.start()
                }

                is NetworkResult.Success -> {
                    spatoProgressBar.stop()
                    if (it.data!!.data.regions.size > 0){
                        stateList = ArrayList()
                        isDeliverable = true
                        binding.state.visibility = View.VISIBLE
                        binding.llState.visibility = View.VISIBLE
                        stateList = it.data.data.regions
                        setStateData(stateList)
                    } else{
                        isDeliverable = false
                        binding.state.visibility = View.GONE
                        binding.llState.visibility = View.GONE
                        Toast.makeText(this, getString(R.string.delivery_is_not_allowed_to_selected_country), Toast.LENGTH_SHORT).show()
                    }
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

        mainViewModel.commonResponse.observe(this, Observer {
            when (it) {
                is NetworkResult.Loading -> {
                    spatoProgressBar.start()
                }

                is NetworkResult.Success -> {
                    spatoProgressBar.stop()
                    if (activityStatus == Constants.UPDATE_ADDRESS){
                        Toast.makeText(this, getString(R.string.address_updated_successfully), Toast.LENGTH_SHORT).show()
                    } else{
                        Toast.makeText(this, getString(R.string.address_added_successfully), Toast.LENGTH_SHORT).show()
                    }
                    val intent = Intent()
                    intent.putExtra(Constants.ADDRESS_ADDED_UPDATED_KEY, Constants.ADDRESS_ADDED_UPDATED_VALUE)
                    setResult(RESULT_OK, intent)
                    finish()
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

    private fun setStateData(stateList: java.util.ArrayList<GetRegionIdResponse.Data.Region>) {
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@AddUpdateAddressActivity)
            addAddressStateListAdapter = AddAddressStateListAdapter(this@AddUpdateAddressActivity, stateList, this@AddUpdateAddressActivity)
            adapter = addAddressStateListAdapter
        }
    }

    private fun dialog() {
        alertDialog = AlertDialog.Builder(this, R.style.CustomDialog).create()
        alertView = LayoutInflater.from(this).inflate(R.layout.add_address_state_recycler_item_dialog_layout, null)
        recyclerView = alertView.findViewById(R.id.recyclerView)
    }

    private fun apiCall() {
        mainViewModel.getRegionId(countryNameCode)
    }

    private fun onClicks() {
        binding.backButton.setOnClickListener {
            finish()
        }

        binding.ccp.setOnCountryChangeListener(OnCountryChangeListener {
            countryNameCode = binding.ccp.selectedCountryNameCode
            binding.tvState.text = getString(R.string.choose_a_state)
            apiCall()
        })

        binding.llState.setOnClickListener {
            setStateData(stateList)
            alertDialog.setView(alertView)
            alertDialog.show()
        }

        binding.llSaveAddress.setOnClickListener {
            if (isDeliverable){
                if (binding.etFirstName.text.toString().trim().isNullOrEmpty()) {
                    binding.etFirstName.requestFocus()
                    binding.etFirstName.error = getString(R.string.enter_your_first_name)
                    return@setOnClickListener
                } else if (binding.etLastName.text.toString().trim().isNullOrEmpty()) {
                    binding.etLastName.requestFocus()
                    binding.etLastName.error = getString(R.string.enter_your_last_name)
                    return@setOnClickListener
                } else if (binding.etMobileNumber.text.toString().trim().isNullOrEmpty()) {
                    binding.etMobileNumber.requestFocus()
                    binding.etMobileNumber.error = getString(R.string.enter_your_mobile_number)
                    return@setOnClickListener
                } else if (binding.etPinCode.text.toString().trim().isNullOrEmpty()) {
                    binding.etPinCode.requestFocus()
                    binding.etPinCode.error = getString(R.string.enter_your_6_digit_pincode)
                    return@setOnClickListener
                } else if (binding.etPinCode.text.toString().trim().length < 6) {
                    binding.etPinCode.requestFocus()
                    binding.etPinCode.error = getString(R.string.enter_a_valid_pincode)
                    return@setOnClickListener
                } else if (binding.etAreaStreetSector.text.toString().trim().isNullOrEmpty()) {
                    binding.etAreaStreetSector.requestFocus()
                    binding.etAreaStreetSector.error = getString(R.string.enter_your_flat_company_street_sector_village)
                    return@setOnClickListener
                } else if (binding.etTownCity.text.toString().trim().isNullOrEmpty()) {
                    binding.etTownCity.requestFocus()
                    binding.etTownCity.error = getString(R.string.enter_your_town_city)
                    return@setOnClickListener
                } else if (binding.tvState.text.toString().trim().isNullOrEmpty()) {
                    Toast.makeText(this, getString(R.string.please_choose_a_state), Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                } else {
                    mainViewModel.addAddress(binding.etFirstName.text.toString(), binding.etLastName.text.toString(),
                        binding.etAreaStreetSector.text.toString(), binding.etTownCity.text.toString(),
                        selectedStateName, binding.etPinCode.text.toString().toLong(), countryNameCode,
                        binding.etMobileNumber.text.toString(), regionId.toLong(), addressId)
                }
            } else{
                Toast.makeText(this, getString(R.string.delivery_is_not_allowed_to_selected_country), Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun stateClicked(regions: GetRegionIdResponse.Data.Region) {
        regionId = regions.value
        selectedStateName = regions.label
        binding.tvState.text = regions.label
        alertDialog.dismiss()
    }
}