package com.rakul.skriningparu.data.model.response

data class ScreeningResponse(
    val title: String = "",
    val image: String = "",
    val answer: List<String> = listOf()
)