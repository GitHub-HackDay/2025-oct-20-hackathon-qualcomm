package com.tinystories.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tinystories.ui.recording.RecordingScreen
import com.tinystories.ui.gallery.GalleryScreen
import com.tinystories.ui.generation.GenerationScreen

/**
 * Navigation destinations for TinyStories app
 */
sealed class Destination(val route: String) {
    object Home : Destination("home")
    object Recording : Destination("recording")
    object Generation : Destination("generation/{audioPath}") {
        fun createRoute(audioPath: String) = "generation/$audioPath"
    }
    object Gallery : Destination("gallery")
}

/**
 * Main navigation component for TinyStories app
 */
@Composable
fun TinyStoriesNavigation(
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    
    NavHost(
        navController = navController,
        startDestination = Destination.Home.route,
        modifier = modifier
    ) {
        composable(Destination.Home.route) {
            RecordingScreen(
                onNavigateToGeneration = { audioPath ->
                    navController.navigate(Destination.Generation.createRoute(audioPath))
                },
                onNavigateToGallery = {
                    navController.navigate(Destination.Gallery.route)
                }
            )
        }
        
        composable(Destination.Recording.route) {
            RecordingScreen(
                onNavigateToGeneration = { audioPath ->
                    navController.navigate(Destination.Generation.createRoute(audioPath))
                },
                onNavigateToGallery = {
                    navController.navigate(Destination.Gallery.route)
                }
            )
        }
        
        composable(Destination.Generation.route) { backStackEntry ->
            val audioPath = backStackEntry.arguments?.getString("audioPath") ?: ""
            GenerationScreen(
                audioPath = audioPath,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToGallery = {
                    navController.navigate(Destination.Gallery.route)
                }
            )
        }
        
        composable(Destination.Gallery.route) {
            GalleryScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}