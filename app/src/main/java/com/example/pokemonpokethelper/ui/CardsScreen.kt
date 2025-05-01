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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.pokemonpokethelper.data.FirestoreRepo
import com.example.pokemonpokethelper.data.RemoteRepo
import com.example.pokemonpokethelper.network.CardDto
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable


fun CardsScreen(
    collectionId: String,
    onAddCard: () -> Unit,
    onBack: () -> Unit
) {
    // 1) Firestore flow & state
    val cardsFlow    = remember { FirestoreRepo.getCards(collectionId) }
    val snapshots    by cardsFlow.collectAsState(initial = null)
    val docs         = snapshots?.documents ?: emptyList()

    var cards by remember { mutableStateOf<List<CardDto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // 2) Search-state
    var searchQuery by remember { mutableStateOf("") }

    // 3) Delete-state
    var deletingId by remember { mutableStateOf<String?>(null) }
    var isDeleting by remember { mutableStateOf(false) }
    val scope      = rememberCoroutineScope()
    var jwtToken = remember { "RemoteRepo.fetchCards(jwtToken!!, collectionId)" }


    LaunchedEffect(Unit) {
        Firebase.auth.currentUser
            ?.getIdToken(false)            // false = don’t force-refresh if you already have one
            ?.await()                      // from kotlinx-coroutines-play-services
            ?.let { result ->
                jwtToken = result.token.toString()      // this is your JWT
            }
    }
    LaunchedEffect(jwtToken, collectionId) {
        val token = jwtToken ?: return@LaunchedEffect  // do nothing until non-null
        isLoading = true
        cards = try {
            RemoteRepo.fetchCards(token, collectionId)
        } catch (e: Exception) {
            emptyList()
        }
        isLoading = false
    }

    if (jwtToken == null) {
        // you could show a spinner here…
        CircularProgressIndicator()
        return
    }

    // ④ Now jwtToken!! is safe to use
    LaunchedEffect(jwtToken, collectionId) {
        cards = RemoteRepo.fetchCards(jwtToken!!, collectionId)
        isLoading = false
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cards") },
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
        Column(
            Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // ── Search Bar ───────────────────────────────
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search cards") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            // ── Filtered List ────────────────────────────
            val filtered = if (searchQuery.isBlank()) docs
            else docs.filter { doc ->
                val name      = doc.getString("name") ?: ""
                val expansion = doc.getString("expansion") ?: ""
                name.contains(searchQuery, ignoreCase = true) ||
                        expansion.contains(searchQuery, ignoreCase = true)
            }

            LazyColumn {
                items(filtered) { doc ->
                    val id          = doc.id
                    val name        = doc.getString("name")      ?: ""
                    val expansion   = doc.getString("expansion") ?: ""
                    val expansionId = doc.getLong("expansionId")?.toInt() ?: 0

                    ListItem(
                        modifier          = Modifier.fillMaxWidth(),
                        headlineContent   = { Text(name) },
                        supportingContent = {
                            Column {
                                Text("Expansion: $expansion")
                                Text("ID: $expansionId")
                            }
                        },
                        trailingContent   = {
                            IconButton(onClick = { deletingId = id }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete")
                            }
                        }
                    )
                    HorizontalDivider()
                }
            }
        }
        if (isLoading) {
            CircularProgressIndicator()
        } else {
            LazyColumn {
                items(cards) { card ->
                    Text("${card.name} (${card.expansion})")
                    IconButton(onClick = {
                        scope.launch {
                            // ① pass jwtToken, ② collectionId, ③ card.id
                            RemoteRepo.deleteCard(jwtToken!!, collectionId, card.id.toString())
                            // then reload with the same two params
                            cards = RemoteRepo.fetchCards(jwtToken!!, collectionId)
                        }
                    }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                    }

                }
            }
        }
        // ── Confirmation Dialog ───────────────────────
        if (deletingId != null) {
            AlertDialog(
                onDismissRequest = { if (!isDeleting) deletingId = null },
                title   = { Text("Delete this card?") },
                text    = { Text("This action cannot be undone.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            scope.launch {
                                isDeleting = true
                                try {
                                    FirestoreRepo.deleteCard(collectionId, deletingId!!)
                                } catch (e: Exception) {
                                    // TODO: Show error Snackbar
                                }
                                isDeleting = false
                                deletingId = null
                            }
                        }
                    ) {
                        Text(if (isDeleting) "Deleting…" else "Delete")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { if (!isDeleting) deletingId = null }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}