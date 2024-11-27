package com.ibs.spato.responses.cart_list

data class MyCartItems(
    val image: String,
    val item_id: Long,
    val name: String,
    val price: String,
    val product_id: Long,
    val product_type: String,
    val qty: Int,
    val subtotal: String
)