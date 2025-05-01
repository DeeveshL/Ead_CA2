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
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
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
    navController: NavHostController,
    userId: String,
    onOpenCollection: (String) -> Unit
) {
    // 1) UI State
    var collections by remember { mutableStateOf<List<CollectionDto>>(emptyList()) }
    var isLoading   by remember { mutableStateOf(true) }
    var jwtToken    by remember { mutableStateOf<String?>(null) }

    // 2) Grab JWT once
    LaunchedEffect(Unit) {
        jwtToken = Firebase.auth.currentUser
            ?.getIdToken(false)
            ?.await()
            ?.token
    }

    // 3) Set up SavedStateHandle flow
    val savedStateHandle = navController
        .currentBackStackEntry!!
        .savedStateHandle

    // “refreshCollections” toggles when you pop back from AddCollectionScreen
    val shouldRefresh by savedStateHandle
        .getStateFlow("refreshCollections", false)
        .collectAsState()

    // 4) Reload whenever token arrives **or** shouldRefresh flips true
    LaunchedEffect(jwtToken, shouldRefresh) {
        val token = jwtToken ?: return@LaunchedEffect
        isLoading = true
        collections = try {
            AzureRepo.getCollections(token, userId)
        } catch (e: Exception) {
            emptyList()
        }
        isLoading = false

        // reset the flag so we don’t loop
        if (shouldRefresh) {
            savedStateHandle.set("refreshCollections", false)
        }
    }

    // 5) UI
    Scaffold(
        topBar = { TopAppBar(title = { Text("Your Collections") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("addCollection") }) {
                Icon(Icons.Default.Add, contentDescription = "New Collection")
            }
        }
    ) { padding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(padding)
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
                                .clickable { onOpenCollection(col.name) },
                            headlineContent = { Text(col.name) }
                        )
                        Divider()
                    }
                }
            }
        }
    }
}

