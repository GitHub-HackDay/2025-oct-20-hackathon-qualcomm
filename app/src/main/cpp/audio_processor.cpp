#include <jni.h>
#include <android/log.h>
#include <vector>

#define LOG_TAG "TinyStories-Audio"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

/**
 * Audio processing utilities for TinyStories
 * Handles audio preprocessing for speech-to-text models
 */

extern "C" JNIEXPORT jfloatArray JNICALL
Java_com_tinystories_data_audio_AudioProcessor_preprocessAudio(
        JNIEnv *env,
        jobject /* this */,
        jfloatArray audioData,
        jint sampleRate) {
    
    LOGI("Preprocessing audio data, sample rate: %d", sampleRate);
    
    jsize len = env->GetArrayLength(audioData);
    jfloat *audio = env->GetFloatArrayElements(audioData, nullptr);
    
    // TODO: Implement audio preprocessing
    // - Noise reduction
    // - Normalization
    // - Resampling to 16kHz if needed
    // - Apply windowing
    
    // For now, just copy the input data
    std::vector<float> processed(audio, audio + len);
    
    // Simple normalization example
    float max_val = 0.0f;
    for (int i = 0; i < len; i++) {
        max_val = std::max(max_val, std::abs(processed[i]));
    }
    
    if (max_val > 0.0f) {
        for (int i = 0; i < len; i++) {
            processed[i] /= max_val;
        }
    }
    
    jfloatArray result = env->NewFloatArray(len);
    env->SetFloatArrayRegion(result, 0, len, processed.data());
    
    env->ReleaseFloatArrayElements(audioData, audio, 0);
    
    LOGI("Audio preprocessing completed");
    return result;
}

extern "C" JNIEXPORT jfloatArray JNICALL
Java_com_tinystories_data_audio_AudioProcessor_extractFeatures(
        JNIEnv *env,
        jobject /* this */,
        jfloatArray audioData,
        jint sampleRate) {
    
    LOGI("Extracting audio features for speech recognition");
    
    // TODO: Implement feature extraction
    // - MFCC features
    // - Mel-scale filterbank features
    // - Log-mel spectrogram
    
    jsize len = env->GetArrayLength(audioData);
    jfloat *audio = env->GetFloatArrayElements(audioData, nullptr);
    
    // Placeholder: return simplified features
    int feature_size = 80; // Typical mel-filterbank size
    int num_frames = len / 160; // Assuming 10ms hop size
    int total_features = feature_size * num_frames;
    
    std::vector<float> features(total_features, 0.0f);
    
    jfloatArray result = env->NewFloatArray(total_features);
    env->SetFloatArrayRegion(result, 0, total_features, features.data());
    
    env->ReleaseFloatArrayElements(audioData, audio, 0);
    
    return result;
}