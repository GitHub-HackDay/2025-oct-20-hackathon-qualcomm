# TinyStories - Project Specification

## Overview

**TinyStories** is an Android mobile application that transforms children's audio dictations into illustrated story characters using on-device AI inference. The app leverages Qualcomm hardware acceleration and ExecuTorch for efficient, privacy-preserving AI processing directly on the device.

### Core Value Proposition

- **Privacy-First**: All AI processing happens on-device, no cloud dependencies
- **Child-Friendly**: Simple interface designed for kids aged 4-10
- **Creative Expression**: Turns spoken imagination into visual reality
- **Offline-Capable**: Works without internet connection

---

## Project Architecture

### System Overview

```
┌─────────────────────────────────────────────────────────────┐
│                    TinyStories Android App                   │
├─────────────────────────────────────────────────────────────┤
│                                                               │
│  [Audio Input] → [Speech-to-Text] → [Story Processing]      │
│                                    ↓                          │
│                          [Text-to-Image Generation]          │
│                                    ↓                          │
│                        [Character Display/Gallery]           │
│                                                               │
└─────────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────────┐
│              ExecuTorch Runtime (C++ Core)                   │
├─────────────────────────────────────────────────────────────┤
│  • Model Loading & Management                                │
│  • Inference Engine                                          │
│  • Memory Management                                         │
│  • Backend Delegation (Qualcomm HTP/DSP)                     │
└─────────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────────┐
│            Qualcomm Hardware Acceleration                    │
├─────────────────────────────────────────────────────────────┤
│  • Hexagon Tensor Processor (HTP)                            │
│  • Qualcomm AI Engine                                        │
│  • DSP Acceleration                                          │
└─────────────────────────────────────────────────────────────┘
```

### Technology Stack

#### Android Application Layer

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM with Clean Architecture
- **Minimum SDK**: Android 10 (API 29)
- **Target SDK**: Android 15 (API 35)

#### AI/ML Infrastructure

- **ML Framework**: ExecuTorch v0.7.0+
- **Backend**: PyTorch 2.8.0+
- **Hardware Acceleration**: Qualcomm QNN (Qualcomm Neural Network SDK)
- **Model Format**: .pte (PyTorch ExecuTorch)

#### Models Required

1. **Speech-to-Text**: Whisper (tiny/base) or Wav2Vec2
2. **Text Processing**: Llama-3.2-1B (Meta)
3. **Text-to-Image**: Stable Diffusion Mobile or ControlNet optimized
4. **Fallback**: Simpler sprite-based character generator

#### Native Layer

- **Language**: C++ (JNI bindings)
- **Build System**: CMake + Gradle NDK
- **ExecuTorch Runtime**: C++ library

---

## Development Phases

### Phase 0: Foundation & Setup 

**Goal**: Establish development environment and validate core technologies

#### Tasks

1. **Environment Setup**
   - Configure Android Studio with NDK
   - Set up ExecuTorch build system
   - Install Qualcomm QNN SDK
   - Configure device testing environment (physical Qualcomm device)

2. **Proof of Concept**
   - Create minimal Android app shell
   - Integrate ExecuTorch runtime
   - Load and run a simple test model
   - Validate Qualcomm backend delegation
   - Measure inference performance baseline

3. **Model Research & Selection**
   - Benchmark candidate models on target hardware
   - Evaluate model size vs accuracy tradeoffs
   - Select final models for each pipeline stage
   - Document model requirements and constraints

#### Deliverables

- ✅ Working Android project with ExecuTorch integration
- ✅ Performance baseline document
- ✅ Model selection decision matrix
- ✅ Development environment documentation

#### Success Criteria

- ExecuTorch successfully runs on Qualcomm hardware
- Simple model inference completes in <500ms
- Memory footprint stays under 2GB

---

### Phase 1: Audio Capture & Speech-to-Text

**Goal**: Implement audio recording and transcription pipeline

#### Components

##### 1.1 Audio Capture Module

```kotlin
// Features:
- Record audio from device microphone
- Support push-to-talk and continuous recording
- Audio preprocessing (noise reduction, normalization)
- WAV/PCM format handling
- Visual feedback during recording (waveform display)
```

**Technical Details**:

- Use Android `MediaRecorder` or `AudioRecord` API
- Sample rate: 16kHz (optimal for speech models)
- Encoding: 16-bit PCM
- Max recording duration: 60 seconds
- Buffer management for real-time processing

##### 1.2 Speech-to-Text Model Integration

**Model Options**:

- **Primary**: Whisper-tiny (39M params) or Whisper-base (74M params)
- **Alternative**: Wav2Vec2-base (95M params)
- **Fallback**: On-device Android Speech Recognition API

**Model Export Process**:

```python
# Model conversion pipeline:
1. Load pre-trained Whisper model from HuggingFace
2. Quantize to INT8 using PyTorch quantization
3. Export to ExecuTorch format (.pte)
4. Optimize for Qualcomm HTP backend
5. Validate accuracy vs size tradeoff
```

**Performance Targets**:

- Inference time: <3 seconds for 10-second audio clip
- Accuracy: >85% WER (Word Error Rate) for children's speech
- Model size: <100MB

##### 1.3 UI Components

- Recording screen with animated microphone icon
- Countdown timer (max 60s)
- Transcription preview text box
- Re-record and confirm buttons

#### Deliverables

- ✅ Audio capture functionality
- ✅ Speech-to-text model integrated and running
- ✅ Basic UI for recording and transcription
- ✅ Unit tests for audio processing

#### Success Criteria

- Clear audio recording with minimal noise
- Transcription accuracy >80% on test dataset
- End-to-end latency <5 seconds

---

### Phase 2: Story Processing & Understanding (Hour 3)

**Goal**: Process transcribed text and extract character descriptions

#### Components

##### 2.1 Text Processing Pipeline

```text
// Features:
- Clean and normalize transcribed text
- Identify character descriptions
- Extract key visual attributes (color, clothing, emotions, etc.)
- Generate structured prompt for image generation
```

**Processing Stages**:

1. **Text Cleaning**: Remove filler words, fix grammar
2. **Entity Extraction**: Identify character mentions
3. **Attribute Parsing**: Extract visual descriptors
4. **Prompt Engineering**: Create image generation prompt

##### 2.2 Language Model Integration

**Primary Model: Llama-3.2-1B (Meta)**

**Model Specifications**:
- **Parameters**: 1.23 billion (1.23B)
- **Architecture**: Auto-regressive transformer with optimized architecture for edge devices
- **Context Length**: 128k tokens (extremely large context window)
  - *Note: For mobile deployment, we'll limit to 2k-4k tokens to reduce memory*
- **Training Tokens**: 9 trillion tokens (massive training data)
- **Data Cutoff**: December 2023
- **Multilingual Support**: 8 languages (EN, DE, FR, IT, PT, HI, ES, TH)
  - *Useful for international expansion*

**Mobile Optimization Features**:
- **Quantization Options**:
  - **4-bit groupwise quantization** (group size 32) for weights → 54.1% size reduction
  - **8-bit dynamic quantization** for activations
  - **SpinQuant support**: 2.6x faster decoding speed with minimal accuracy loss
  - **QLoRA**: Low-rank adaptation for efficient fine-tuning
- **Designed for Mobile**: Explicitly mentioned as suitable for "mobile AI powered writing assistants"
- **Constrained Environments**: Optimized for on-device inference with limited compute

**Model Size After Quantization**:
- **Original (FP32)**: ~4.9GB
- **INT8**: ~1.2GB
- **INT4 (with SpinQuant)**: ~600-800MB ✅ *Target for TinyStories*

**Performance Benchmarks** (from HuggingFace):
- **MMLU (Multilingual)**: 32.2-58% across languages
- **Inference Speed**: 2.6x faster with SpinQuant optimization
- **Training Infrastructure**: Meta H100-80GB cluster (370k GPU hours)

**Licensing**:
- **License**: Llama 3.2 Community License
- **Commercial Use**: ✅ Permitted
- **Research Use**: ✅ Permitted
- **Requirements**: Must comply with Meta's Acceptable Use Policy
- *Good for TinyStories: Free commercial use for mobile app*

**Use Cases for TinyStories**:
1. **Prompt Enhancement**:
   - Input: "a princess"
   - Output: "a princess with long blonde hair, wearing a sparkly blue dress, holding a magic wand"
2. **Grammar Correction**: Fix child speech transcription errors
3. **Character Extraction**: Parse complex sentences to identify characters and attributes
4. **Story Context**: Use large context window to maintain consistency across multiple prompts
5. **Multilingual**: Support for non-English speaking children (future feature)

**Performance Targets for Mobile**:
- Inference time: <2 seconds for 512 tokens
- Model size: ~600-800MB (INT4 quantized)
- Memory usage: <1GB during inference
- Context window: 2048 tokens (practical limit for mobile)

**Alternative Fallback**:
- **Rule-based keyword extraction** if model is too large or slow

**Key Advantages for TinyStories**:
- ✅ **Officially designed for mobile deployment**
- ✅ **Massive 128k context window** (can process long stories if needed)
- ✅ **Advanced quantization support** (SpinQuant, QLoRA)
- ✅ **Multilingual capabilities** (future-proof for global expansion)
- ✅ **Permissive licensing** (free commercial use)
- ✅ **State-of-the-art training** (9T tokens, modern architecture)

##### 2.3 Prompt Generation
**Prompt Template**:
```
"A simple, colorful, child-friendly illustration of [CHARACTER],
[ATTRIBUTES], [STYLE_MODIFIERS], white background,
cartoon style, suitable for children's book"
```

**Style Modifiers**:
- Cartoon/anime style
- Bright colors
- Simple shapes
- Friendly appearance

#### Deliverables
- ✅ Text processing utilities
- ✅ Prompt generation system
- ✅ (Optional) LLM integration
- ✅ Test suite with sample transcriptions

#### Success Criteria
- Generated prompts accurately reflect input descriptions
- Processing time <3 seconds
- Prompts produce consistent, child-appropriate images

---

### Phase 3: Text-to-Image Generation (Week 4-5)
**Goal**: Convert text descriptions into character images

#### Components

##### 3.1 Image Generation Model Selection

**Option A: Stable Diffusion Mobile**
- **Model**: Stable Diffusion 1.5 or 2.1 (distilled)
- **Size**: ~1.5-2GB quantized
- **Pros**: High quality, flexible
- **Cons**: Large size, slower inference
- **Inference Time**: 10-30 seconds per image

**Option B: ControlNet Lite**
- **Model**: ControlNet with edge/sketch guidance
- **Size**: ~500MB-1GB
- **Pros**: Better control, faster
- **Cons**: Requires base model still
- **Inference Time**: 5-15 seconds per image

**Option C: Sprite Generator (Fallback)**
- **Approach**: Combine pre-rendered assets
- **Size**: 50-100MB asset library
- **Pros**: Fast, predictable, small
- **Cons**: Limited creativity
- **Inference Time**: <1 second

**Recommended Approach**:
Start with **Option C** (sprint/hackathon), plan for **Option A/B** (production)

##### 3.2 Model Optimization for Mobile
```python
# Optimization pipeline:
1. Use distilled Stable Diffusion variant
2. Reduce inference steps (50 → 10-20)
3. Lower resolution (512x512 → 256x256 or 384x384)
4. Quantize weights to INT8/INT4
5. Optimize attention mechanism
6. Enable Qualcomm HTP delegation
7. Profile and optimize memory usage
```

**Performance Targets**:
- Image generation time: <15 seconds
- Image resolution: 384x384 minimum
- Model size: <2GB total
- Memory usage: <1.5GB during inference

##### 3.3 Generation UI
- Loading screen with progress indicator
- "Generating your character..." message
- Cancel button for long-running tasks
- Preview generation (low-res fast preview)

#### Deliverables
- ✅ Text-to-image model integrated
- ✅ Model optimization for Qualcomm hardware
- ✅ Generation UI with progress feedback
- ✅ Error handling and fallback mechanisms

#### Success Criteria
- Images match text descriptions (subjective evaluation)
- Generation time acceptable (<30s)
- Images are child-appropriate (content safety)
- App remains responsive during generation

---

### Phase 4: Character Display & Gallery (Week 6)
**Goal**: Display generated characters and manage user's collection

#### Components

##### 4.1 Image Display
- Full-screen character viewer
- Pinch-to-zoom functionality
- Share button (save to gallery, share with parents)
- Regenerate button (try again with same prompt)

##### 4.2 Story Gallery
**Features**:
- Grid view of all generated characters
- Thumbnail generation and caching
- Sort by date created
- Search by description/transcript
- Delete unwanted images

**Data Storage**:
```kotlin
// Local database schema:
Story {
    id: String
    audioPath: String
    transcription: String
    prompt: String
    imagePath: String
    timestamp: Long
    isFavorite: Boolean
}
```

##### 4.3 Character Metadata
- Display original audio transcription
- Show generated prompt
- Playback original audio recording
- Edit/regenerate options

#### Deliverables
- ✅ Character display screen
- ✅ Gallery view with grid layout
- ✅ Local storage implementation
- ✅ Share functionality

#### Success Criteria
- Smooth scrolling in gallery (60fps)
- Fast thumbnail loading (<100ms)
- Reliable storage and retrieval

---

### Phase 5: Polish & Optimization (Week 7)
**Goal**: Enhance UX, optimize performance, and prepare for launch

#### Tasks

##### 5.1 Performance Optimization
- Profile and optimize critical paths
- Reduce app launch time
- Minimize memory footprint
- Improve model loading time
- Implement caching strategies
- Background processing for non-blocking UI

##### 5.2 UX Enhancements
- Add onboarding tutorial
- Implement haptic feedback
- Add sound effects
- Smooth transitions and animations
- Error messaging improvements
- Loading state refinements

##### 5.3 Child Safety Features
- Content filtering for generated images
- Parental controls (if applicable)
- Data privacy settings
- Offline mode indicator

##### 5.4 Testing & QA
- End-to-end testing on multiple devices
- Edge case handling (very long/short audio)
- Memory leak detection
- Crash analytics integration
- Performance regression testing

##### 5.5 Documentation
- User guide
- Technical documentation
- API documentation
- Model deployment guide

#### Deliverables
- ✅ Optimized app performance
- ✅ Polished UI/UX
- ✅ Complete test coverage
- ✅ Documentation package
- ✅ Release candidate build

#### Success Criteria
- App starts in <2 seconds
- No crashes during normal use
- Positive feedback from test users (kids + parents)
- All acceptance criteria met

---

## Technical Challenges & Mitigations

### Challenge 1: Model Size vs Performance
**Issue**: Large models provide better quality but are slow on mobile

**Mitigation**:
- Use quantization (INT8/INT4) to reduce size
- Implement progressive loading (show low-res preview first)
- Offer quality settings (fast/balanced/quality modes)
- Use model distillation techniques

### Challenge 2: Generation Time
**Issue**: Image generation may take 20-30+ seconds

**Mitigation**:
- Show entertaining loading animations
- Display progress indicators
- Allow cancellation
- Implement background processing
- Consider cloud generation for premium features (future)

### Challenge 3: Child Speech Recognition
**Issue**: Kids speak differently than adults (mumbling, mispronunciation)

**Mitigation**:
- Fine-tune STT model on children's speech dataset
- Implement audio preprocessing (noise reduction)
- Allow manual transcription editing
- Use language model to fix grammar

### Challenge 4: Content Safety
**Issue**: Generated images must be appropriate for children

**Mitigation**:
- Use models fine-tuned for child-safe content
- Implement prompt filtering (block inappropriate keywords)
- Add post-generation content filtering
- Manual review during testing phase
- Parental preview before showing to child

### Challenge 5: Device Compatibility
**Issue**: Not all Android devices have Qualcomm chips

**Mitigation**:
- Implement CPU fallback for non-Qualcomm devices
- Runtime hardware detection
- Graceful degradation (lower quality/slower)
- Clear device requirements in app store listing

---

## Success Metrics

### Technical Metrics
- **App Launch Time**: <2 seconds
- **Audio Recording**: Smooth capture, no dropped frames
- **STT Latency**: <5 seconds for 30-second clip
- **Image Generation**: <30 seconds per image
- **Memory Usage**: <2GB peak
- **Crash Rate**: <1% of sessions
- **Battery Impact**: <5% per generation

### User Experience Metrics
- **Task Completion**: >90% of sessions result in generated image
- **User Satisfaction**: 4+ star rating
- **Session Duration**: 2-5 minutes average
- **Return Rate**: >40% weekly active users
- **Gallery Size**: Average 10+ saved characters per user

### Quality Metrics
- **STT Accuracy**: >85% WER
- **Image Quality**: Subjective evaluation by test users
- **Character Match**: >80% of images match description
- **Content Safety**: 100% appropriate for children

---

## Development Timeline

```
Week 1: Phase 0 - Foundation & Setup
Week 2: Phase 1 - Audio & Speech-to-Text
Week 3: Phase 2 - Story Processing
Week 4-5: Phase 3 - Text-to-Image Generation
Week 6: Phase 4 - Gallery & Display
Week 7: Phase 5 - Polish & Optimization

Total: 7 weeks to MVP
```

### Minimum Viable Product (MVP) Scope
For a hackathon or initial release, focus on:
1. ✅ Audio recording (30s max)
2. ✅ Speech-to-text (any working model)
3. ✅ Simple prompt generation (rule-based)
4. ✅ Sprite-based character generator (fallback option)
5. ✅ Basic display and save functionality

**MVP Timeline**: 2-3 days (hackathon mode)

---

## File Structure

```
TinyStories/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/tinystories/
│   │   │   │   ├── ui/
│   │   │   │   │   ├── recording/      # Audio capture screens
│   │   │   │   │   ├── generation/     # Image generation screens
│   │   │   │   │   ├── gallery/        # Gallery screens
│   │   │   │   │   └── common/         # Shared UI components
│   │   │   │   ├── domain/
│   │   │   │   │   ├── model/          # Data models
│   │   │   │   │   ├── usecase/        # Business logic
│   │   │   │   │   └── repository/     # Data abstraction
│   │   │   │   ├── data/
│   │   │   │   │   ├── local/          # Local storage
│   │   │   │   │   ├── ml/             # ML model integration
│   │   │   │   │   └── audio/          # Audio processing
│   │   │   │   ├── di/                 # Dependency injection
│   │   │   │   └── util/               # Utilities
│   │   │   ├── cpp/
│   │   │   │   ├── executorch_wrapper.cpp
│   │   │   │   ├── audio_processor.cpp
│   │   │   │   └── jni_bindings.cpp
│   │   │   ├── assets/
│   │   │   │   └── models/
│   │   │   │       ├── whisper_tiny.pte         # ~40MB (STT)
│   │   │   │       ├── llama32_1b_int4_qnn.pte  # ~600MB (text processing)
│   │   │   │       ├── sd_mobile.pte            # ~1.5GB (image gen)
│   │   │   │       └── sprites/                 # Fallback assets
│   │   │   └── res/                    # Android resources
│   │   └── test/                       # Unit tests
│   └── build.gradle.kts
├── scripts/
│   ├── export_models.py                # Model conversion scripts
│   ├── optimize_for_mobile.py
│   └── benchmark_models.py
├── models/                             # Model training/export
│   ├── whisper/
│   │   └── export_whisper.py
│   ├── llama/
│   │   ├── export_llama32.py         # Export script for Llama-3.2-1B
│   │   ├── quantize_spinquant.py      # SpinQuant quantization
│   │   └── benchmark_llama.py         # Performance testing
│   ├── stable_diffusion/
│   └── sprites/
├── docs/
│   ├── API.md
│   ├── DEPLOYMENT.md
│   └── USER_GUIDE.md
├── third_party/
│   └── executorch/                     # Git submodule
└── README.md
```

---

## Next Steps

1. **Review this specification** with team and stakeholders
2. **Set up development environment** (Phase 0)
3. **Procure test device** with Qualcomm chipset
4. **Download and prepare models** for export
5. **Create project backlog** in issue tracker
6. **Begin Phase 0 tasks**

---

## Appendix

### Recommended Qualcomm Devices for Testing
- Snapdragon 8 Gen 2/3 devices (flagship)
- Snapdragon 7+ Gen 2 devices (mid-range)
- Any device with Hexagon DSP support

### Resources
- [ExecuTorch Documentation](https://pytorch.org/executorch/)
- [Qualcomm QNN SDK](https://www.qualcomm.com/products/technology/processors/qualcomm-neural-processing-sdk)
- [Whisper Model](https://github.com/openai/whisper)
- [Llama-3.2-1B Model](https://huggingface.co/meta-llama/Llama-3.2-1B)
- [Stable Diffusion Mobile](https://github.com/huggingface/diffusers)

### Llama-3.2-1B Model Export Guide

**Step 1: Download Model from HuggingFace**
```bash
# Requires HuggingFace account and accepted Llama license
huggingface-cli login
huggingface-cli download meta-llama/Llama-3.2-1B
```

**Step 2: Export to ExecuTorch Format**
```python
import torch
from transformers import AutoModelForCausalLM, AutoTokenizer

# Load model
model = AutoModelForCausalLM.from_pretrained(
    "meta-llama/Llama-3.2-1B",
    torch_dtype=torch.float32
)
tokenizer = AutoTokenizer.from_pretrained("meta-llama/Llama-3.2-1B")

# Apply quantization (INT4 with SpinQuant)
from torchao.quantization import quantize_
quantize_(model, int4_weight_only())

# Export to ExecuTorch
from executorch.exir import to_edge
from torch.export import export

# Trace model
example_input = torch.randint(0, 32000, (1, 512))
exported_program = export(model, (example_input,))

# Convert to ExecuTorch edge dialect
edge_program = to_edge(exported_program)

# Save as .pte file
edge_program.to_executorch().save("llama32_1b_int4.pte")
```

**Step 3: Optimize for Qualcomm Backend**
```python
from executorch.backends.qualcomm.partition.qnn_partitioner import QnnPartitioner

# Partition for Qualcomm HTP
edge_program_qnn = edge_program.to_backend(QnnPartitioner)

# Save optimized model
edge_program_qnn.save("llama32_1b_int4_qnn.pte")
```

**Expected Model Sizes**:
- `llama32_1b_int4.pte`: ~800MB (CPU/GPU fallback)
- `llama32_1b_int4_qnn.pte`: ~600MB (Qualcomm HTP optimized)

**Key Export Considerations**:
1. **Context Length**: Reduce from 128k to 2048 tokens for mobile memory constraints
2. **Quantization**: Use 4-bit groupwise (group=32) for best size/accuracy tradeoff
3. **KV Cache**: Implement efficient key-value cache management
4. **Dynamic Shapes**: Support variable sequence lengths (16-2048 tokens)
5. **Batching**: Use batch_size=1 for mobile (no batching needed)

**Model Limitations** (from HuggingFace):
- May produce inaccurate or biased responses
- Requires application-specific safety testing
- Not designed for standalone deployment (needs guardrails)
- **Content Safety**: Important for TinyStories - add output filtering for child-appropriate content

**Recommended System Prompts for TinyStories**:
```
System: You are a creative children's story assistant. Your task is to enhance
character descriptions to be vivid, colorful, and appropriate for children aged 4-10.
Keep descriptions simple, positive, and imaginative. Use bright colors, friendly
features, and magical elements when appropriate.

User: a princess
Assistant: a princess with long golden hair, wearing a sparkly blue gown with
silver stars, holding a glowing magic wand, friendly smile
```

### Dependencies
See `pyproject.toml` for Python dependencies
See `app/build.gradle.kts` for Android dependencies (TBD)

---

**Document Version**: 1.1
**Last Updated**: 2025-10-20
**Status**: Draft for Review
**Changelog**:
- v1.1: Updated text processing model to Llama-3.2-1B with detailed specifications
- v1.0: Initial specification document
