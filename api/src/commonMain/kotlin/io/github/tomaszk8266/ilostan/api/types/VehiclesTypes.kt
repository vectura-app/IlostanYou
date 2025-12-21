package io.github.tomaszk8266.ilostan.api.types

data class VehiclesTypes(
    val name: String,
    val description: String?,
    val series: List<SeriesEntry>
) {
    data class SeriesEntry(
        val id: Int,
        val name: String,
        val photos: List<Photo>
    )
}
