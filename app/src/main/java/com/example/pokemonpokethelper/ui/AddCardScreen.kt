import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.pokemonpokethelper.data.FirestoreRepo
import com.example.pokemonpokethelper.data.RemoteRepo
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import com.example.pokemonpokethelper.R

@Composable
fun AddCardScreen(
    userId: String,
    collectionName: String,
    onDone: () -> Unit
) {
    var name         by remember { mutableStateOf("") }
    var expansion    by remember { mutableStateOf("") }
    var cardId by remember { mutableStateOf("") }
    var expansionId  by remember { mutableStateOf("") }  // temporarily as String
    var saving       by remember { mutableStateOf(false) }
    val scope        = rememberCoroutineScope()
    var jwtToken by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        jwtToken = Firebase.auth.currentUser
            ?.getIdToken(false)
            ?.await()
            ?.token
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(stringResource(R.string.new_card))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text(stringResource(R.string.name)) },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = expansion,
            onValueChange = { expansion = it },
            label = { Text(stringResource(R.string.expansion)) },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = expansionId,
            onValueChange = { expansionId = it.filter { it.isDigit() } },
            label = { Text(stringResource(R.string.expansion_id)) },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Spacer(Modifier.height(8.dp))

        Button(
            onClick = {
                val token = jwtToken ?: return@Button
                scope.launch {
                    RemoteRepo.addCard(token, userId, collectionName, cardId)
                    onDone()
                }
            },
            enabled = cardId.isNotBlank()
        ) {
            Text(stringResource(R.string.cancel))
        }
    }

}
