package com.example.pokemonpokethelper

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.content.edit
import com.example.pokemonpokethelper.ui.AppNavigator
import com.example.pokemonpokethelper.ui.theme.PokemonPoketHelperTheme
import java.util.UUID

fun getOrCreateUserId(context: Context): String {
    val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    val existing = prefs.getString("currentUserId", null)
    return existing ?: run {
        val newId = UUID.randomUUID().toString()
        prefs.edit {
            putString("currentUserId", newId)
        }
        newId
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val currentUserId = getOrCreateUserId(this)

        setContent {
            PokemonPoketHelperTheme {
                AppNavigator(currentUserId = currentUserId)
            }
        }
    }
}
