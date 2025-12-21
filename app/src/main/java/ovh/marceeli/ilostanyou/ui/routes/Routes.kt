package ovh.marceeli.ilostanyou.ui.routes

import kotlinx.serialization.Serializable

sealed class Route {
    @Serializable
    data object Dashboard : Route()

    @Serializable
    data object Types : Route()

    @Serializable
    data object About : Route()
}
