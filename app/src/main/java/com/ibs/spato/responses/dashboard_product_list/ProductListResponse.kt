package com.ibs.spato.responses.dashboard_product_list

data class ProductListResponse(
    val data: ProductListData,
    val message: String,
    val status: Int
)