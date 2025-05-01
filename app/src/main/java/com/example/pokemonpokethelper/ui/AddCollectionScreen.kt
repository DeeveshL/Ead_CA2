import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.dp
import com.example.pokemonpokethelper.data.FirestoreRepo
import kotlinx.coroutines.launch

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