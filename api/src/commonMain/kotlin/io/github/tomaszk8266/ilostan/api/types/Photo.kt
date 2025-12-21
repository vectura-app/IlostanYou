package io.github.tomaszk8266.ilostan.api.types

import kotlinx.datetime.LocalDate

data class Photo(
    val id: Int,
    val date: LocalDate,
    val description: String
)
