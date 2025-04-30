package com.example.pokemonpokethelper

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import coil.compose.rememberAsyncImagePainter
import com.example.pokemonpokethelper.data.FirestoreRepo
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.HorizontalDivider


import com.example.pokemonpokethelper.ui.theme.PokemonPoketHelperTheme
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.launch
import java.util.UUID

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
fun AppNavigator() {
    val nav = rememberNavController()
    NavHost(navController = nav, startDestination = "collections") {
        composable("collections") {
            CollectionsScreen(
                onCreateCollection = { nav.navigate("addCollection") },
                onOpenCollection   = { id -> nav.navigate("cards/$id") }
            )
        }

        composable("addCollection") {
            AddCollectionScreen(onDone = { nav.popBackStack() })
        }

        composable(
            "cards/{collectionId}",
            arguments = listOf(navArgument("collectionId") { type = NavType.StringType })
        ) { backStack ->
            val cid = backStack.arguments!!.getString("collectionId")!!
            CardsScreen(
                collectionId = cid,
                onAddCard    = { nav.navigate("addCard/$cid") },
                onBack       = { nav.popBackStack() }
            )
        }

        composable(
            "addCard/{collectionId}",
            arguments = listOf(navArgument("collectionId") { type = NavType.StringType })
        ) { backStack ->
            val cid = backStack.arguments!!.getString("collectionId")!!
            AddCardScreen(collectionId = cid) {
                nav.popBackStack()
            }
        }
    }
}


@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onSignUpClick: () -> Unit
) {
    val auth = Firebase.auth
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    Column(Modifier.fillMaxSize().padding(32.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Log In", style = MaterialTheme.typography.headlineSmall)

        OutlinedTextField(value = email, onValueChange = { email = it },
            label = { Text("Email") }, singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
        OutlinedTextField(value = password, onValueChange = { password = it },
            label = { Text("Password") }, singleLine = true,
            visualTransformation = PasswordVisualTransformation()
        )

        errorMsg?.let { Text(it, color = MaterialTheme.colorScheme.error) }

        Button(onClick = {
            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener { onLoginSuccess() }
                .addOnFailureListener { errorMsg = it.localizedMessage }
        }, Modifier.fillMaxWidth()) {
            Text("Log In")
        }

        Spacer(Modifier.height(8.dp))
        TextButton(onClick = onSignUpClick, Modifier.align(Alignment.End)) {
            Text("No account? Sign Up")
        }
    }
}

@Composable
fun SignUpScreen(onSignUpSuccess: () -> Unit) {
    val auth = Firebase.auth
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    Column(Modifier.fillMaxSize().padding(32.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Sign Up", style = MaterialTheme.typography.headlineSmall)

        OutlinedTextField(value = email, onValueChange = { email = it },
            label = { Text("Email") }, singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
        OutlinedTextField(value = password, onValueChange = { password = it },
            label = { Text("Password") }, singleLine = true,
            visualTransformation = PasswordVisualTransformation()
        )

        errorMsg?.let { Text(it, color = MaterialTheme.colorScheme.error) }

        Button(onClick = {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener { onSignUpSuccess() }
                .addOnFailureListener { errorMsg = it.localizedMessage }
        }, Modifier.fillMaxWidth()) {
            Text("Create Account")
        }
    }
}

@Composable
fun MediaUploadScreen() {
    var mediaUri by remember { mutableStateOf<Uri?>(null) }
    var isUploading by remember { mutableStateOf(false) }
    var progress by remember { mutableFloatStateOf(0f) }

    // grab Context once here
    val context = LocalContext.current
    val storage = Firebase.storage

    val pickMedia = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        mediaUri = uri
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Button(onClick = {
            // use PickVisualMediaRequest with the enum from ActivityResultContracts
            pickMedia.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo)
            )
        }) {
            Text("Pick Image or Video")
        }

        mediaUri?.let { uri ->
            val isVideo = uri.toString().endsWith(".mp4", ignoreCase = true)
            if (isVideo) {
                Text("Video selected: $uri", style = MaterialTheme.typography.bodyMedium)
            } else {
                Image(
                    painter = rememberAsyncImagePainter(uri),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(Modifier.height(8.dp))

            if (isUploading) {
                LinearProgressIndicator(
                    progress = { progress },
                )
                Text(text = "${(progress * 100).toInt()}% uploaded")
            } else {
                Button(onClick = {
                    // start upload
                    isUploading = true
                    val filename = UUID.randomUUID().toString()
                    val ext = if (isVideo) "mp4" else "jpg"
                    val ref = storage.reference.child("uploads/$filename.$ext")

                    ref.putFile(uri)
                        .addOnProgressListener { snap ->
                            progress = snap.bytesTransferred.toFloat() / snap.totalByteCount
                        }
                        .addOnSuccessListener {
                            isUploading = false
                            // use 'context' here!
                            Toast
                                .makeText(context, "Upload complete!", Toast.LENGTH_LONG)
                                .show()
                            // if you need the download URL asynchronously:
                            ref.downloadUrl.addOnSuccessListener { downloadUri ->
                                Toast
                                    .makeText(context, "URL: $downloadUri", Toast.LENGTH_LONG)
                                    .show()
                            }
                        }
                        .addOnFailureListener { e ->
                            isUploading = false
                            Toast
                                .makeText(context, "Upload failed: ${e.localizedMessage}", Toast.LENGTH_LONG)
                                .show()
                        }
                }) {
                    Text("Upload to Firebase")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun CollectionsScreen(
    onCreateCollection: () -> Unit,
    onOpenCollection: (String) -> Unit
) {
    // 1) Firestore flow & state
    val collectionsFlow = remember { FirestoreRepo.getCollections() }
    val snapshots by collectionsFlow.collectAsState(initial = null)

    // 2) Search query state
    var searchQuery by remember { mutableStateOf("") }

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
        Column(Modifier
            .padding(padding)
            .fillMaxSize()
            .padding(16.dp)       // inner padding
        ) {
            // 3) Search bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search collections") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            // 4) Filter the documents
            val docs = snapshots?.documents ?: emptyList()
            val filtered = if (searchQuery.isBlank()) docs
            else docs.filter { doc ->
                val name = doc.getString("name") ?: ""
                name.contains(searchQuery, ignoreCase = true)
            }

            // 5) Display
            LazyColumn {
                items(filtered) { doc ->
                    val name = doc.getString("name") ?: "Untitled"
                    ListItem(
                        modifier        = Modifier
                            .clickable { onOpenCollection(doc.id) }
                            .fillMaxWidth(),
                        headlineContent = { Text(name) }
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}



@Composable
fun AddCollectionScreen(onDone: () -> Unit) {
    var name    by remember { mutableStateOf("") }
    var saving  by remember { mutableStateOf(false) }
    val scope   = rememberCoroutineScope()

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("New Collection", style = MaterialTheme.typography.headlineSmall)

        OutlinedTextField(
            value       = name,
            onValueChange = { name = it },
            label       = { Text("Collection Name") },
            modifier    = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                saving = true
                scope.launch {
                    try {
                        FirestoreRepo.addCollection(name)   // suspend here
                        Log.d("AddCollection", "success")
                    } catch(e: Exception) {
                        Log.e("AddCollection", "failed", e)
                    }
                    saving = false
                    onDone()   // <— this tells NavController to go back
                }
            },
            enabled  = name.isNotBlank() && !saving,
            modifier = Modifier.align(Alignment.End)
        ) {
            Text(if (saving) "Saving…" else "Create")
        }
    }
}


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

    // 2) Search-state
    var searchQuery by remember { mutableStateOf("") }

    // 3) Delete-state
    var deletingId by remember { mutableStateOf<String?>(null) }
    var isDeleting by remember { mutableStateOf(false) }
    val scope      = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cards") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
                    Divider()
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



@Composable
fun AddCardScreen(
    collectionId: String,
    onDone: () -> Unit
) {
    var name         by remember { mutableStateOf("") }
    var expansion    by remember { mutableStateOf("") }
    var expansionId  by remember { mutableStateOf("") }  // temporarily as String
    var saving       by remember { mutableStateOf(false) }
    val scope        = rememberCoroutineScope()

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("New Card", style = MaterialTheme.typography.headlineSmall)

        OutlinedTextField(
            value       = name,
            onValueChange = { name = it },
            label       = { Text("Name") },
            modifier    = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value       = expansion,
            onValueChange = { expansion = it },
            label       = { Text("Expansion") },
            modifier    = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value       = expansionId,
            onValueChange = { expansionId = it.filter { it.isDigit() } },
            label       = { Text("Expansion ID") },
            modifier    = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Spacer(Modifier.height(8.dp))

        Button(
            onClick = {
                val expId = expansionId.toIntOrNull() ?: 0
                saving = true
                scope.launch {
                    FirestoreRepo.addCard(collectionId, name, expansion, expId)
                    saving = false
                    onDone()
                }
            },
            enabled  = name.isNotBlank()
                    && expansion.isNotBlank()
                    && expansionId.isNotBlank()
                    && !saving,
            modifier = Modifier.align(Alignment.End)
        ) {
            Text(if (saving) "Saving…" else "Add Card")
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

