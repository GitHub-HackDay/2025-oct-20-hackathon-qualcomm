package com.farv.dreamspark.ui.storyboard

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.imageLoader
import coil.request.ImageRequest
import java.io.File
import java.io.IOException

@Composable
fun VoiceToStoryboardScreen() {
    val context = LocalContext.current
    var isRecording by remember { mutableStateOf(false) }
    var isAudioRecorded by remember { mutableStateOf(false) }
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
            Row(horizontalArrangement = Arrangement.Center) {
                RecordButton(isRecording) {
                    isRecording = !isRecording
                    if (isRecording) {
                        audioRecorder.start()
                    } else {
                        audioRecorder.stop()
                        isAudioRecorded = true
                    }
                }
                PlayButton(isAudioRecorded) {
                    audioRecorder.play()
                }
            }
        }
    }
}

@Composable
private fun RecordButton(isRecording: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
        modifier = Modifier.padding(8.dp)
    ) {
        Text(text = if (isRecording) "Stop Recording" else "Start Recording")
    }
}

@Composable
fun PlayButton(enabled: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.padding(8.dp)
    ) {
        Text(text = "Play Recording")
    }
}


@Composable
private fun ColumnScope.DisplayCanvas(gifUri: String?) {
    Box(
        modifier = Modifier
            .weight(1f)
            .fillMaxWidth()
            .padding(16.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0D47A1), // Deep Blue
                        Color(0xFF1976D2), // Medium Blue
                        Color(0xFF42A5F5)  // Light Blue
                    )
                )
            )
            .border(2.dp, Color.DarkGray),
        contentAlignment = Alignment.Center
    ) {
        if (gifUri != null) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(gifUri)
                    .build(),
                contentDescription = "Generated Storyboard",
                imageLoader = LocalContext.current.imageLoader,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

// A placeholder for the audio recorder
class AudioRecorder(private val context: Context) {
    private var audioFile: File? = null
    private var player: MediaPlayer? = null

    fun start() {
        // TODO: Implement audio recording
        val file = File(context.cacheDir, "audio.3gp")
        audioFile = file
    }

    fun stop() {
        // TODO: Implement audio recording stop and processing
    }

    fun play() {
        if (audioFile?.exists() == true) {
            try {
                player?.release()
                player = MediaPlayer().apply {
                    setDataSource(audioFile?.absolutePath)
                    prepare()
                    start()
                }
            } catch (e: IOException) {
                Log.e("AudioRecorder", "Error playing audio", e)
            }
        } else {
            Log.e("AudioRecorder", "Audio file not found")
        }
    }
}