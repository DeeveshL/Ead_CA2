package com.example.pokemonpokethelper.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.pokemonpokethelper.R
import com.example.pokemonpokethelper.data.RemoteRepo
import kotlinx.coroutines.launch

@Composable
fun AddCollectionScreen(
    userId: String,
    onDone: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var saving by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(stringResource(R.string.new_collection), style = MaterialTheme.typography.headlineSmall)

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text(stringResource(R.string.collection_name)) },
            modifier = Modifier.fillMaxWidth()
        )

        errorMessage?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        Spacer(Modifier.height(8.dp))

        Button(
            onClick = {
                saving = true
                errorMessage = null

                scope.launch {
                    try {
                        RemoteRepo.createCollection(userId = userId, name = name.trim())
                        onDone()
                    } catch (e: Exception) {
                        errorMessage = e.localizedMessage ?: "An error occurred"
                        saving = false
                    }
                }
            },
            enabled = name.isNotBlank() && !saving,
            modifier = Modifier.align(Alignment.End)
        ) {
            Text(if (saving) "Saving…" else "Create")
        }
    }
}
