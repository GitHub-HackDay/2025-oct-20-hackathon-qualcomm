#include <jni.h>
#include <android/log.h>

/**
 * JNI bindings for TinyStories native functionality
 * Central place for native method registrations
 */

#define LOG_TAG "TinyStories-JNI"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

/**
 * Called when the native library is loaded
 */
extern "C" JNIEXPORT jint JNICALL
JNI_OnLoad(JavaVM *vm, void *reserved) {
    LOGI("TinyStories native library loaded");
    
    JNIEnv *env;
    if (vm->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION_1_6) != JNI_OK) {
        return JNI_ERR;
    }
    
    // TODO: Register native methods if needed
    // Register ExecuTorch wrapper methods
    // Register audio processor methods
    
    return JNI_VERSION_1_6;
}

/**
 * Called when the native library is unloaded
 */
extern "C" JNIEXPORT void JNICALL
JNI_OnUnload(JavaVM *vm, void *reserved) {
    LOGI("TinyStories native library unloaded");
    
    // TODO: Cleanup native resources
    // Release ExecuTorch runtime
    // Free audio processing resources
}