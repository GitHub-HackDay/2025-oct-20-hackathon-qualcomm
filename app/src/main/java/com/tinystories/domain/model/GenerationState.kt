package com.tinystories.domain.model

/**
 * Represents the state of character generation process
 */
data class GenerationState(
    val isGenerating: Boolean = false,
    val progress: Float = 0f,
    val currentStep: String = "",
    val error: String? = null
)

/**
 * Steps in the character generation pipeline
 */
enum class GenerationStep(val description: String) {
    PROCESSING_AUDIO("Processing your story..."),
    SPEECH_TO_TEXT("Understanding what you said..."),
    TEXT_PROCESSING("Creating character description..."),
    IMAGE_GENERATION("Bringing your character to life..."),
    FINALIZING("Adding magical touches..."),
    COMPLETED("Your character is ready!")
}

/**
 * Result of character generation
 */
sealed class GenerationResult {
    object Loading : GenerationResult()
    data class Success(val story: Story) : GenerationResult()
    data class Error(val message: String) : GenerationResult()
}