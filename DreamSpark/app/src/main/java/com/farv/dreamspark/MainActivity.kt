package com.farv.dreamspark

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.farv.dreamspark.ui.storyboard.VoiceToStoryboardScreen
import com.farv.dreamspark.ui.theme.DreamSparkTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DreamSparkTheme {
                VoiceToStoryboardScreen()
            }
        }
    }
}
