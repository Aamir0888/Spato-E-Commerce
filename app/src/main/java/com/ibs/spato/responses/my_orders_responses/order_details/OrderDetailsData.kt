package com.ibs.spato.responses.my_orders_responses.order_details

data class OrderDetailsData(
    val arriving_on: String,
    val billingaddress: Billingaddress,
    val coupon_code: String,
    val customer_email: String,
    val customer_id: String,
    val customer_name: String,
    val date: String,
    val discount_description: Any,
    val increment_id: String,
    val order_id: String,
    val paymentmethod: String,
    val product: List<OrderDetailsProduct>,
    val set_status: String,
    val shippingMethod: String,
    val shipping_amount: String,
    val shippingaddress: Shippingaddress,
    val subtotal: String,
    val total: String,
    val total_discount_amount: String
)