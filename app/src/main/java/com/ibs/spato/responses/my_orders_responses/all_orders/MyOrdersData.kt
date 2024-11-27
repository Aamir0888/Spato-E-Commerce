package com.ibs.spato.responses.my_orders_responses.all_orders

import java.io.Serializable

data class MyOrdersData(
    val date: String,
    val increment_id: String,
    val item_count: Int,
    val order_id: Long,
    val product: ArrayList<MyOrdersProduct>,
    val state: String,
    val status: String,
    val total: String,
    val updated_date: String
) : Serializable