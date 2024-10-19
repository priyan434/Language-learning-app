import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun AdminScreen(firestore: FirebaseFirestore) {
    var phrase by remember { mutableStateOf("") }
    var translation by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Admin - Add a Phrase", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        // Input for Phrase
        OutlinedTextField(
            value = phrase,
            onValueChange = { phrase = it },
            label = { Text("Phrase") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Input for Translation
        OutlinedTextField(
            value = translation,
            onValueChange = { translation = it },
            label = { Text("Translation") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Button to add the phrase to Firestore
        Button(
            onClick = {
                if (phrase.isNotEmpty() && translation.isNotEmpty()) {
                    val phraseData = hashMapOf(
                        "phrase" to phrase,
                        "translation" to translation
                    )
                    firestore.collection("phrases")
                        .add(phraseData)
                        .addOnSuccessListener {
                            message = "Phrase added successfully!"
                            phrase = ""
                            translation = ""
                        }
                        .addOnFailureListener {
                            message = "Failed to add phrase: ${it.message}"
                        }
                } else {
                    message = "Please fill both fields."
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Phrase")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Display success or error message
        Text(text = message, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.error)
    }
}
