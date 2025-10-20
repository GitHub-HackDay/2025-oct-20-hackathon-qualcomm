package com.tinystories.domain.model

/**
 * Domain model representing a story created by a child
 */
data class Story(
    val id: String,
    val audioPath: String,
    val transcription: String,
    val enhancedPrompt: String,
    val imagePath: String,
    val timestamp: Long,
    val isFavorite: Boolean = false,
    val duration: Int = 0 // in seconds
) {
    val formattedDate: String
        get() = java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault())
            .format(java.util.Date(timestamp))
    
    val shortDate: String
        get() = java.text.SimpleDateFormat("MMM dd", java.util.Locale.getDefault())
            .format(java.util.Date(timestamp))
}