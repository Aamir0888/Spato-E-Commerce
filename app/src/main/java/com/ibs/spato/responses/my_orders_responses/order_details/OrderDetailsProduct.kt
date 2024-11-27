package com.ibs.spato.responses.my_orders_responses.order_details

data class OrderDetailsProduct(
    val coupon_discount_refund: Any,
    val description: String,
    val image: String,
    val item_id: String,
    val mrp: String,
    val name: String,
    val original_price: String,
    val packaging_size: PackagingSize,
    val price: String,
    val product_id: String,
    val product_weight: String,
    val qty: Int,
    val shortdescription: Any
)