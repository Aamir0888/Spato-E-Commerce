package com.ibs.spato.responses.cart_list

data class MyCartData(
    val AppliedCoupans: Any,
    val cart_id: Long,
    val cartcount: String,
    val discount_amount: Double,
    val getGrandTotal: Double,
    val items: ArrayList<MyCartItems>,
    val shipping_cost: Int,
    val shipping_method: Any,
    val subtotal: Any
)