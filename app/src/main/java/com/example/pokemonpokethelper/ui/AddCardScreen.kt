package com.example.pokemonpokethelper.ui
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.pokemonpokethelper.data.RemoteRepo
import kotlinx.coroutines.launch


@Composable
fun AddCardScreen(
    userId: String,
    collectionName: String,
    onDone: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var expansion by remember { mutableStateOf("") }
    var expansionId by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Add New Card", style = MaterialTheme.typography.headlineSmall)

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Card Name") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = expansion,
            onValueChange = { expansion = it },
            label = { Text("Expansion") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = expansionId,
            onValueChange = { expansionId = it.filter { ch -> ch.isDigit() } },
            label = { Text("Expansion ID") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )

        errorMessage?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                isSaving = true
                errorMessage = null

                scope.launch {
                    try {
                        val createdCard = RemoteRepo.createCard(
                            name = name.trim(),
                            expansion = expansion.trim(),
                            expansionId = expansionId.toInt()
                        )

                        RemoteRepo.addCardToCollection(
                            userId = userId,
                            collectionName = collectionName,
                            cardId = createdCard.id
                        )

                        onDone()
                    } catch (e: Exception) {
                        errorMessage = e.localizedMessage ?: "An error occurred"
                        isSaving = false
                    }
                }
            },
            enabled = name.isNotBlank() && expansion.isNotBlank() && expansionId.isNotBlank() && !isSaving
        ) {
            Text(if (isSaving) "Saving..." else "Add Card")
        }
    }
}
