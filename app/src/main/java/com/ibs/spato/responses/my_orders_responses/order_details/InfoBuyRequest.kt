package com.ibs.spato.responses.my_orders_responses.order_details

data class InfoBuyRequest(
    val item: String,
    val product: String,
    val qty: String,
    val related_product: String,
    val selected_configurable_option: String,
    val uenc: String
)