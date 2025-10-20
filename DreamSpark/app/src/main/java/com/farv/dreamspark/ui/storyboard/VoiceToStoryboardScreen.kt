package com.farv.dreamspark.ui.storyboard

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.imageLoader
import coil.request.ImageRequest
import java.io.File
import java.io.IOException
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

@Composable
fun VoiceToStoryboardScreen() {
    val context = LocalContext.current
    var isRecording by remember { mutableStateOf(false) }
    var isAudioRecorded by remember { mutableStateOf(false) }
    var currentGifUri by remember { mutableStateOf<String?>(null) }
    var audioDuration by remember { mutableStateOf(0) }
    var currentPlaybackPosition by remember { mutableStateOf(0) }
    var isAudioPlaying by remember { mutableStateOf(false) }
    val audioRecorder = remember { AudioRecorder(context) }
    val scope = rememberCoroutineScope()

    // Request audio permission
    val requestPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        isGranted: Boolean ->
        if (isGranted) {
            Log.d("VoiceToStoryboardScreen", "RECORD_AUDIO permission granted")
        } else {
            Log.e("VoiceToStoryboardScreen", "RECORD_AUDIO permission denied")
            // Handle the case where permission is denied
        }
    }

    LaunchedEffect(Unit) {
        when {
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED -> {
                Log.d("VoiceToStoryboardScreen", "RECORD_AUDIO permission already granted")
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        }
    }


    Scaffold { innerPadding ->
        Column(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
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
                        audioDuration = audioRecorder.getDuration()
                    }
                }
                PlayButton(isAudioRecorded, isAudioPlaying, audioDuration, currentPlaybackPosition) {
                    isAudioPlaying = true
                    audioRecorder.play {
                        currentPlaybackPosition = it
                        if (it == audioDuration) {
                            isAudioPlaying = false
                            currentPlaybackPosition = 0
                        }
                    }
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
    ) { Text(text = if (isRecording) "Stop Recording" else "Start Recording") }
}

@Composable
fun PlayButton(
        enabled: Boolean,
        isAudioPlaying: Boolean,
        audioDuration: Int,
        currentPlaybackPosition: Int,
        onClick: () -> Unit
) {
    val displayTime =
            if (isAudioPlaying) {
                val timeLeft = (audioDuration - currentPlaybackPosition) / 1000
                "$timeLeft s left"
            } else if (enabled) {
                val totalDuration = audioDuration / 1000
                "Play Recording ($totalDuration s)"
            } else {
                "Play Recording"
            }
    Button(onClick = onClick, enabled = enabled, modifier = Modifier.padding(8.dp)) {
        Text(text = displayTime)
    }
}

@Composable
private fun ColumnScope.DisplayCanvas(gifUri: String?) {
    Box(
            modifier =
                    Modifier.weight(1f)
                            .fillMaxWidth()
                            .padding(16.dp)
                            .background(
                                    brush =
                                            Brush.verticalGradient(
                                                    colors =
                                                            listOf(
                                                                    Color(0xFF0D47A1), // Deep Blue
                                                                    Color(
                                                                            0xFF1976D2
                                                                    ), // Medium Blue
                                                                    Color(0xFF42A5F5) // Light Blue
                                                            )
                                            )
                            )
                            .border(2.dp, Color.DarkGray),
            contentAlignment = Alignment.Center
    ) {
        if (gifUri != null) {
            AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current).data(gifUri).build(),
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
    private var mediaRecorder: MediaRecorder? = null
    private var player: MediaPlayer? = null
    private var isCurrentlyPlaying: Boolean = false

    fun start() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            audioFile = File(context.cacheDir, "audio.m4a")
        } else {
            @Suppress("DEPRECATION") audioFile = File(context.cacheDir, "audio.3gp")
        }

        mediaRecorder =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            MediaRecorder(context)
                        } else {
                            @Suppress("DEPRECATION") MediaRecorder()
                        }
                        .apply {
                            setAudioSource(MediaRecorder.AudioSource.MIC)
                            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                            setOutputFile(audioFile?.absolutePath)
                            try {
                                prepare()
                                start()
                                Log.d("AudioRecorder", "Recording started")
                            } catch (e: Exception) { // Changed IOException to Exception
                                Log.e("AudioRecorder", "Failed to start recording", e)
                            }
                        }
    }

    fun stop() {
        mediaRecorder?.apply {
            stop()
            release()
        }
        mediaRecorder = null
        Log.d("AudioRecorder", "Recording stopped")
    }

    fun getDuration(): Int {
        if (audioFile?.exists() == true) {
            MediaPlayer().apply {
                setDataSource(audioFile?.absolutePath)
                prepare()
                val duration = duration
                release()
                return duration
            }
        }
        return 0
    }

    fun play(onProgress: (Int) -> Unit) {
        if (audioFile?.exists() == true) {
            try {
                player?.release()
                player = MediaPlayer().apply {
                    setDataSource(audioFile?.absolutePath)
                    prepare()
                    start()
                    isCurrentlyPlaying = true

                    // Update progress periodically
                    val updateInterval = 100L // milliseconds
                    val runnable =
                            object : Runnable {
                                override fun run() {
                                    if (isCurrentlyPlaying) {
                                        onProgress(currentPosition)
                                        if (currentPosition < duration) {
                                            // schedule next update
                                            player?.let { p ->
                                                android.os.Handler()
                                                        .postDelayed(this, updateInterval)
                                            }
                                        } else {
                                            isCurrentlyPlaying = false
                                            onProgress(
                                                    duration
                                            ) // Ensure last update is full duration
                                            release()
                                        }
                                    }
                                }
                            }
                    player?.setOnCompletionListener {
                        isCurrentlyPlaying = false
                        onProgress(duration)
                        release()
                    }
                    android.os.Handler().postDelayed(runnable, updateInterval)
                }
            } catch (e: IOException) {
                Log.e("AudioRecorder", "Error playing audio", e)
            }
        } else {
            Log.e("AudioRecorder", "Audio file not found")
        }
    }
}
