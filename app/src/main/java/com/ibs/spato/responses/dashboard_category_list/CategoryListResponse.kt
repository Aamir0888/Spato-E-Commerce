package com.ibs.spato.responses.dashboard_category_list

import java.io.Serializable

data class CategoryListResponse(
    val status: Int,
    val message: String,
    val data: Data) {
    data class Data(val category: ArrayList<Category>) {
        data class Category(
            val name: String,
            val id: Long,
            val image: String) : Serializable
    }
}