package io.github.tomaszk8266.ilostan.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Train
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

enum class CurrentScreen {
    Categories,
    Vehicle
}

@Composable
fun MainScreen() {
    var currentScreen by remember { mutableStateOf(CurrentScreen.Categories) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = currentScreen == CurrentScreen.Categories,
                    onClick = { currentScreen = CurrentScreen.Categories },
                    icon = { Icon(Icons.Default.Category, null) }
                )
                NavigationBarItem(
                    selected = currentScreen == CurrentScreen.Vehicle,
                    onClick = { currentScreen = CurrentScreen.Vehicle },
                    icon = { Icon(Icons.Default.Train, null) }
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
                .padding(24.dp)
        ) {
            when(currentScreen) {
                CurrentScreen.Vehicle -> VehicleScreen()
                CurrentScreen.Categories -> CategoriesScreen()
            }
        }
    }
}