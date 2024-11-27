package com.ibs.spato.responses.dashboard_product_list

import java.io.Serializable

data class Products(
    val image: ArrayList<String>,
    val name: String,
    val old_price: Double,
    val price: Double,
    val product_id: Long,
    val product_type: String,
    val sku: String,
    val wishlist: Int
) : Serializable