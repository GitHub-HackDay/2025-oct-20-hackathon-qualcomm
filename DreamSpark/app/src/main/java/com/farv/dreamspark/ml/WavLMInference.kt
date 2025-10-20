package com.farv.dreamspark.ml

import android.content.Context
import android.util.Log
import java.io.File
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import org.tensorflow.lite.Interpreter

class WavLMInference(private val context: Context) {
    private var interpreter: Interpreter? = null
    private var isInitialized = false
    companion object {
        private const val TAG = "WavLMInference"
        private const val MODEL_FILENAME = "wavlm_base_plus.tflite"
        private const val INPUT_SIZE = 320000
    }
    fun initialize(modelFile: File): Boolean {
        return try {
            val modelBuffer = loadModelFile(modelFile)
            val options =
                    Interpreter.Options().apply {
                        setNumThreads(4)
                        setUseNNAPI(true)
                    }
            interpreter = Interpreter(modelBuffer, options)
            isInitialized = true
            Log.d(TAG, "WavLM model initialized successfully")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize model", e)
            false
        }
    }
    fun runInference(audioFeatures: FloatArray): FloatArray? {
        if (!isInitialized || interpreter == null) {
            Log.e(TAG, "Model not initialized")
            return null
        }
        return try {
            val inputArray =
                    if (audioFeatures.size > INPUT_SIZE) {
                        audioFeatures.copyOfRange(0, INPUT_SIZE)
                    } else {
                        audioFeatures + FloatArray(INPUT_SIZE - audioFeatures.size)
                    }
            val input = Array(1) { FloatArray(INPUT_SIZE) }
            input[0] = inputArray
            val outputShape = interpreter!!.getOutputTensor(0).shape()
            val output =
                    Array(outputShape[0]) { Array(outputShape[1]) { FloatArray(outputShape[2]) } }
            interpreter!!.run(input, output)
            Log.d(TAG, "Inference completed. Output shape: ${outputShape.contentToString()}")
            output[0].flatMap { it.asIterable() }.toFloatArray()
        } catch (e: Exception) {
            Log.e(TAG, "Inference failed", e)
            null
        }
    }
    fun release() {
        interpreter?.close()
        interpreter = null
        isInitialized = false
        Log.d(TAG, "Model released")
    }
    private fun loadModelFile(modelFile: File): MappedByteBuffer {
        val inputStream = FileInputStream(modelFile)
        val fileChannel = inputStream.channel
        val startOffset = 0L
        val declaredLength = fileChannel.size()
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }
}
