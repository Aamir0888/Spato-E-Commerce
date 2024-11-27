package com.ibs.spato.responses.common_response

data class CommonResponse(
    val data: ArrayList<Any>,
    val message: String,
    val status: Int
)
