package ovh.marceeli.ilostanyou.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.tomaszk8266.ilostan.api.extractors.getAndExtractCategories
import io.github.tomaszk8266.ilostan.api.extractors.getAndExtractRecentPhots
import io.github.tomaszk8266.ilostan.api.extractors.getAndExtractSeries
import io.github.tomaszk8266.ilostan.api.extractors.getAndExtractVehiclesTypes
import io.github.tomaszk8266.ilostan.api.types.Category
import io.github.tomaszk8266.ilostan.api.types.toPhotoUrl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class DashboardState(
    val query: String = "",
    val categories: List<Category> = emptyList(),
    val selectedCategoryIds: Set<Int> = emptySet(),
    val suggestions: List<Suggestion> = emptyList(),
    val searchLoading: Boolean = false,
    val feedItems: List<FeedItem> = emptyList(),
    val feedLoading: Boolean = false
) {
    data class Suggestion(
        val id: Int,
        val type: Type,
        val name: String,
        val rawName: String?,
        val description: String?,
        val photoUrl: String?
    ) {
        enum class Type {
            Vehicle, Series
        }
    }

    data class FeedItem(
        val title: String,
        val subtitle: String?,
        val photoUrl: String?
    )
}

data class VehicleSeries(
    val id: Int,
    val seriesName: String,
    val manufacturerTypeName: String?,
    val displayName: String?,
    val photoUrl: String?,
)

class DashboardViewModel : ViewModel() {
    private val _state = MutableStateFlow(DashboardState())
    val state = _state.asStateFlow()

    private var seriesList = mutableListOf<VehicleSeries>()

    init {
        viewModelScope.launch {
            _state.update { it.copy(searchLoading = true) }
            fetchCategories()
            loadVehicleTypes(_state.value.selectedCategoryIds)
            _state.update { it.copy(searchLoading = false) }
        }
        viewModelScope.launch {
            _state.update { it.copy(feedLoading = true) }
            _state.update {
                it.copy(
                    feedLoading = false,
                    feedItems = getAndExtractRecentPhots().map { photo ->
                        DashboardState.FeedItem(
                            title = "New photo added",
                            subtitle = photo.description,
                            photoUrl = photo.id.toPhotoUrl()
                        )
                    }
                )
            }
        }
    }

    suspend fun fetchCategories() {
        _state.update { s ->
            val categories = getAndExtractCategories()
            s.copy(categories = categories, selectedCategoryIds = categories.map { it.id }.toSet())
        }
    }

    private suspend fun loadVehicleTypes(selectedIds: Set<Int>) {
        val allTypes = selectedIds.flatMap { id ->
            getAndExtractVehiclesTypes(id)
        }
        seriesList.clear()
        seriesList.addAll(allTypes.flatMap { type ->
            type.series.map { series ->
                VehicleSeries(
                    id = series.id,
                    seriesName = series.name,
                    manufacturerTypeName = type.manufacturerTypeName,
                    displayName = type.name,
                    photoUrl = series.photoId.toPhotoUrl()
                )
            }
        })
        updateSuggestions()
    }

    fun toggleCategory(categoryId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _state.update { it.copy(searchLoading = true) }
            _state.update { current ->
                val newSelected = if (current.selectedCategoryIds.contains(categoryId))
                    current.selectedCategoryIds - categoryId else current.selectedCategoryIds + categoryId

                loadVehicleTypes(newSelected)
                current.copy(selectedCategoryIds = newSelected)
            }
            _state.update { it.copy(searchLoading = false) }
        }
    }

    suspend fun onQueryChange(newQuery: String) {
        _state.update { it.copy(query = newQuery, searchLoading = true) }
        updateSuggestions()
        _state.update { it.copy(searchLoading = false) }
    }

    private suspend fun updateSuggestions() {
        val query = _state.value.query.lowercase().replace(" ", "-")

        val suggestions = when {
            query.isBlank() -> emptyList()
            "-" !in query -> seriesList.filter {
                query in it.seriesName.lowercase()
                        || query in it.manufacturerTypeName.orEmpty().lowercase()
                        || query in it.displayName.orEmpty().lowercase()
            }.map {
                DashboardState.Suggestion(
                    id = it.id,
                    type = DashboardState.Suggestion.Type.Series,
                    name = it.displayName?.let { name -> "$name (${it.seriesName})" } ?: it.seriesName,
                    rawName = it.seriesName,
                    description = it.manufacturerTypeName,
                    photoUrl = it.photoUrl
                )
            }
            else -> {
                val seriesName = query.substringBefore("-").uppercase()
                val series = seriesList.firstOrNull { it.seriesName.uppercase().startsWith(seriesName) }
                val seriesVehicles = withContext(Dispatchers.IO) {
                    series?.let { getAndExtractSeries(it.id) }?.vehicles.orEmpty()
                }

                seriesVehicles.map {
                    DashboardState.Suggestion(
                        id = it.id,
                        type = DashboardState.Suggestion.Type.Vehicle,
                        name = it.name,
                        rawName = null,
                        description = it.ownershipHistory[0].carrier,
                        photoUrl = null
                    )
                }.filter {
                    val vehicleUnitNumber = it.name.uppercase()
                        .replace(" ", "-")
                        .substringAfter("-")

                    query.substringAfter("-") in vehicleUnitNumber
                }
            }
        }

        _state.update { it.copy(suggestions = suggestions) }
    }
}