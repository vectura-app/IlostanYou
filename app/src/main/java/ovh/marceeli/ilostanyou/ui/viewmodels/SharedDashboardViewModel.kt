package ovh.marceeli.ilostanyou.ui.viewmodels

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.tomaszk8266.ilostan.api.extractors.getAndExtractCategories
import io.github.tomaszk8266.ilostan.api.extractors.getAndExtractVehiclesTypes
import io.github.tomaszk8266.ilostan.api.types.Category
import io.github.tomaszk8266.ilostan.api.types.VehiclesTypes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class SharedDashboardViewModel : ViewModel() {
    val categories = mutableStateMapOf<Category, List<VehiclesTypes>>()

    val types = mutableStateListOf<VehiclesTypes>()

    private val _isLoading = MutableStateFlow(false)

    private var hasFetchedCategories = false

    fun fetchCategoriesIfNeeded() {
        if (hasFetchedCategories || _isLoading.value) return

        viewModelScope.launch {
            _isLoading.value = true
            try {
                categories.clear()
                categories.putAll(
                    getAndExtractCategories()
                        .associateWith { getAndExtractVehiclesTypes(it.id) }
                )
                hasFetchedCategories = true
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setSelectedTypes(newTypes: List<VehiclesTypes>) {
        types.clear()
        types.addAll(newTypes)
    }

}