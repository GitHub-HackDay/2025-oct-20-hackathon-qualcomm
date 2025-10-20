package com.tinystories

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * TinyStories Application class
 * Entry point for the app with Hilt dependency injection
 */
@HiltAndroidApp
class TinyStoriesApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        // Initialize ExecuTorch runtime (placeholder for now)
        // initializeExecuTorch()
    }
    
    /**
     * Initialize ExecuTorch runtime for AI model inference
     * This will be implemented when ExecuTorch integration is added
     */
    private fun initializeExecuTorch() {
        // TODO: Initialize ExecuTorch runtime
        // Load models from assets
        // Configure Qualcomm backend
    }
}