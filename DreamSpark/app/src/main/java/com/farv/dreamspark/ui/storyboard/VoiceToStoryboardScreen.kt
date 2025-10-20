package com.farv.dreamspark.ui.storyboard

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.imageLoader
import coil.request.ImageRequest

@Composable
fun VoiceToStoryboardScreen() {
    val context = LocalContext.current
    var isRecording by remember { mutableStateOf(false) }
    var currentGifUri by remember { mutableStateOf<String?>(null) }
    val audioRecorder = remember { AudioRecorder(context) }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            DisplayCanvas(currentGifUri)
            RecordButton(isRecording) {
                isRecording = !isRecording
                if (isRecording) {
                    audioRecorder.start()
                } else {
                    audioRecorder.stop()
                }
            }
        }
    }
}

@Composable
private fun RecordButton(isRecording: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
    ) {
        Text(text = if (isRecording) "Stop Recording" else "Start Recording")
    }
}

@Composable
private fun DisplayCanvas(gifUri: String?) {
    if (gifUri != null) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(gifUri)
                .key(gifUri) // Restart animation when the URI changes
                .build(),
            contentDescription = "Generated Storyboard",
            imageLoader = LocalContext.current.imageLoader,
            modifier = Modifier.fillMaxSize()
        )
    }
}

// A placeholder for the audio recorder
class AudioRecorder(private val context: Context) {
    fun start() {
        // TODO: Implement audio recording
    }

    fun stop() {
        // TODO: Implement audio recording stop and processing
    }
}