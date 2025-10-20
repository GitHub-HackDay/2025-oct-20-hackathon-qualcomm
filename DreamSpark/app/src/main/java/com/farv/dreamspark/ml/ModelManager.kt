package com.farv.dreamspark.ml
import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.URL
class ModelManager(private val context: Context) {
    companion object {
        private const val TAG = "ModelManager"
        private const val MODEL_FILENAME = "wavlm_base_plus.tflite"
        private const val MODEL_URL = "https://huggingface.co/qualcomm/HuggingFace-WavLM-Base-Plus/resolve/main/HuggingFace-WavLM-Base-Plus.tflite"
    }
    private fun getModelFilePath(): File = File(context.filesDir, MODEL_FILENAME)
    fun isModelDownloaded(): Boolean {
        val file = getModelFilePath()
        return file.exists() && file.length() > 0
    }
    suspend fun downloadModel(onProgress: (Int) -> Unit = {}): Result<File> = withContext(Dispatchers.IO) {
        try {
            val file = getModelFilePath()
            if (isModelDownloaded()) {
                Log.d(TAG, "Model already exists")
                return@withContext Result.success(file)
            }
            Log.d(TAG, "Downloading model from $MODEL_URL")
            val url = URL(MODEL_URL)
            val connection = url.openConnection()
            connection.connect()
            val fileLength = connection.contentLength
            connection.getInputStream().use { input ->
                FileOutputStream(file).use { output ->
                    val buffer = ByteArray(8192)
                    var total = 0L
                    var count: Int
                    while (input.read(buffer).also { count = it } != -1) {
                        total += count
                        if (fileLength > 0) {
                            val progress = (total * 100 / fileLength).toInt()
                            onProgress(progress)
                        }
                        output.write(buffer, 0, count)
                    }
                }
            }
            Log.d(TAG, "Model downloaded successfully")
            Result.success(file)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to download model", e)
            val file = getModelFilePath()
            if (file.exists()) {
                file.delete()
            }
            Result.failure(e)
        }
    }
    fun getModelFile(): File? {
        return if (isModelDownloaded()) getModelFilePath() else null
    }
    fun deleteModel() {
        val file = getModelFilePath()
        if (file.exists()) {
            file.delete()
            Log.d(TAG, "Model deleted")
        }
    }
}

