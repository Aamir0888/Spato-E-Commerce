package com.ibs.spato.responses.edit_profile

data class EditProfileResponse(
    val data: List<EditProfileData>,
    val message: String,
    val status: Int
)