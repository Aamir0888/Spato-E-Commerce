package com.ibs.spato.responses.my_orders_responses.all_orders

import java.io.Serializable

data class MyOrdersProduct(
    val coupon_discount_refund: Any,
    val description: String,
    val image: String,
    val item_id: String,
    val mrp: String,
    val name: String,
    val original_price: Double,
    val packaging_size: PackagingSize,
    val price: Double,
    val product_id: Long,
    val product_weight: String,
    val qty: Int,
    val shortdescription: String
) : Serializable