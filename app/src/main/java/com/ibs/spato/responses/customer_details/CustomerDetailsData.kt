package com.ibs.spato.responses.customer_details

import java.io.Serializable

data class CustomerDetailsData(
    val block: String,
    val city: Any,
    val country_id: Any,
    val customer_type: String,
    val profile_pic: String,
    val email: String,
    val first_name: String,
    val last_name: String,
    val mobile: String,
    val panchayat: String,
    val postcode: Any,
    val region: Any,
    val userid: String,
    val village: String
) : Serializable