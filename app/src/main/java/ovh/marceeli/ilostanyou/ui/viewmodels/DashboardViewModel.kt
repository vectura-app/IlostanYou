package ovh.marceeli.ilostanyou.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.tomaszk8266.ilostan.api.extractors.getAndExtractCategories
import io.github.tomaszk8266.ilostan.api.extractors.getAndExtractSeries
import io.github.tomaszk8266.ilostan.api.extractors.getAndExtractVehiclesTypes
import io.github.tomaszk8266.ilostan.api.types.Category
import io.github.tomaszk8266.ilostan.api.types.VehiclesTypes
import io.github.tomaszk8266.ilostan.api.types.toPhotoUrl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DashboardState(
    val query: String = "",
    val categories: List<Category> = emptyList(),
    val selectedCategoryIds: Set<Int> = emptySet(),
    val suggestions: List<Suggestion> = emptyList(),
    val isLoading: Boolean = false,
    val feedItems: List<String> = emptyList(),
) {
    data class Suggestion(
        val id: Int,
        val type: Type,
        val name: String,
        val photoUrl: String?
    ) {
        enum class Type {
            Vehicle, Series
        }
    }
}

class DashboardViewModel : ViewModel() {
    private val _state = MutableStateFlow(DashboardState(
        feedItems = listOf(
            "Szpont",
            "Szczur"
        )
    ))
    val state = _state.asStateFlow()

    private var seriesList = mutableListOf<VehiclesTypes.SeriesEntry>()

    init {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            fetchCategories()
            loadVehicleTypes(_state.value.selectedCategoryIds)
            _state.update { it.copy(isLoading = false) }
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
        seriesList.addAll(allTypes.flatMap { it.series })
        updateSuggestions()
    }

    fun toggleCategory(categoryId: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            _state.update { current ->
                val newSelected = if (current.selectedCategoryIds.contains(categoryId)) {
                    current.selectedCategoryIds - categoryId
                } else {
                    current.selectedCategoryIds + categoryId
                }
                loadVehicleTypes(newSelected)
                current.copy(selectedCategoryIds = newSelected)
            }
            _state.update { it.copy(isLoading = false) }
        }
    }

    suspend fun onQueryChange(newQuery: String) {
        _state.update { it.copy(query = newQuery, isLoading = true) }
        updateSuggestions()
        _state.update { it.copy(isLoading = false) }
    }

    private suspend fun updateSuggestions() {
        val query = _state.value.query.uppercase().replace(" ", "-")

        val suggestions = when {
            query.isBlank() -> emptyList()
            "-" !in query -> seriesList.filter { query in it.name }.map {
                DashboardState.Suggestion(
                    id = it.id,
                    type = DashboardState.Suggestion.Type.Series,
                    name = it.name,
                    photoUrl = it.photoId.toPhotoUrl()
                )
            }
            else -> {
                val seriesName = query.substringBefore("-")
                val series = seriesList.firstOrNull { it.name.uppercase() == seriesName }
                val seriesVehicles = series?.let { getAndExtractSeries(it.id) }?.vehicles.orEmpty()

                seriesVehicles.map {
                    DashboardState.Suggestion(
                        id = it.id,
                        type = DashboardState.Suggestion.Type.Vehicle,
                        name = it.name,
                        photoUrl = null
                    )
                }
            }
        }

        _state.update { it.copy(suggestions = suggestions) }
    }
}