# TinyStories Android App

TinyStories is an Android mobile application that transforms children's audio dictations into illustrated story characters using on-device AI inference with Qualcomm hardware acceleration and ExecuTorch.

## 🎯 Features

- **Voice Recording**: Children can record their story ideas with a simple tap
- **Speech-to-Text**: Convert audio to text using on-device AI models
- **Story Enhancement**: AI-powered text processing to create vivid character descriptions
- **Character Generation**: Transform descriptions into colorful, child-friendly illustrations
- **Story Gallery**: Save and browse all created characters
- **Privacy-First**: All processing happens on-device, no cloud dependencies
- **Offline-Capable**: Works without internet connection

## 🏗️ Architecture

### Technology Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM with Clean Architecture
- **Dependency Injection**: Dagger Hilt
- **ML Framework**: ExecuTorch (PyTorch)
- **Hardware Acceleration**: Qualcomm QNN
- **Minimum SDK**: Android 10 (API 29)
- **Target SDK**: Android 15 (API 35)

### Project Structure

```
app/
├── src/main/
│   ├── java/com/tinystories/
│   │   ├── ui/                     # UI layer (Compose screens)
│   │   │   ├── recording/          # Audio recording screens
│   │   │   ├── generation/         # Character generation screens
│   │   │   ├── gallery/            # Story gallery screens
│   │   │   ├── theme/              # App theming
│   │   │   └── navigation/         # Navigation logic
│   │   ├── domain/                 # Business logic layer
│   │   │   ├── model/              # Domain models
│   │   │   ├── usecase/            # Use cases
│   │   │   └── repository/         # Repository interfaces
│   │   ├── data/                   # Data layer
│   │   │   ├── local/              # Local storage
│   │   │   ├── ml/                 # ML model integration
│   │   │   └── audio/              # Audio processing
│   │   ├── di/                     # Dependency injection
│   │   └── util/                   # Utilities
│   ├── cpp/                        # Native C++ code
│   │   ├── executorch_wrapper.cpp  # ExecuTorch JNI wrapper
│   │   ├── audio_processor.cpp     # Audio preprocessing
│   │   └── jni_bindings.cpp        # JNI registration
│   ├── assets/models/              # AI model files (.pte)
│   └── res/                        # Android resources
```

## 🚀 Getting Started

### Prerequisites

- Android Studio Flamingo or later
- Android SDK 29+
- NDK 25.1.8937393+
- ExecuTorch SDK (for AI models)
- Qualcomm QNN SDK (for hardware acceleration)

### Build Instructions

1. **Clone the repository**
   ```bash
   git clone https://github.com/GitHub-HackDay/2025-oct-20-hackathon-qualcomm.git
   cd 2025-oct-20-hackathon-qualcomm
   ```

2. **Open in Android Studio**
   - Open Android Studio
   - Select "Open an existing project"
   - Navigate to the project directory

3. **Configure NDK and ExecuTorch**
   ```bash
   # Download ExecuTorch (placeholder - actual setup needed)
   # git submodule update --init third_party/executorch
   ```

4. **Build the project**
   ```bash
   ./gradlew assembleDebug
   ```

### Running the App

1. Connect an Android device with API 29+ (preferably with Qualcomm chipset)
2. Enable Developer Options and USB Debugging
3. Run the app from Android Studio or use:
   ```bash
   ./gradlew installDebug
   ```

## 🤖 AI Models

### Planned Model Pipeline

1. **Speech-to-Text**: Whisper-tiny (~40MB)
2. **Text Processing**: Llama-3.2-1B quantized (~600MB)
3. **Text-to-Image**: Stable Diffusion Mobile (~1.5GB)

### Model Optimization

- INT8/INT4 quantization for size reduction
- Qualcomm HTP backend delegation
- ExecuTorch runtime optimization
- Memory-efficient inference

## 🎨 Design Principles

- **Child-Friendly**: Bright colors, simple interface, large touch targets
- **Accessibility**: High contrast, clear typography, intuitive navigation
- **Performance**: Smooth 60fps UI, fast model inference
- **Privacy**: On-device processing, no data collection

## 🧪 Current Status

This is a **scaffold/prototype** implementation with:

- ✅ Complete UI structure with Jetpack Compose
- ✅ Navigation between screens
- ✅ Basic audio recording permissions
- ✅ Placeholder AI model integration
- ✅ Clean architecture setup
- 🚧 Audio recording implementation (pending)
- 🚧 ExecuTorch model integration (pending)
- 🚧 Qualcomm hardware acceleration (pending)
- 🚧 Local database storage (pending)

## 🔮 Next Steps

1. **Audio Implementation**: Integrate MediaRecorder for voice capture
2. **ExecuTorch Setup**: Configure ExecuTorch runtime and model loading
3. **Model Integration**: Implement speech-to-text and image generation
4. **Database**: Set up Room database for story persistence
5. **Testing**: Add unit and integration tests
6. **Performance**: Optimize for target hardware

## 📱 Screenshots

*Screenshots will be added as the app development progresses*

## 🤝 Contributing

This is a hackathon project. For development:

1. Follow clean architecture principles
2. Use Jetpack Compose for UI
3. Write tests for business logic
4. Optimize for Qualcomm hardware when possible

## 📄 License

This project is part of the 2025 October Qualcomm Hackathon.

## 🔗 Related Documentation

- [Product Requirements Document](../PRODUCT_REQUIREMENT_DOCUMENT.md)
- [How-To Guide](../HOWTO.md)
- [ExecuTorch Documentation](https://pytorch.org/executorch/)
- [Qualcomm QNN SDK](https://www.qualcomm.com/products/technology/processors/qualcomm-neural-processing-sdk)