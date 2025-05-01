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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.pokemonpokethelper.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


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
            label = { Text(stringResource(R.string.email)) }, singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
        OutlinedTextField(value = password, onValueChange = { password = it },
            label = { Text(stringResource(R.string.password)) }, singleLine = true,
            visualTransformation = PasswordVisualTransformation()
        )

        errorMsg?.let { Text(it, color = MaterialTheme.colorScheme.error) }

        Button(onClick = {
            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener { onLoginSuccess() }
                .addOnFailureListener { errorMsg = it.localizedMessage }
        }, Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.login))
        }

        Spacer(Modifier.height(8.dp))
        TextButton(onClick = onSignUpClick, Modifier.align(Alignment.End)) {
            Text(stringResource(R.string.sign_up))
        }
    }
}
