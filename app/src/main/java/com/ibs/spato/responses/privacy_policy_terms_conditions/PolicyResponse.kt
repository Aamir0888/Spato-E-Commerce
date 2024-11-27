package com.ibs.spato.responses.privacy_policy_terms_conditions

data class PolicyResponse(
    val data: PolicyData,
    val message: String,
    val status: Int
)