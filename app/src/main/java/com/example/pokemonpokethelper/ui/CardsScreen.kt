package com.example.pokemonpokethelper.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import com.example.pokemonpokethelper.R
import com.example.pokemonpokethelper.data.RemoteRepo
import com.example.pokemonpokethelper.model.Card

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardsScreen(
    userId: String,
    collectionName: String,
    onAddCard: () -> Unit,
    onBack: () -> Unit
) {
    val cards = remember { mutableStateOf(emptyList<Card>()) }
    var isLoading by remember { mutableStateOf(true) }
    var deleting by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(collectionName) {
        isLoading = true
        try {
            cards.value = RemoteRepo.searchCards(userId, collectionName)
        } catch (e: Exception) {
            cards.value = emptyList()
        }
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(collectionName) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddCard) {
                Icon(Icons.Default.Add, contentDescription = "Add Card")
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            } else {
                LazyColumn {
                    items(cards.value) { card ->
                        ListItem(
                            headlineContent = { Text(card.name ?: "Unnamed Card") },
                            supportingContent = { Text("Expansion: ${card.expansion}") },
                            trailingContent = {
                                IconButton(onClick = { deleting = card.id }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                                }
                            }
                        )
                        HorizontalDivider()
                    }
                }
            }

            if (deleting != null) {
                AlertDialog(
                    onDismissRequest = { deleting = null },
                    title = { Text(stringResource(R.string.delete_dialog_confirm)) },
                    confirmButton = {
                        TextButton(onClick = {
                            scope.launch {
                                cards.value = RemoteRepo.searchCards(userId, collectionName)
                                deleting = null
                            }
                        }) {
                            Text(stringResource(R.string.delete_dialog_title))
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { deleting = null }) {
                            Text(stringResource(R.string.cancel))
                        }
                    }
                )
            }
        }
    }
}
