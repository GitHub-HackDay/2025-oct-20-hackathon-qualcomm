package com.tinystories.domain.usecase

import com.tinystories.domain.model.GenerationResult
import com.tinystories.domain.model.GenerationState
import com.tinystories.domain.model.GenerationStep
import com.tinystories.domain.model.Story
import com.tinystories.domain.repository.MLRepository
import com.tinystories.domain.repository.StoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.UUID
import javax.inject.Inject

/**
 * Use case for generating character from audio recording
 */
class GenerateCharacterUseCase @Inject constructor(
    private val mlRepository: MLRepository,
    private val storyRepository: StoryRepository
) {
    
    /**
     * Generate character from audio file
     * Returns a flow of generation states and final result
     */
    suspend fun execute(audioPath: String): Flow<GenerationResult> = flow {
        try {
            emit(GenerationResult.Loading)
            
            // Step 1: Convert audio to text
            val transcription = mlRepository.audioToText(audioPath)
            
            // Step 2: Enhance description using LLM
            val enhancedPrompt = mlRepository.enhanceDescription(transcription)
            
            // Step 3: Generate character image
            val imagePath = mlRepository.generateImage(enhancedPrompt)
            
            // Step 4: Create and save story
            val story = Story(
                id = UUID.randomUUID().toString(),
                audioPath = audioPath,
                transcription = transcription,
                enhancedPrompt = enhancedPrompt,
                imagePath = imagePath,
                timestamp = System.currentTimeMillis()
            )
            
            storyRepository.saveStory(story)
            
            emit(GenerationResult.Success(story))
            
        } catch (e: Exception) {
            emit(GenerationResult.Error("Failed to generate character: ${e.message}"))
        }
    }
}