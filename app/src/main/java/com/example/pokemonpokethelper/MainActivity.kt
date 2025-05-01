package com.example.pokemonpokethelper

import AppNavigator
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.*
import com.example.pokemonpokethelper.ui.theme.PokemonPoketHelperTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PokemonPoketHelperTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigator()
                }
            }
        }
    }
}





@Composable
fun HomeScreen() {
    // Your existing “Hello, World!” or PokémonGreeting screen goes here
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Hello, World!", style = MaterialTheme.typography.headlineMedium)
    }
}

