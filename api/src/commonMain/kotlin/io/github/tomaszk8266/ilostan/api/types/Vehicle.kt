package io.github.tomaszk8266.ilostan.api.types

import kotlinx.datetime.LocalDate

data class Vehicle(
    val name: String,
    val factoryNumber: String?,
    val manufacturingYear: Int,
    val statusHistory: List<StatusEntry>,
    val eventHistory: List<EventEntry>,
    val ownershipHistory: List<OwnershipEntry>,
    val repairHistory: List<RepairEntry>,
    val photos: List<Photo>
) {
    data class StatusEntry(
        val status: Status,
        val comment: String?,
        val date: LocalDate
    )

    data class EventEntry(
        val event: String,
        val description: String,
        val date: LocalDate
    )

    data class OwnershipEntry(
        val transferDate: LocalDate,
        val owner: String,
        val carrier: String?
    )

    data class RepairEntry(
        val finishDate: LocalDate,
        val zntk: String,
        val colors: String,
        val type: String,
    )

    data class Photo(
        val date: LocalDate,
        val id: Int,
        val description: String,
    )

    enum class Status {
        Operated,
        Unoperated,
        AwaitingRevisionRepair,
        RevisionRepair,
        AwaitingGeneralRepair,
        GeneralRepair,
        Spare,

        AwaitingDeletion,
        Scrapped,
        Wrecked,
        Exhibited,
        Other
    }
}
