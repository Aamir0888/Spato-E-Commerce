package com.ibs.spato.responses.models.request_product

data class RequestProductModel(
    val contact_name: String,
    val phone: String,
    val email: String,
    val product_title: String,
    val message: String,
    val image: String
)