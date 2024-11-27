package com.ibs.spato.responses.my_addresses.get_region_id

data class GetRegionIdResponse(
    val data: Data,
    val message: String,
    val status: Int) {

    data class Data(
        val regions: ArrayList<Region>) {

        data class Region(
            val label: String,
            val  value: Int,
            var selectedOrNot: Boolean = false
        )
    }
}