package com.ibs.spato.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ibs.spato.databinding.MyAddressesRecyclerSingleItemLayoutBinding
import com.ibs.spato.responses.my_addresses.address_list.AddressListData

class MyAddressesAdapter(private val context: Context, private val ordersList: ArrayList<AddressListData>,
                         private val clickListener : ClickListener):
        RecyclerView.Adapter<MyAddressesAdapter.MyViewHolder>(){

    inner class MyViewHolder(binding: MyAddressesRecyclerSingleItemLayoutBinding) : RecyclerView.ViewHolder(binding.root){
        private val customerName = binding.customerName
        private val address = binding.address
        private val cityStatePin = binding.cityStatePin
        private val country = binding.country
        private val contactNumber = binding.contactNumber
        private val llSingleItem = binding.llSingleItem

        @SuppressLint("SetTextI18n")
        fun bind(addressListData: AddressListData){
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
                clickListener.onClick(addressListData)
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
        holder.bind(ordersList[position])
    }

    interface ClickListener{
        fun onClick(addressListData: AddressListData)
    }
}