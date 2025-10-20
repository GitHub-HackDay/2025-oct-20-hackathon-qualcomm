package com.farv.dreamspark.ui.storyboard

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.media.AudioManager
import android.media.AudioDeviceInfo
import android.os.Build
import android.os.Looper
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import coil.imageLoader
import coil.request.ImageRequest
import com.farv.dreamspark.ml.AudioPreprocessor
import com.farv.dreamspark.ml.ModelManager
import com.farv.dreamspark.ml.SpeechToTextRecognizer
import com.farv.dreamspark.ml.WavLMInference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@Composable
fun VoiceToStoryboardScreen() {
    val context = LocalContext.current
    val scope = androidx.compose.runtime.rememberCoroutineScope()
    var isRecording by remember { mutableStateOf(false) }
    var isAudioRecorded by remember { mutableStateOf(false) }
    var currentGifUri by remember { mutableStateOf<String?>(null) }
    var audioDuration by remember { mutableStateOf(0) }
    var currentPlaybackPosition by remember { mutableStateOf(0) }
    var isAudioPlaying by remember { mutableStateOf(false) }
    var modelStatus by remember { mutableStateOf("Checking model...") }
    var inferenceResult by remember { mutableStateOf<String?>(null) }
    var isProcessing by remember { mutableStateOf(false) }
    var liveTranscript by remember { mutableStateOf("") }
    var fullTranscript by remember { mutableStateOf("") }
    var showTranscriptDialog by remember { mutableStateOf(false) }
    val audioRecorder = remember { AudioRecorder(context) }
    val modelManager = remember { ModelManager(context) }
    val wavLMInference = remember { WavLMInference(context) }
    val speechRecognizer = remember { SpeechToTextRecognizer(context) }

    DisposableEffect(Unit) {
        onDispose {
            audioRecorder.release()
            wavLMInference.release()
            speechRecognizer.release()
        }
    }
    LaunchedEffect(Unit) {
        launch {
            speechRecognizer.partialTranscript.collect { text ->
                liveTranscript = text
            }
        }
        launch {
            speechRecognizer.fullTranscript.collect { text ->
                fullTranscript = text
            }
        }
    }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
        scope.launch {
            if (!modelManager.isModelDownloaded()) {
                modelStatus = "Downloading model..."
                val result = modelManager.downloadModel { progress ->
                    modelStatus = "Downloading model... $progress%"
                }
                result.onSuccess { file ->
                    modelStatus = "Initializing model..."
                    withContext(Dispatchers.IO) {
                        if (wavLMInference.initialize(file)) {
                            modelStatus = "Model ready"
                        } else {
                            modelStatus = "Model initialization failed"
                        }
                    }
                }.onFailure {
                    modelStatus = "Model download failed: ${it.message}"
                }
            } else {
                modelStatus = "Initializing model..."
                withContext(Dispatchers.IO) {
                    val file = modelManager.getModelFile()
                    if (file != null && wavLMInference.initialize(file)) {
                        modelStatus = "Model ready"
                    } else {
                        modelStatus = "Model initialization failed"
                    }
                }
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
            Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = modelStatus, color = if (modelStatus == "Model ready") Color.Green else Color.White)
                if (isProcessing) {
                    Text(text = "Processing audio...", color = Color.Yellow)
                }
                if (inferenceResult != null) {
                    Text(text = "WavLM embeddings: $inferenceResult", color = Color.White)
                }
            }
            if (liveTranscript.isNotEmpty() && isRecording) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Live Transcript:",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = liveTranscript,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 2
                        )
                    }
                }
            }
            Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.padding(8.dp)) {
                RecordButton(isRecording) {
                    isRecording = !isRecording
                    if (isRecording) {
                        audioRecorder.start()
                        speechRecognizer.reset()
                        speechRecognizer.startListening()
                        inferenceResult = null
                        liveTranscript = ""
                    } else {
                        speechRecognizer.stopListening()
                        audioRecorder.stop()
                        isAudioRecorded = true
                        audioDuration = audioRecorder.getDuration()
                        scope.launch {
                            isProcessing = true
                            withContext(Dispatchers.IO) {
                                try {
                                    val audioFile = audioRecorder.getAudioFile()
                                    if (audioFile != null) {
                                        val features = AudioPreprocessor.extractAudioFeatures(audioFile)
                                        if (features != null) {
                                            val output = wavLMInference.runInference(features)
                                            if (output != null) {
                                                inferenceResult = "Embeddings: ${output.size} features"
                                            } else {
                                                inferenceResult = "Inference failed"
                                            }
                                        } else {
                                            inferenceResult = "Feature extraction failed"
                                        }
                                    } else {
                                        inferenceResult = "Audio file not found"
                                    }
                                } catch (e: Exception) {
                                    inferenceResult = "Error: ${e.message}"
                                }
                            }
                            isProcessing = false
                        }
                    }
                }
                PlayButton(
                        isAudioRecorded,
                        isAudioPlaying,
                        audioDuration,
                        currentPlaybackPosition
                ) {
                    isAudioPlaying = true
                    audioRecorder.play {
                        currentPlaybackPosition = it
                        if (it == audioDuration) {
                            isAudioPlaying = false
                            currentPlaybackPosition = 0
                        }
                    }
                }
                ViewTranscriptButton(
                    enabled = isAudioRecorded && fullTranscript.isNotEmpty(),
                    onClick = { showTranscriptDialog = true }
                )
            }
        }
    }
    if (showTranscriptDialog) {
        TranscriptDialog(
            transcript = fullTranscript,
            onDismiss = { showTranscriptDialog = false }
        )
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
                "Play ($totalDuration s)"
            } else {
                "Play Recording"
            }
    Button(onClick = onClick, enabled = enabled, modifier = Modifier.padding(8.dp)) {
        Text(text = displayTime)
    }
}
@Composable
fun ViewTranscriptButton(enabled: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.padding(8.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2))
    ) {
        Text(text = "View Transcript")
    }
}
@Composable
fun TranscriptDialog(transcript: String, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Full Transcript") },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Text(text = transcript.ifEmpty { "No transcript available" })
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
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
    private val handler = android.os.Handler(Looper.getMainLooper())
    private var progressRunnable: Runnable? = null
    fun getAudioFile(): File? = audioFile

    fun release() {
        mediaRecorder?.release()
        mediaRecorder = null
        player?.release()
        player = null
        progressRunnable?.let { handler.removeCallbacks(it) }
        progressRunnable = null
    }

    fun start() {
        audioFile = File(context.cacheDir, "audio.m4a")
        mediaRecorder = (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else {
            @Suppress("DEPRECATION") MediaRecorder()
        }).apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(audioFile?.absolutePath)
            try {
                prepare()
                start()
            } catch (e: Exception) {
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
    }

    fun getDuration(): Int {
        audioFile?.takeIf { it.exists() }?.let {
            val tempPlayer = MediaPlayer()
            try {
                tempPlayer.setDataSource(it.absolutePath)
                tempPlayer.prepare()
                return tempPlayer.duration
            } finally {
                tempPlayer.release()
            }
        }
        return 0
    }

    fun play(onProgress: (Int) -> Unit) {
        audioFile?.takeIf { it.exists() }?.let { file ->
            try {
                player?.release()
                player = MediaPlayer().apply {
                    setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
                    )
                    setDataSource(file.absolutePath)
                    prepare()

                    val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
                    audioManager.mode = AudioManager.MODE_NORMAL
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        val devices = audioManager.availableCommunicationDevices
                        val speaker = devices.find { it.type == AudioDeviceInfo.TYPE_BUILTIN_SPEAKER }
                        speaker?.let { audioManager.setCommunicationDevice(it) }
                    } else {
                        @Suppress("DEPRECATION") audioManager.isSpeakerphoneOn = true
                    }

                    setOnCompletionListener {
                        progressRunnable?.let { handler.removeCallbacks(it) }
                        onProgress(duration)
                        release()
                        player = null
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            audioManager.clearCommunicationDevice()
                        } else {
                            @Suppress("DEPRECATION") audioManager.isSpeakerphoneOn = false
                        }
                        audioManager.mode = AudioManager.MODE_NORMAL
                    }
                    start()
                    progressRunnable = object : Runnable {
                        override fun run() {
                            player?.let {
                                if (it.isPlaying) {
                                    onProgress(it.currentPosition)
                                    handler.postDelayed(this, 100)
                                }
                            }
                        }
                    }
                    handler.post(progressRunnable!!)
                }
            } catch (e: Exception) {
                Log.e("AudioRecorder", "Error playing audio", e)
            }
        }
    }
}
