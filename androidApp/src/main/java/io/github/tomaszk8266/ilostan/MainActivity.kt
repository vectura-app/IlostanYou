package io.github.tomaszk8266.ilostan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import io.github.tomaszk8266.ilostan.ui.screens.MainScreen
import io.github.tomaszk8266.ilostan.ui.theme.IlostanTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            IlostanTheme {
                MainScreen()
            }
        }
    }
}