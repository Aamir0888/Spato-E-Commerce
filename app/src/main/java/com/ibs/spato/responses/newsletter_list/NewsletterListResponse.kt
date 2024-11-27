package com.ibs.spato.responses.newsletter_list

data class NewsletterListResponse(
    val status: Int,
    val message: String,
    val data: ArrayList<Data>
) {
    data class Data(
        val entity_id: Long,
        val title: String,
        val message: String,
        val date: String
    )
}