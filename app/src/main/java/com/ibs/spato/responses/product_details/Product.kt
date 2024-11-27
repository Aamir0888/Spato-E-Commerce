package com.ibs.spato.responses.product_details

data class Product(
    val description: String,
    val final_rating: List<Any>,
    val id: String,
    val image: List<String>,
    val pdf: ArrayList<String>,
    val specification: ArrayList<String>,
    val is_in_stock: Boolean,
    val name: String,
    val old_price: String,
    val price: String,
    val product_code: String,
    val product_type: String,
    val qty: Int,
    val qty_ordered: Any,
    val selfrating: Int,
    val shortdescription: String,
    val sku: String,
    val totalrating: Int,
    val wishlist: Int
)