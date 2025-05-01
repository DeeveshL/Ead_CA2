import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.pokemonpokethelper.data.RemoteRepo
import com.example.pokemonpokethelper.network.CardDto
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import androidx.compose.ui.res.stringResource
import com.example.pokemonpokethelper.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardsScreen(
    userId: String,
    collectionName: String,
    onAddCard: () -> Unit,
    onBack: () -> Unit
) {
    // 1) UI state
    var cards     by remember { mutableStateOf<List<CardDto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var deleting  by remember { mutableStateOf<String?>(null) }
    val scope     = rememberCoroutineScope()

    // 2) Fetch Firebase JWT once
    var jwtToken by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(Unit) {
        jwtToken = Firebase.auth.currentUser
            ?.getIdToken(false)
            ?.await()
            ?.token
    }

    // 3) As soon as we have token + collectionName, load cards
    LaunchedEffect(jwtToken, collectionName) {
        val token = jwtToken ?: return@LaunchedEffect
        isLoading = true
        cards = try {
            RemoteRepo.fetchCards(token, userId, collectionName)
        } catch (e: Exception) {
            emptyList()
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
                    items(cards) { card ->
                        ListItem(
                            headlineContent   = { Text(card.name) },
                            supportingContent = { Text("Expansion: ${card.expansion}") },
                            trailingContent   = {
                                IconButton(
                                    onClick = { deleting = card.id.toString() }
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                                }
                            }
                        )
                        Divider()
                    }
                }
            }

            if (deleting != null && jwtToken != null) {
                AlertDialog(
                    onDismissRequest = { deleting = null },
                    title   = { Text(stringResource(R.string.delete_dialog_confirm)) },
                    confirmButton = {
                        TextButton(onClick = {
                            scope.launch {
                                RemoteRepo.deleteCard(jwtToken!!, userId, collectionName, deleting!!)
                                cards = RemoteRepo.fetchCards(jwtToken!!, userId, collectionName)
                                deleting = null
                            }
                        }) { Text(stringResource(R.string.delete_dialog_title)) }
                    },
                    dismissButton = {
                        TextButton(onClick = { deleting = null }) { Text(stringResource(R.string.cancel)) }
                    }
                )
            }
        }
    }
}
