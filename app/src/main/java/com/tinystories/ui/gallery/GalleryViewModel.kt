package com.tinystories.ui.gallery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.tinystories.domain.model.Story
import javax.inject.Inject

data class GalleryUiState(
    val isLoading: Boolean = false,
    val stories: List<Story> = emptyList(),
    val selectedStory: Story? = null,
    val error: String? = null
)

@HiltViewModel
class GalleryViewModel @Inject constructor(
    // TODO: Inject repository for story data access
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(GalleryUiState())
    val uiState: StateFlow<GalleryUiState> = _uiState.asStateFlow()
    
    fun loadStories() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                
                // TODO: Load stories from database
                // val stories = storyRepository.getAllStories()
                
                // For demo purposes, create sample stories
                val sampleStories = createSampleStories()
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    stories = sampleStories
                )
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to load stories: ${e.message}"
                )
            }
        }
    }
    
    fun selectStory(story: Story) {
        _uiState.value = _uiState.value.copy(selectedStory = story)
    }
    
    fun toggleFavorite(story: Story) {
        viewModelScope.launch {
            // TODO: Update story in database
            val updatedStories = _uiState.value.stories.map { existingStory ->
                if (existingStory.id == story.id) {
                    existingStory.copy(isFavorite = !existingStory.isFavorite)
                } else {
                    existingStory
                }
            }
            
            _uiState.value = _uiState.value.copy(stories = updatedStories)
        }
    }
    
    fun deleteStory(story: Story) {
        viewModelScope.launch {
            // TODO: Delete story from database and file system
            val updatedStories = _uiState.value.stories.filter { it.id != story.id }
            _uiState.value = _uiState.value.copy(stories = updatedStories)
        }
    }
    
    private fun createSampleStories(): List<Story> {
        // Sample stories for demo - in real app these would come from database
        return listOf(
            Story(
                id = "1",
                audioPath = "/path/to/audio1.wav",
                transcription = "a brave princess with long golden hair and a sparkling blue dress",
                enhancedPrompt = "A brave princess with long golden hair, wearing a sparkly blue dress with silver stars",
                imagePath = "", // Placeholder - would be actual generated image path
                timestamp = System.currentTimeMillis() - 86400000, // 1 day ago
                isFavorite = true
            ),
            Story(
                id = "2",
                audioPath = "/path/to/audio2.wav",
                transcription = "a friendly dragon with green scales and purple wings",
                enhancedPrompt = "A friendly dragon with bright green scales, purple wings, and a big smile",
                imagePath = "", // Placeholder
                timestamp = System.currentTimeMillis() - 172800000, // 2 days ago
                isFavorite = false
            ),
            Story(
                id = "3",
                audioPath = "/path/to/audio3.wav",
                transcription = "a magical unicorn with a rainbow mane and silver horn",
                enhancedPrompt = "A magical unicorn with flowing rainbow mane, silver horn, and sparkly white coat",
                imagePath = "", // Placeholder
                timestamp = System.currentTimeMillis() - 259200000, // 3 days ago
                isFavorite = true
            )
        )
    }
}