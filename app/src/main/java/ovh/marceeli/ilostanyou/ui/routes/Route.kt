package ovh.marceeli.ilostanyou.ui.routes

import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable
    data object Dashboard : Route

    @Serializable
    data class Vehicle(
        val id: Int
    ) : Route
}
