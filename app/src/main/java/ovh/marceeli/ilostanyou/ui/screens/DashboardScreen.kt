package ovh.marceeli.ilostanyou.ui.screens

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import io.github.tomaszk8266.ilostan.api.types.Category
import io.github.tomaszk8266.ilostan.api.types.VehiclesTypes
import org.koin.compose.viewmodel.koinViewModel
import ovh.marceeli.ilostanyou.ui.common.theme.ExpressiveListItemShapes
import ovh.marceeli.ilostanyou.ui.routes.Route
import ovh.marceeli.ilostanyou.ui.viewmodels.SharedDashboardViewModel

@Composable
fun DashboardScreen(
    navController: NavController,
    viewModel: SharedDashboardViewModel
) {
    LaunchedEffect(Unit) {
        viewModel.fetchCategoriesIfNeeded()
    }

    val categories = viewModel.categories

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CategoryList(
            categories = categories,
            navController = navController,
            viewModel = viewModel
        )
    }
}

@Composable
fun CategoryList(
    categories: SnapshotStateMap<Category, List<VehiclesTypes>>,
    navController: NavController,
    viewModel: SharedDashboardViewModel = koinViewModel()
) {
    if (categories.isEmpty()) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Text(
                text = "Loading categories...",
                modifier = Modifier.padding(24.dp),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    } else {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val list = categories.toList()

            itemsIndexed(list) { index, (category, types) ->
                val isFirst = index == 0
                val isLast = index == list.lastIndex

                val shape = when {
                    isFirst -> ExpressiveListItemShapes.topListItemShape
                    isLast -> ExpressiveListItemShapes.bottomListItemShape
                    else -> ExpressiveListItemShapes.middleListItemShape
                }

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(shape = shape)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = LocalIndication.current
                        ) {
                            viewModel.setSelectedTypes(types)
                            navController.navigate(Route.Types)
                        },
                    shape = shape,
                    color = MaterialTheme.colorScheme.surfaceContainer,
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(all = 16.dp)
                    ) {
                        Text(
                            text = category.name,
                            style = MaterialTheme.typography.titleMedium
                        )

                        if (!category.description.isNullOrBlank()) {
                            category.description?.let {
                                Text(
                                    text = it,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }

                        if (types.isNotEmpty()) {
                            Text(
                                text = "${types.size} vehicle types",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}