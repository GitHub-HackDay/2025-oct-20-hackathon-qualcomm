package com.farv.dreamspark.ml
import android.media.MediaExtractor
import android.media.MediaFormat
import android.util.Log
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.min
class AudioPreprocessor {
    companion object {
        private const val TAG = "AudioPreprocessor"
        private const val SAMPLE_RATE = 16000
        private const val MAX_AUDIO_LENGTH_SECONDS = 20
        private const val MAX_SAMPLES = SAMPLE_RATE * MAX_AUDIO_LENGTH_SECONDS
        fun extractAudioFeatures(audioFile: File): FloatArray? {
            if (!audioFile.exists()) {
                Log.e(TAG, "Audio file does not exist: ${audioFile.absolutePath}")
                return null
            }
            val extractor = MediaExtractor()
            return try {
                extractor.setDataSource(audioFile.absolutePath)
                var audioTrackIndex = -1
                for (i in 0 until extractor.trackCount) {
                    val format = extractor.getTrackFormat(i)
                    val mime = format.getString(MediaFormat.KEY_MIME)
                    if (mime?.startsWith("audio/") == true) {
                        audioTrackIndex = i
                        break
                    }
                }
                if (audioTrackIndex == -1) {
                    Log.e(TAG, "No audio track found")
                    return null
                }
                extractor.selectTrack(audioTrackIndex)
                val format = extractor.getTrackFormat(audioTrackIndex)
                val sampleRate = format.getInteger(MediaFormat.KEY_SAMPLE_RATE)
                val channelCount = format.getInteger(MediaFormat.KEY_CHANNEL_COUNT)
                Log.d(TAG, "Audio format - Sample rate: $sampleRate, Channels: $channelCount")
                val audioSamples = mutableListOf<Short>()
                val buffer = ByteBuffer.allocate(1024 * 1024)
                while (true) {
                    val sampleSize = extractor.readSampleData(buffer, 0)
                    if (sampleSize < 0) break
                    buffer.position(0)
                    buffer.limit(sampleSize)
                    buffer.order(ByteOrder.LITTLE_ENDIAN)
                    while (buffer.remaining() >= 2) {
                        audioSamples.add(buffer.short)
                    }
                    extractor.advance()
                }
                if (audioSamples.isEmpty()) {
                    Log.e(TAG, "No audio samples extracted")
                    return null
                }
                val monoSamples = if (channelCount > 1) {
                    audioSamples.chunked(channelCount).map { chunk ->
                        (chunk.sum() / channelCount).toShort()
                    }
                } else {
                    audioSamples
                }
                val resampledSamples = if (sampleRate != SAMPLE_RATE) {
                    resample(monoSamples, sampleRate, SAMPLE_RATE)
                } else {
                    monoSamples
                }
                val finalSamples = if (resampledSamples.size > MAX_SAMPLES) {
                    resampledSamples.subList(0, MAX_SAMPLES)
                } else {
                    resampledSamples + List(MAX_SAMPLES - resampledSamples.size) { 0.toShort() }
                }
                finalSamples.map { it.toFloat() / Short.MAX_VALUE }.toFloatArray()
            } catch (e: Exception) {
                Log.e(TAG, "Error extracting audio features", e)
                null
            } finally {
                extractor.release()
            }
        }
        private fun resample(samples: List<Short>, fromRate: Int, toRate: Int): List<Short> {
            if (fromRate == toRate) return samples
            val ratio = fromRate.toDouble() / toRate
            val outputSize = (samples.size / ratio).toInt()
            return List(outputSize) { i ->
                val srcIndex = (i * ratio).toInt()
                samples[min(srcIndex, samples.size - 1)]
            }
        }
    }
}

