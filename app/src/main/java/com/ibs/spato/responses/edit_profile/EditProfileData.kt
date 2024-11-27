package com.ibs.spato.responses.edit_profile

data class EditProfileData(
    val email: String,
    val entity_id: String,
    val first_name: String,
    val last_name: String,
    val mobile_no: String,
    val profile_image: String
)