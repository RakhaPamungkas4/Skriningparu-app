package com.rakul.skriningparu.data.model.request

data class IdentityRequest(
    val fullName: String = "",
    val gender: String = "",
    val phoneNumber: String = "",
    val rangeAge: String = "",
    val screeningType: String = "",
    val subjectScreening: String = ""
)