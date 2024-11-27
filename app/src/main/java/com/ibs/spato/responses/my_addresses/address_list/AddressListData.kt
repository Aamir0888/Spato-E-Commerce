package com.ibs.spato.responses.my_addresses.address_list

import java.io.Serializable

data class AddressListData(
    val address: ArrayList<String>,
    val addressId: Long,
    val city: String,
    val company: String,
    val contactNo: String,
    val country: String,
    val defaultBillingAddress: String,
    val defaultShippingAddress: String,
    val fname: String,
    val lname: String,
    val pincode: String,
    val regionId: String,
    val state: String,
    var selectedOrNot: Boolean = false
) : Serializable