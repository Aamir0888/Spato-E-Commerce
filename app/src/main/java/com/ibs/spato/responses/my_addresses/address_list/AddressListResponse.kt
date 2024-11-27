package com.ibs.spato.responses.my_addresses.address_list

data class AddressListResponse(
    val data: ArrayList<AddressListData>,
    val message: String,
    val status: Int
)