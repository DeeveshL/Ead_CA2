package com.example.pokemonpokethelper.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.pokemonpokethelper.R
import com.example.pokemonpokethelper.data.AzureRepo
import com.example.pokemonpokethelper.model.ModelCollection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionsScreen(
    navController: NavHostController,
    userId: String,
    onOpenCollection: (String) -> Unit
) {
    var collections by remember { mutableStateOf<List<ModelCollection>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    val savedStateHandle = navController
        .currentBackStackEntry!!
        .savedStateHandle

    val shouldRefresh by savedStateHandle
        .getStateFlow("refreshCollections", false)
        .collectAsState()

    LaunchedEffect(shouldRefresh) {
        isLoading = true
        collections = try {
            AzureRepo.getCollections(userId)
        } catch (e: Exception) {
            emptyList()
        }
        isLoading = false

        if (shouldRefresh) {
            savedStateHandle.set("refreshCollections", false)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(stringResource(R.string.your_collections)) })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("addCollection") }) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.new_collection))
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            } else {
                LazyColumn {
                    items(collections) { col ->
                        ListItem(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onOpenCollection(col.name?: "Unnamed") },
                            headlineContent = { Text(col.name?: "Unnamed") }
                        )
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}
