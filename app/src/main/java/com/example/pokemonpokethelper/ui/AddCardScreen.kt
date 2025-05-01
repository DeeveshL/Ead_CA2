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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.pokemonpokethelper.data.FirestoreRepo
import kotlinx.coroutines.launch

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
