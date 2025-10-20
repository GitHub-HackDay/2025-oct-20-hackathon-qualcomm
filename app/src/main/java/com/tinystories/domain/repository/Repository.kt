package com.tinystories.domain.repository

import com.tinystories.domain.model.GenerationResult
import com.tinystories.domain.model.Story
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for story data operations
 */
interface StoryRepository {
    
    /**
     * Get all stories ordered by timestamp (newest first)
     */
    fun getAllStories(): Flow<List<Story>>
    
    /**
     * Get a specific story by ID
     */
    suspend fun getStoryById(id: String): Story?
    
    /**
     * Save a new story
     */
    suspend fun saveStory(story: Story)
    
    /**
     * Update an existing story
     */
    suspend fun updateStory(story: Story)
    
    /**
     * Delete a story and associated files
     */
    suspend fun deleteStory(storyId: String)
    
    /**
     * Toggle favorite status of a story
     */
    suspend fun toggleFavorite(storyId: String)
    
    /**
     * Get favorite stories only
     */
    fun getFavoriteStories(): Flow<List<Story>>
}

/**
 * Repository interface for ML model operations
 */
interface MLRepository {
    
    /**
     * Initialize ML models and runtime
     */
    suspend fun initializeModels(): Boolean
    
    /**
     * Convert audio to text using speech-to-text model
     */
    suspend fun audioToText(audioPath: String): String
    
    /**
     * Enhance text description using language model
     */
    suspend fun enhanceDescription(text: String): String
    
    /**
     * Generate character image from text description
     */
    suspend fun generateImage(prompt: String): String
    
    /**
     * Get model loading progress
     */
    fun getModelLoadingProgress(): Flow<Float>
}