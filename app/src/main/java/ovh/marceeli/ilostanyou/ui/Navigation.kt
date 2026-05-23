package ovh.marceeli.ilostanyou.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import ovh.marceeli.ilostanyou.ui.routes.Route
import ovh.marceeli.ilostanyou.ui.screens.DashboardScreen
import ovh.marceeli.ilostanyou.ui.screens.VehicleScreen

@Composable
fun Navigation() {
    SharedTransitionLayout {
        val backStack = remember { mutableStateListOf<Route>(Route.Dashboard) }
        val entryProvider = entryProvider {
            entry<Route.Dashboard> {
                DashboardScreen(
                    onVehicleSelect = { id -> backStack += Route.Vehicle(id) }
                )
            }

            entry<Route.Vehicle> { route ->
                VehicleScreen(
                    vehicleId = route.id,
                    onBack = { if (backStack.size > 1) backStack.removeAt(backStack.lastIndex) }
                )
            }
        }

        BackHandler(enabled = backStack.size > 1) {
            backStack.removeAt(backStack.lastIndex)
        }

        NavDisplay(
            backStack = backStack,
            entryProvider = entryProvider
        )
    }
}