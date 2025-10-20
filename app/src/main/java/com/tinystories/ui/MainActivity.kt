package com.tinystories.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.tinystories.ui.navigation.TinyStoriesNavigation
import com.tinystories.ui.theme.TinyStoriesTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main Activity for TinyStories app
 * Uses Jetpack Compose for modern UI
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            TinyStoriesTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    TinyStoriesNavigation(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}