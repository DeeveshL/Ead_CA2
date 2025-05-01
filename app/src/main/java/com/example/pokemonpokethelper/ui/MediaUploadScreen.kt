import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.util.UUID

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