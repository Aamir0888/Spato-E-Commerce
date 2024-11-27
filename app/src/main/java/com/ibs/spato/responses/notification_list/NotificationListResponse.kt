package com.ibs.spato.responses.notification_list

data class NotificationListResponse(
    val data: ArrayList<Data>,
    val message: String,
    val status: Int
) {
    data class Data(
        val customer_id: String,
        val date: String,
        val entity_id: Long,
        val message: String,
        val title: String
    )
}