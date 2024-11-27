package com.ibs.spato.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ibs.spato.R
import com.ibs.spato.databinding.MyAddressesRecyclerSingleItemLayoutBinding
import com.ibs.spato.responses.my_addresses.address_list.AddressListData

class ChooseAddressAdapter(private val context: Context, private val ordersList: ArrayList<AddressListData>,
                           private val clickListener : ClickListener):
        RecyclerView.Adapter<ChooseAddressAdapter.MyViewHolder>(){

    inner class MyViewHolder(binding: MyAddressesRecyclerSingleItemLayoutBinding) : RecyclerView.ViewHolder(binding.root){
        private val customerName = binding.customerName
        private val address = binding.address
        private val cityStatePin = binding.cityStatePin
        private val country = binding.country
        private val contactNumber = binding.contactNumber
        private val llSingleItem = binding.llSingleItem
        private val edit = binding.edit

        @SuppressLint("SetTextI18n")
        fun bind(addressListData: AddressListData, ordersList: ArrayList<AddressListData>){
            edit.visibility = View.GONE

            if (addressListData.selectedOrNot){
                llSingleItem.setBackgroundResource(R.drawable.selected_payment_address_background)
            } else{
                llSingleItem.setBackgroundResource(R.drawable.my_addresses_single_item_background)
            }
            customerName.text = "${addressListData.fname} ${addressListData.lname}"
            if (addressListData.address.size > 0){
                address.visibility = View.VISIBLE
                address.text = addressListData.address[0]
            } else{
                address.visibility = View.GONE
            }
            cityStatePin.text = "${addressListData.city}, ${addressListData.state}, ${addressListData.pincode}"
            country.text = addressListData.country
            contactNumber.text = addressListData.contactNo

            llSingleItem.setOnClickListener {
                llSingleItem.setBackgroundResource(R.drawable.selected_payment_address_background)
                clickListener.onClick(addressListData.addressId)
                for (i in 0 until ordersList.size){
                    ordersList[i].selectedOrNot = false
                }
                addressListData.selectedOrNot = true
                notifyDataSetChanged()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = MyAddressesRecyclerSingleItemLayoutBinding.inflate(LayoutInflater.from(context), parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return ordersList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(ordersList[position], ordersList)
    }

    interface ClickListener{
        fun onClick(addressId: Long)
    }
}