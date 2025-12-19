package ovh.marceeli.ilostanyou.ui.routes

import kotlinx.serialization.Serializable

sealed class Route {
    @Serializable
    object Dashboard : Route()

    @Serializable
    object About : Route()
}
