package com.ibs.spato.responses.my_orders_responses.track_order

data class Data(
    val customer_name: String,
    val date: String,
    val grand_total: String,
    val order_id: String,
    val paymentmethod: String,
    val product: List<Product>,
    val shipping_amount: String,
    val shippingmethod: String,
    val state: String,
    val status: String,
    val subtotal: String,
    val updated_date: String
)