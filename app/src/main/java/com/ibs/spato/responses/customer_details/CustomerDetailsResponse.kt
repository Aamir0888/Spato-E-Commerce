package com.ibs.spato.responses.customer_details

data class CustomerDetailsResponse(
    val data: CustomerDetailsData,
    val message: String,
    val status: Int
)