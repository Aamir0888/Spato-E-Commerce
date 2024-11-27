package com.ibs.spato.responses.order_place

data class OrderPlaceResponse(
    val `data`: Data,
    val message: String,
    val status: Int
) {
    data class Data(
        val order_id: String
    )
}