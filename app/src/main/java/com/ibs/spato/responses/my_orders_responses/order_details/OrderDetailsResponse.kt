package com.ibs.spato.responses.my_orders_responses.order_details

data class OrderDetailsResponse(
    val data: OrderDetailsData,
    val message: String,
    val status: Int
)