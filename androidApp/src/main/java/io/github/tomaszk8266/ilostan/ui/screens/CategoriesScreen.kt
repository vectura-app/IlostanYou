package io.github.tomaszk8266.ilostan.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.tomaszk8266.ilostan.api.extractors.getAndExtractCategories
import io.github.tomaszk8266.ilostan.api.extractors.getAndExtractVehiclesTypes
import io.github.tomaszk8266.ilostan.api.types.Category
import io.github.tomaszk8266.ilostan.api.types.VehiclesTypes
import kotlinx.coroutines.launch

@Composable
fun CategoriesScreen() {
    val scope = rememberCoroutineScope()
    val categories = remember { mutableStateMapOf<Category, List<VehiclesTypes>>() }

    Button(
        onClick = {
            scope.launch { categories.putAll(getAndExtractCategories()
                .associateWith { getAndExtractVehiclesTypes(it.id) })
            }
        },
        modifier = Modifier.fillMaxWidth()
            .padding(10.dp)
    ) {
        Text("Get categories")
    }

    categories.forEach { (category, types) ->
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = category.name,
                fontSize = 24.sp
            )

            Text(
                text = "Vehicle types in category:",
                fontSize = 20.sp
            )

            types.forEach {
                Text(" ${it.name} - ${it.series.joinToString { series -> series.name }}")
            }
        }
    }
}