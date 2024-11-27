package com.ibs.spato.responses.product_details

data class ProductDetailsResponse(
    val data: ProductDetailsData,
    val message: String,
    val status: Int
)