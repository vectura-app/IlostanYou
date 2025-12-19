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
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import io.github.tomaszk8266.ilostan.api.extractors.getAndExtractCategories
import io.github.tomaszk8266.ilostan.api.extractors.getAndExtractVehiclesTypes
import io.github.tomaszk8266.ilostan.api.types.Category
import io.github.tomaszk8266.ilostan.api.types.VehiclesTypes
import ovh.marceeli.ilostanyou.ui.common.theme.ExpressiveListItemShapes

@Composable
fun DashboardScreen() {
    val categories = remember { mutableStateMapOf<Category, List<VehiclesTypes>>() }

    LaunchedEffect(Unit) {
        categories.putAll(
            getAndExtractCategories()
                .associateWith { getAndExtractVehiclesTypes(it.id) }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CategoryList(categories)
    }
}

@Composable
fun CategoryList(categories: SnapshotStateMap<Category, List<VehiclesTypes>>) {
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

            itemsIndexed(categories.toList()) { index, (category, types) ->
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
                        ) {  },
                    shape = shape,
                    color = MaterialTheme.colorScheme.surfaceContainer,
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(all = 16.dp)
                    ) {
                        // Headline
                        Text(
                            text = category.name,
                            style = MaterialTheme.typography.titleMedium
                        )

                        // Description
                        Text(
                            text = category.description.takeUnless { it.isNullOrBlank() } ?: "No description",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        // Types info
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
