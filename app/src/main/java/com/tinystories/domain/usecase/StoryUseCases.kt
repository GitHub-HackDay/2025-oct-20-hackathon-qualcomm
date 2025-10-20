package com.tinystories.domain.usecase

import com.tinystories.domain.model.Story
import com.tinystories.domain.repository.StoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for managing story gallery operations
 */
class GetStoriesUseCase @Inject constructor(
    private val storyRepository: StoryRepository
) {
    
    /**
     * Get all stories
     */
    fun execute(): Flow<List<Story>> {
        return storyRepository.getAllStories()
    }
    
    /**
     * Get favorite stories only
     */
    fun getFavorites(): Flow<List<Story>> {
        return storyRepository.getFavoriteStories()
    }
}

/**
 * Use case for toggling story favorite status
 */
class ToggleFavoriteUseCase @Inject constructor(
    private val storyRepository: StoryRepository
) {
    
    suspend fun execute(storyId: String) {
        storyRepository.toggleFavorite(storyId)
    }
}

/**
 * Use case for deleting a story
 */
class DeleteStoryUseCase @Inject constructor(
    private val storyRepository: StoryRepository
) {
    
    suspend fun execute(storyId: String) {
        storyRepository.deleteStory(storyId)
    }
}