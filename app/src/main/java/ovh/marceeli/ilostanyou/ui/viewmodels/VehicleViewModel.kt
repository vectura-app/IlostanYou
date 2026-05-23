package ovh.marceeli.ilostanyou.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.tomaszk8266.ilostan.api.extractors.getAndExtractVehicle
import io.github.tomaszk8266.ilostan.api.types.Vehicle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class VehicleState(
    val vehicle: Vehicle? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class VehicleViewModel : ViewModel() {
    private val _state = MutableStateFlow(VehicleState())
    val state = _state.asStateFlow()

    fun loadVehicle(vehicleId: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val vehicle = getAndExtractVehicle(vehicleId)
                _state.update { it.copy(vehicle = vehicle, isLoading = false) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}
