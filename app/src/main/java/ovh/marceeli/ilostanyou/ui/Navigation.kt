package ovh.marceeli.ilostanyou.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.koin.compose.koinInject
import ovh.marceeli.ilostanyou.R
import ovh.marceeli.ilostanyou.ui.routes.Route
import ovh.marceeli.ilostanyou.ui.screens.AboutScreen
import ovh.marceeli.ilostanyou.ui.screens.DashboardScreen
import ovh.marceeli.ilostanyou.ui.screens.TypesScreen
import ovh.marceeli.ilostanyou.ui.viewmodels.SharedDashboardViewModel

data class BarItem(
    val label: String,
    val iconSelected: ImageVector,
    val icon: ImageVector,
    val route: Route
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Navigation(
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()
    val sharedViewModel: SharedDashboardViewModel = koinInject()
    var currentScreen by remember { mutableStateOf<Route?>(null) }

    val noBottomNavScreens = listOf(Route.Types)

    val screensWithBackButton = listOf(Route.Types)

    val navigationItems = listOf(
        BarItem(
            stringResource(R.string.dashboard_title),
            Icons.Filled.Dashboard,
            Icons.Outlined.Dashboard,
            Route.Dashboard
        ),
        BarItem(
            stringResource(R.string.about_title),
            Icons.Filled.Info,
            Icons.Outlined.Info,
            Route.About
        ),
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        when (currentScreen) {
                            null -> stringResource(R.string.app_name)
                            Route.About -> stringResource(R.string.about_title)
                            Route.Dashboard -> stringResource(R.string.dashboard_title)
                            Route.Types -> "Vehicle Types"
                        }
                    )
                },
                navigationIcon = {
                    if (screensWithBackButton.contains(currentScreen)) {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            if (noBottomNavScreens.none { it == currentScreen }) {
                NavigationBar {
                    navigationItems.forEach { item ->
                        val isSelected = currentScreen == item.route

                        NavigationBarItem(
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) item.iconSelected else item.icon,
                                    contentDescription = item.label
                                )
                            },
                            label = { Text(item.label) },
                            selected = isSelected,
                            onClick = {
                                if (!isSelected) {
                                    navController.navigate(item.route) { launchSingleTop = true }
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            modifier = Modifier.padding(innerPadding),
            startDestination = Route.Dashboard
        ) {
            composable<Route.Dashboard> {
                currentScreen = Route.Dashboard
                DashboardScreen(navController = navController, viewModel = sharedViewModel)
            }

            composable<Route.About> {
                currentScreen = Route.About
                AboutScreen()
            }

            composable<Route.Types> {
                currentScreen = Route.Types
                TypesScreen(viewModel = sharedViewModel)
            }
        }
    }
}