package com.tinystories.ui.recording

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

/**
 * Recording screen where children can record their stories
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RecordingScreen(
    onNavigateToGeneration: (String) -> Unit,
    onNavigateToGallery: () -> Unit,
    viewModel: RecordingViewModel = hiltViewModel()
) {
    val recordingPermission = rememberPermissionState(
        android.Manifest.permission.RECORD_AUDIO
    )
    
    val uiState by viewModel.uiState.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Header
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            text = "🎤 Tell Your Story!",
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.primary
        )
        
        Text(
            text = "Press the button and tell me about your favorite character!",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground
        )
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Recording UI
        RecordingButton(
            isRecording = uiState.isRecording,
            isPermissionGranted = recordingPermission.status.isGranted,
            onRequestPermission = { recordingPermission.launchPermissionRequest() },
            onStartRecording = viewModel::startRecording,
            onStopRecording = viewModel::stopRecording
        )
        
        // Recording timer
        if (uiState.isRecording) {
            Text(
                text = "Recording: ${uiState.recordingDuration}s",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.error
            )
        }
        
        // Transcription preview
        if (uiState.transcription.isNotBlank()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "What I heard:",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = uiState.transcription,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
            
            // Generate button
            Button(
                onClick = { 
                    uiState.audioFilePath?.let { path ->
                        onNavigateToGeneration(path)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Create My Character!")
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Bottom navigation
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedButton(
                onClick = onNavigateToGallery,
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.PhotoLibrary, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("My Gallery")
            }
        }
    }
}

@Composable
private fun RecordingButton(
    isRecording: Boolean,
    isPermissionGranted: Boolean,
    onRequestPermission: () -> Unit,
    onStartRecording: () -> Unit,
    onStopRecording: () -> Unit
) {
    val buttonColor = if (isRecording) {
        MaterialTheme.colorScheme.error
    } else {
        MaterialTheme.colorScheme.primary
    }
    
    FloatingActionButton(
        onClick = {
            if (!isPermissionGranted) {
                onRequestPermission()
            } else if (isRecording) {
                onStopRecording()
            } else {
                onStartRecording()
            }
        },
        modifier = Modifier
            .size(120.dp)
            .clip(CircleShape),
        containerColor = buttonColor
    ) {
        Icon(
            imageVector = if (isRecording) Icons.Default.Stop else Icons.Default.Mic,
            contentDescription = if (isRecording) "Stop Recording" else "Start Recording",
            modifier = Modifier.size(48.dp),
            tint = Color.White
        )
    }
}