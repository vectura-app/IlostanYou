package io.github.tomaszk8266.ilostan.api.types

import kotlinx.datetime.LocalDate

data class Vehicle(
    val id: Int,
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
        val transferDate: LocalDate?,
        val owner: String,
        val carrier: String?
    )

    data class RepairEntry(
        val finishDate: LocalDate,
        val zntk: String,
        val colors: String,
        val type: String,
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
        Reintroduced,
        Scrapped,
        Wrecked,
        Exhibited,
        Other;

        companion object {
            fun fromString(string: String) = when(string) {
                "Czynny" -> Operated
                "Nieczynny" -> Unoperated
                "OR" -> AwaitingRevisionRepair
                "OG" -> AwaitingGeneralRepair
                "NR" -> RevisionRepair
                "NG" -> GeneralRepair
                "Zapas" -> Spare
                "Wrak" -> Wrecked
                "Oczekuje skreslenia", "Oczekuje kasacji" -> AwaitingDeletion
                "Ponowne wpisanie na stan" -> Reintroduced
                "Zlomowany" -> Scrapped
                "Eksponat" -> Exhibited
                else -> Other
            }
        }
    }
}
