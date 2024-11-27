package com.ibs.spato.responses.my_orders_responses.track_order

data class Product(
    val image: String,
    val name: String,
    val price: Double,
    val qty: Int,
    val sku: String,
    val track: ArrayList<Track>
)