package com.ibs.spato.responses.my_orders_responses.all_orders

data class MyOrdersResponse(
    val data: ArrayList<MyOrdersData>,
    val message: String,
    val status: Int
)