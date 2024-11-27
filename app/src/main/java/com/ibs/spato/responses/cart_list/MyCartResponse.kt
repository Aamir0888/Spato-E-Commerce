package com.ibs.spato.responses.cart_list

data class MyCartResponse(
    val data: MyCartData,
    val message: String,
    val status: Int
)