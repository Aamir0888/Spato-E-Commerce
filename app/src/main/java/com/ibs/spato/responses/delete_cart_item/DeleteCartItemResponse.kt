package com.ibs.spato.responses.delete_cart_item

data class DeleteCartItemResponse(
    val data: DeleteCartItemData,
    val message: String,
    val status: Int
)