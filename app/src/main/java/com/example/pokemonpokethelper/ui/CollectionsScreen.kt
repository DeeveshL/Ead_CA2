import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.pokemonpokethelper.data.AzureRepo
import com.example.pokemonpokethelper.data.FirestoreRepo
import com.example.pokemonpokethelper.network.CardDto
import com.example.pokemonpokethelper.network.CollectionDto
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionsScreen(
    onCreateCollection: () -> Unit,
    onOpenCollection: (String) -> Unit
) {
    // 1) State for your JWT
    var jwtToken by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(Unit) {
        jwtToken = Firebase.auth.currentUser
            ?.getIdToken(false)
            ?.await()    // kotlinx-coroutines-play-services
            ?.token
    }

    // 2) State for your Azure collections
    val cols by produceState(initialValue = emptyList<CollectionDto>(), jwtToken) {
        jwtToken?.let { value = AzureRepo.getCollections(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Your Collections") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onCreateCollection) {
                Icon(Icons.Default.Add, contentDescription = "New")
            }
        }
    ) { padding ->
        // Show a spinner until both token & data arrive
        if (jwtToken == null) {
            Box(
                Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                items(cols) { col ->
                    ListItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                // pass the numeric Azure ID as a string
                                onOpenCollection(col.id.toString())
                            },
                        headlineContent = { Text(col.name) }
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}
