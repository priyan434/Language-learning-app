package com.example.languagelearningapp

import android.speech.tts.TextToSpeech
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
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

@Composable
fun PhrasesScreen(textToSpeech: TextToSpeech, firestore: FirebaseFirestore) {
    var phrases by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        firestore.collection("phrases").get()
            .addOnSuccessListener { result ->
                phrases = result.map { document ->
                    val phrase = document.getString("phrase") ?: ""
                    val translation = document.getString("translation") ?: ""
                    Pair(phrase, translation)
                }
                loading = false
            }
            .addOnFailureListener {
                loading = false
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "French Phrases",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (loading) {
            CircularProgressIndicator()
        } else if (phrases.isNotEmpty()) {
            PhrasesList(textToSpeech, phrases)
        } else {
            Text("No phrases available.")
        }
    }
}

@Composable
fun PhrasesList(textToSpeech: TextToSpeech, phrases: List<Pair<String, String>>) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        phrases.forEach { (phrase, translation) ->
            PhraseCardItem(phrase = phrase, translation = translation, textToSpeech = textToSpeech)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhraseCardItem(phrase: String, translation: String, textToSpeech: TextToSpeech) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                // Speak the phrase using Text-to-Speech
                textToSpeech.speak(phrase, TextToSpeech.QUEUE_FLUSH, null, null)
            },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = phrase,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = translation,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                contentDescription = "Play pronunciation",
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewPhrasesScreen() {
    // Preview doesn't need real TextToSpeech instance, use a mock if needed
    // PhrasesScreen(TextToSpeech(context))
}
