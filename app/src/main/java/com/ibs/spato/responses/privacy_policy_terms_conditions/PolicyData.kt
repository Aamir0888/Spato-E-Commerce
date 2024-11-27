package com.ibs.spato.responses.privacy_policy_terms_conditions

data class PolicyData(
    val company_policy: CompanyPolicy,
    val privacy_policy: PrivacyPolicy,
    val terms: TermsAndConditions
)