package com.tinystories.ui.generation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage

/**
 * Screen that shows AI character generation in progress and results
 */
@Composable
fun GenerationScreen(
    audioPath: String,
    onNavigateBack: () -> Unit,
    onNavigateToGallery: () -> Unit,
    viewModel: GenerationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    LaunchedEffect(audioPath) {
        viewModel.generateCharacter(audioPath)
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header with back button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            
            Text(
                text = "Creating Magic ✨",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )
            
            IconButton(onClick = onNavigateToGallery) {
                Icon(Icons.Default.PhotoLibrary, contentDescription = "Gallery")
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        when {
            uiState.isGenerating -> {
                GenerationProgress(
                    progress = uiState.progress,
                    currentStep = uiState.currentStep
                )
            }
            
            uiState.generatedImagePath != null -> {
                GeneratedCharacterResult(
                    imagePath = uiState.generatedImagePath!!,
                    description = uiState.originalTranscription,
                    onRegenerateClick = { viewModel.regenerateCharacter() },
                    onSaveClick = { viewModel.saveCharacter() }
                )
            }
            
            uiState.error != null -> {
                GenerationError(
                    error = uiState.error!!,
                    onRetryClick = { viewModel.generateCharacter(audioPath) }
                )
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Bottom actions
        if (uiState.generatedImagePath != null) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(
                    onClick = { viewModel.regenerateCharacter() },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Try Again")
                }
                
                Button(
                    onClick = onNavigateToGallery,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Keep It!")
                }
            }
        }
    }
}

@Composable
private fun GenerationProgress(
    progress: Float,
    currentStep: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Animated character creation icon
        Card(
            modifier = Modifier.size(200.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            shape = RoundedCornerShape(24.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    progress = progress,
                    modifier = Modifier.size(80.dp),
                    strokeWidth = 6.dp,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Text(
                    text = "🎨",
                    style = MaterialTheme.typography.headlineLarge
                )
            }
        }
        
        Text(
            text = currentStep,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground
        )
        
        Text(
            text = "This might take a moment while I create your character...",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun GeneratedCharacterResult(
    imagePath: String,
    description: String,
    onRegenerateClick: () -> Unit,
    onSaveClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Your Character is Ready! 🎉",
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.primary
        )
        
        // Generated character image
        Card(
            modifier = Modifier
                .size(300.dp)
                .clip(RoundedCornerShape(16.dp))
        ) {
            AsyncImage(
                model = imagePath,
                contentDescription = "Generated character",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
        
        // Original description
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Your Story:",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
    }
}

@Composable
private fun GenerationError(
    error: String,
    onRetryClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "😔",
            style = MaterialTheme.typography.displayLarge
        )
        
        Text(
            text = "Oops! Something went wrong",
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.error
        )
        
        Text(
            text = error,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )
        
        Button(
            onClick = onRetryClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error
            )
        ) {
            Icon(Icons.Default.Refresh, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Try Again")
        }
    }
}