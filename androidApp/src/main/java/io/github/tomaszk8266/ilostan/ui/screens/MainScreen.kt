package io.github.tomaszk8266.ilostan.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.tomaszk8266.ilostan.api.extractors.getAndExtractVehicle
import io.github.tomaszk8266.ilostan.api.types.Vehicle
import kotlinx.coroutines.launch

@Composable
fun MainScreen() {
    val fieldState = rememberTextFieldState()
    var vehicle by remember { mutableStateOf<Vehicle?>(null) }
    val scope = rememberCoroutineScope()

    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            TextField(
                state = fieldState
            )

            Button(
                onClick = {
                    scope.launch {
                        vehicle = getAndExtractVehicle(fieldState.text.toString().toInt())
                    }
                }
            ) { Text("Get") }
        }

        vehicle?.let {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = it.name,
                    fontSize = 24.sp
                )

                Text("\nManufactured in ${it.manufacturingYear}")

                Text("\nOwners:")
                it.ownershipHistory.forEach { owner ->
                    Text("${owner.transferDate} - ${owner.owner}; operated by ${owner.carrier}")
                }

                Text("\nRepairs:")
                it.repairHistory.forEach { repair ->
                    Text("${repair.finishDate} - ${repair.type} in ${repair.zntk}")
                }

                Text("\nEvents:")
                it.eventHistory.forEach { event ->
                    Text("${event.event} has occured ${event.date} - ${event.description}")
                }
            }
        }
    }
}