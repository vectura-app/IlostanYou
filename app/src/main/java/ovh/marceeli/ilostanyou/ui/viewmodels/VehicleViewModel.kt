package ovh.marceeli.ilostanyou.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.tomaszk8266.ilostan.api.extractors.getAndExtractVehicle
import io.github.tomaszk8266.ilostan.api.types.Vehicle
import kotlinx.coroutines.Dispatchers
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
        viewModelScope.launch(Dispatchers.IO) {
            _state.update { VehicleState(isLoading = true) }
            val vehicle = getAndExtractVehicle(vehicleId)
            _state.update { VehicleState(isLoading = false, vehicle = vehicle) }
        }
    }
}
