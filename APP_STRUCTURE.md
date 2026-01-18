# App Folder Structure - Detailed Guide for Kotlin Beginners

## Table of Contents
1. [Overview](#overview)
2. [Project Structure](#project-structure)
3. [Build Configuration](#build-configuration)
4. [Main Application Files](#main-application-files)
5. [Package Structure](#package-structure)
6. [Detailed Component Breakdown](#detailed-component-breakdown)
7. [How Everything Works Together](#how-everything-works-together)
8. [Key Kotlin & Android Concepts](#key-kotlin--android-concepts)

---

## Overview

The `app` folder is the **main module** of this Android application. In Android development, this is where all your application code, resources, and configuration files live.

### What This App Does
Pokemon Vizor is an Android camera app that:
- Uses the device camera to capture live video
- Runs machine learning (ML) inference to recognize animals
- Maps recognized animals to fictional Pokemon characters
- Renders a 3D procedural avatar of the Pokemon using OpenGL ES

---

## Project Structure

```
app/
├── build.gradle              # Build configuration for the app module
├── proguard-rules.pro       # Code obfuscation rules (for release builds)
└── src/
    ├── main/                # Main source code and resources
    │   ├── AndroidManifest.xml
    │   ├── java/com/example/pokemonvizor/
    │   └── res/             # Android resources (layouts, themes, etc.)
    └── test/                # Unit tests
        └── java/com/example/pokemonvizor/
```

---

## Build Configuration

### `app/build.gradle`

This file tells Gradle (Android's build system) how to build your app. Key sections:

**Plugins:**
```kotlin
plugins {
    id "com.android.application"           // Android app plugin
    id "org.jetbrains.kotlin.android"      // Kotlin support
    id "com.google.dagger.hilt.android"    // Dependency injection
    id "kotlin-kapt"                        // Kotlin Annotation Processing
}
```

**Android Configuration:**
```kotlin
android {
    namespace "com.example.pokemonvizor"  // Package name
    compileSdk 34                          // Android SDK version to compile against
    minSdk 26                              // Minimum Android version (Android 8.0)
    targetSdk 34                           // Target Android version
}
```

**Important Features Enabled:**
- **Jetpack Compose**: Modern UI toolkit (`buildFeatures { compose true }`)
- **Kotlin**: Modern Android development language
- **Hilt**: Dependency injection framework

**Key Dependencies:**
- **CameraX**: Modern camera API
- **TensorFlow Lite**: On-device machine learning
- **Jetpack Compose**: Declarative UI framework
- **Hilt**: Dependency injection
- **Coroutines**: Asynchronous programming

---

## Main Application Files

### `AndroidManifest.xml`
The manifest declares essential information about your app to the Android system:

```xml
<manifest>
    <!-- Permissions -->
    <uses-permission android:name="android.permission.CAMERA" />
    
    <application
        android:name=".PokemonVizorApp"    <!-- Custom Application class -->
        android:label="Pokemon Vizor">      <!-- App name shown to users -->
        
        <activity android:name=".MainActivity" android:exported="true">
            <!-- This activity launches when you tap the app icon -->
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>
</manifest>
```

**Key Points:**
- Requests CAMERA permission (user must grant this)
- Sets `PokemonVizorApp` as the custom Application class
- Declares `MainActivity` as the entry point (launcher activity)

---

## Package Structure

Inside `app/src/main/java/com/example/pokemonvizor/`, the code is organized into packages:

```
pokemonvizor/
├── MainActivity.kt           # Entry point - launches the app
├── PokemonVizorApp.kt       # Application class - initializes app-wide components
├── camera/                   # Camera functionality
│   ├── CameraController.kt   # Manages CameraX lifecycle
│   └── FrameAnalyzer.kt      # Analyzes camera frames
├── data/                     # Data layer - local data sources
│   └── LocalPokemonRepository.kt  # Hardcoded animal/Pokemon data
├── di/                       # Dependency Injection configuration
│   └── AppModule.kt          # Hilt module - provides dependencies
├── domain/                   # Domain layer - core business models
│   ├── Models.kt             # Data classes (AnimalInfo, PokemonEntry, etc.)
│   └── PokemonRepository.kt  # Repository interface
├── ml/                       # Machine Learning
│   ├── MLInferenceService.kt     # ML inference interface
│   └── TfliteAnimalClassifier.kt # TensorFlow Lite implementation
├── render3d/                 # 3D Rendering with OpenGL ES
│   ├── PokemonRenderer.kt    # OpenGL renderer
│   ├── PokemonGlSurfaceView.kt
│   ├── Mesh.kt               # 3D mesh data structures
│   ├── PrimitiveModelBuilder.kt  # Builds 3D models from primitives
│   ├── ShaderUtils.kt        # OpenGL shader compilation
│   └── TransformUtils.kt     # 3D transformations
└── ui/                       # UI layer - screens and ViewModels
    ├── CameraScreen.kt       # Main camera screen (Compose UI)
    ├── CameraUiState.kt      # UI state definition
    ├── CameraViewModel.kt    # Business logic for UI
    └── LocalPokemonRenderer.kt  # Composition local for renderer
```

---

## Detailed Component Breakdown

### 1. **Entry Points**

#### `PokemonVizorApp.kt`
```kotlin
@HiltAndroidApp
class PokemonVizorApp : Application()
```

**What it does:**
- This is the **Application class** - created before any activity starts
- Lives for the entire lifetime of your app process
- `@HiltAndroidApp` annotation initializes Hilt dependency injection

**Key Concepts:**
- **Application class**: A singleton that represents your entire app
- **@HiltAndroidApp**: Triggers Hilt's code generation for dependency injection

---

#### `MainActivity.kt`
```kotlin
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: CameraViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                val pokemonRenderer = remember { PokemonGlSurfaceView(this) }
                CompositionLocalProvider(LocalPokemonRenderer provides pokemonRenderer) {
                    CameraScreen(viewModel = viewModel)
                }
            }
        }
    }
}
```

**What it does:**
- The **main activity** - the first screen users see
- Sets up the UI using Jetpack Compose
- Injects `CameraViewModel` using Hilt
- Creates and provides the 3D renderer

**Key Concepts:**
- **ComponentActivity**: Modern base class for activities
- **@AndroidEntryPoint**: Enables Hilt dependency injection in this activity
- **setContent { }**: Compose way to define UI (replaces XML layouts)
- **remember { }**: Keeps an object alive across recompositions
- **CompositionLocalProvider**: Makes renderer accessible throughout UI tree
- **by viewModels()**: Kotlin property delegate for ViewModel creation

---

### 2. **UI Layer** (`ui/` package)

This follows the **MVVM (Model-View-ViewModel)** architecture pattern.

#### `CameraViewModel.kt`
```kotlin
@HiltViewModel
class CameraViewModel @Inject constructor(
    private val repository: PokemonRepository,
    private val mlService: MLInferenceService
) : ViewModel() {
    private val _uiState = MutableStateFlow<CameraUiState>(...)
    val uiState: StateFlow<CameraUiState> = _uiState.asStateFlow()
    
    fun onResult(result: RecognitionResult) {
        viewModelScope.launch {
            val info = repository.getAnimalInfo(result.animalId)
            val pokemon = repository.getPokemonEntry(result.animalId)
            val spec = pokemon?.let { repository.getPokemonSpec(it.pokemonId) }
            // Update UI state
        }
    }
}
```

**What it does:**
- Holds and manages UI-related data
- Survives configuration changes (screen rotations)
- Processes ML results and fetches related data
- Exposes UI state via `StateFlow`

**Key Concepts:**
- **ViewModel**: Survives configuration changes, outlives UI
- **@HiltViewModel**: Enables Hilt to inject dependencies
- **@Inject constructor**: Hilt injects these dependencies automatically
- **StateFlow**: Observable state holder (like LiveData but better)
- **MutableStateFlow**: Internal mutable state
- **viewModelScope**: Coroutine scope tied to ViewModel lifecycle
- **launch { }**: Starts a coroutine (asynchronous operation)

---

#### `CameraUiState.kt`
```kotlin
sealed interface CameraUiState {
    data class Ready(
        val isRealtime: Boolean,
        val selectedAnimal: AnimalInfo?,
        val selectedPokemon: PokemonEntry?,
        val selectedSpec: PokemonPrimitiveModelSpec?,
        val confidence: Float,
        val inferenceMs: Long,
        val fps: Float,
        val lastFrame: Long
    ) : CameraUiState
    
    data class Error(val message: String) : CameraUiState
}
```

**What it does:**
- Defines all possible states of the camera screen
- Uses **sealed interface** for type-safe state management

**Key Concepts:**
- **sealed interface**: Restricted class hierarchy (only these states exist)
- **data class**: Class that holds data with auto-generated equals/hashCode/toString
- **Type-safe state**: Compiler ensures you handle all possible states

---

#### `CameraScreen.kt`
```kotlin
@Composable
fun CameraScreen(viewModel: CameraViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val renderer = LocalPokemonRenderer.current
    
    // Camera permission handling
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> hasPermission = granted }
    
    // Camera setup
    val analyzer = remember {
        FrameAnalyzer(
            mlService = viewModel.mlService(),
            shouldAnalyze = { viewModel.shouldAnalyzeFrame() },
            onResult = { result, _ -> viewModel.onResult(result) }
        )
    }
    
    Box(modifier = Modifier.fillMaxSize()) {
        // Camera preview
        AndroidView(factory = { PreviewView(it).apply { ... } })
        
        // UI overlays (cards, buttons, 3D view)
        when (val state = uiState) {
            is CameraUiState.Ready -> { /* Show UI */ }
            is CameraUiState.Error -> { /* Show error */ }
        }
    }
}
```

**What it does:**
- Defines the camera screen UI using Compose
- Handles camera permissions
- Sets up camera preview and frame analysis
- Displays recognition results and 3D Pokemon

**Key Concepts:**
- **@Composable**: Function that describes UI (pure, can be called many times)
- **by collectAsStateWithLifecycle()**: Observes StateFlow and updates UI
- **remember { }**: Caches computation across recompositions
- **AndroidView { }**: Embeds traditional Android View in Compose
- **when expression**: Kotlin's powerful switch statement
- **Modifier**: Describes how to layout/draw/behave (like CSS)

---

### 3. **Camera Layer** (`camera/` package)

#### `CameraController.kt`
```kotlin
class CameraController(
    private val context: Context,
    private val analyzer: FrameAnalyzer
) {
    private var cameraExecutor: ExecutorService? = null
    
    fun startCamera(previewView: PreviewView, lifecycleOwner: LifecycleOwner) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build()
            val analysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
            
            cameraExecutor = Executors.newSingleThreadExecutor()
            analysis.setAnalyzer(cameraExecutor!!, analyzer)
            
            cameraProvider.bindToLifecycle(lifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, preview, analysis)
        }, ContextCompat.getMainExecutor(context))
    }
}
```

**What it does:**
- Manages CameraX lifecycle
- Sets up camera preview (what you see on screen)
- Sets up image analysis (sends frames to ML)
- Uses back camera by default

**Key Concepts:**
- **CameraX**: Modern camera API (lifecycle-aware, easier than Camera2)
- **ListenableFuture**: Asynchronous result (completed in future)
- **ExecutorService**: Thread pool for background work
- **Lifecycle-aware**: Automatically handles app lifecycle (pause/resume)
- **Preview**: Use case for showing camera feed
- **ImageAnalysis**: Use case for analyzing frames

---

#### `FrameAnalyzer.kt`
```kotlin
class FrameAnalyzer(
    private val mlService: MLInferenceService,
    private val shouldAnalyze: () -> Boolean,
    private val onResult: (RecognitionResult, Bitmap) -> Unit
) : ImageAnalysis.Analyzer {
    override fun analyze(imageProxy: ImageProxy) {
        if (shouldAnalyze()) {
            // Convert ImageProxy to Bitmap
            // Run ML inference
            // Report result
        }
        imageProxy.close()
    }
}
```

**What it does:**
- Receives camera frames from CameraX
- Converts frames to Bitmap format
- Calls ML service to analyze the image
- Reports results back to ViewModel

**Key Concepts:**
- **ImageAnalysis.Analyzer**: Interface for frame-by-frame analysis
- **ImageProxy**: Wrapper around camera frame (must be closed!)
- **Callback pattern**: `onResult` is called when analysis completes

---

### 4. **Domain Layer** (`domain/` package)

This layer contains **business logic** and **data models** (no Android dependencies).

#### `Models.kt`
```kotlin
data class AnimalInfo(
    val id: String,
    val displayName: String,
    val description: String,
    val funFacts: List<String>,
    val wikipediaUrl: String? = null
)

data class PokemonEntry(
    val animalId: String,
    val pokemonId: String,
    val pokemonName: String,
    val pokemonDescription: String
)

data class Primitive(
    val id: String,
    val type: PrimitiveType,
    val size: Vector3,
    val position: Vector3,
    val rotation: Vector3,
    val color: ArgbColor,
    val parentId: String? = null
)

enum class PrimitiveType { BOX, SPHERE, CYLINDER, CONE }

data class Vector3(val x: Float, val y: Float, val z: Float)
```

**What it does:**
- Defines core data structures
- `AnimalInfo`: Real animal information
- `PokemonEntry`: Maps animal to fictional Pokemon
- `Primitive`: 3D shape specification (box, sphere, etc.)
- `Vector3`: 3D coordinate or size
- `ArgbColor`: Color with alpha (transparency)

**Key Concepts:**
- **data class**: Automatically generates equals, hashCode, toString, copy
- **enum class**: Fixed set of constants
- **init block**: Validation logic (e.g., color values 0-1)
- **Default parameters**: `parentId: String? = null`

---

#### `PokemonRepository.kt`
```kotlin
interface PokemonRepository {
    suspend fun getAnimalInfo(animalId: String): AnimalInfo?
    suspend fun getPokemonEntry(animalId: String): PokemonEntry?
    suspend fun getPokemonSpec(pokemonId: String): PokemonPrimitiveModelSpec?
}
```

**What it does:**
- Defines contract (interface) for data access
- **Repository pattern**: Abstracts data source (could be local, network, database)

**Key Concepts:**
- **interface**: Contract without implementation
- **suspend**: Kotlin coroutine function (can be paused/resumed)
- **Nullable return**: `?` means might return null

---

### 5. **Data Layer** (`data/` package)

#### `LocalPokemonRepository.kt`
```kotlin
@Singleton
class LocalPokemonRepository @Inject constructor() : PokemonRepository {
    private val animals = listOf(
        AnimalInfo(id = "deer", displayName = "Deer", ...),
        AnimalInfo(id = "goat", displayName = "Goat", ...),
        // More animals...
    )
    
    private val pokemonEntries = listOf(
        PokemonEntry("deer", "springle", "Springle", "A nimble forest sprite..."),
        // More mappings...
    )
    
    private val pokemonSpecs = listOf(
        PokemonPrimitiveModelSpec(
            pokemonId = "springle",
            primitives = listOf(
                Primitive("body", PrimitiveType.BOX, ...),
                Primitive("head", PrimitiveType.SPHERE, ...),
                // More primitives...
            )
        ),
        // More specs...
    )
    
    override suspend fun getAnimalInfo(animalId: String): AnimalInfo? {
        return animals.find { it.id == animalId }
    }
}
```

**What it does:**
- **Implementation** of PokemonRepository
- Stores hardcoded data (animals, Pokemon mappings, 3D specs)
- Provides lookup methods

**Key Concepts:**
- **@Singleton**: Only one instance exists app-wide (Hilt scope)
- **@Inject constructor**: Hilt can create this class
- **override**: Implements interface method
- **find { }**: Kotlin collection function (returns first match or null)
- **it**: Implicit lambda parameter name

---

### 6. **ML Layer** (`ml/` package)

#### `MLInferenceService.kt`
```kotlin
interface MLInferenceService {
    suspend fun analyzeFrame(bitmap: Bitmap): RecognitionResult?
    fun close()
}
```

**What it does:**
- Interface for ML inference
- `analyzeFrame`: Analyzes an image, returns recognized animal + confidence
- `close`: Cleanup resources

---

#### `TfliteAnimalClassifier.kt`
```kotlin
class TfliteAnimalClassifier(
    context: Context,
    private val repository: PokemonRepository
) : MLInferenceService {
    private var interpreter: Interpreter? = null
    
    init {
        try {
            val modelFile = loadModelFile(context)
            interpreter = Interpreter(modelFile)
        } catch (e: Exception) {
            // Model not found - will return dummy results
        }
    }
    
    override suspend fun analyzeFrame(bitmap: Bitmap): RecognitionResult? {
        return if (interpreter != null) {
            // Real TensorFlow Lite inference
        } else {
            // Dummy deterministic results
        }
    }
}
```

**What it does:**
- Loads TensorFlow Lite model from assets
- Runs on-device ML inference
- Falls back to dummy results if model missing
- Preprocesses images (resize, normalize)
- Postprocesses outputs (find max confidence)

**Key Concepts:**
- **TensorFlow Lite**: Optimized ML for mobile devices
- **Interpreter**: TFLite inference engine
- **try-catch**: Exception handling
- **init block**: Runs when object is created
- **Fallback pattern**: Graceful degradation if model missing

---

### 7. **3D Rendering Layer** (`render3d/` package)

#### `PokemonRenderer.kt`
```kotlin
class PokemonRenderer : GLSurfaceView.Renderer {
    private val modelSpec = AtomicReference<PokemonPrimitiveModelSpec?>(null)
    private var meshInstances: List<MeshInstance> = emptyList()
    private val projection = FloatArray(16)
    private val view = FloatArray(16)
    
    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(0.08f, 0.08f, 0.1f, 1f)
        programId = ShaderUtils.createProgram(VERTEX_SHADER, FRAGMENT_SHADER)
        // Get shader attribute/uniform locations
    }
    
    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        Matrix.perspectiveM(projection, 0, 45f, ratio, 0.1f, 50f)
        Matrix.setLookAtM(view, 0, 0f, 1.2f, 4f, 0f, 0f, 0f, 0f, 1f, 0f)
    }
    
    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(...)
        if (needsRebuild) {
            meshInstances = modelBuilder.build(modelSpec.get())
        }
        // Draw each mesh with transformations
    }
}
```

**What it does:**
- Renders 3D Pokemon using OpenGL ES
- Builds meshes from primitive specifications
- Handles 3D transformations (rotation, perspective)
- Uses shaders (GPU programs) to render

**Key Concepts:**
- **OpenGL ES**: 3D graphics API for mobile
- **GLSurfaceView.Renderer**: Interface for OpenGL rendering
- **Shader**: GPU program (runs on graphics card)
- **Matrix**: 4x4 matrix for 3D transformations
- **Perspective projection**: Creates 3D depth effect
- **AtomicReference**: Thread-safe reference (GL thread vs UI thread)

---

#### `PrimitiveModelBuilder.kt`
```kotlin
class PrimitiveModelBuilder {
    fun build(spec: PokemonPrimitiveModelSpec?): List<MeshInstance> {
        if (spec == null) return emptyList()
        
        return spec.primitives.map { primitive ->
            val mesh = when (primitive.type) {
                PrimitiveType.BOX -> buildBox(primitive)
                PrimitiveType.SPHERE -> buildSphere(primitive)
                PrimitiveType.CYLINDER -> buildCylinder(primitive)
                PrimitiveType.CONE -> buildCone(primitive)
            }
            MeshInstance(mesh, computeModelMatrix(primitive))
        }
    }
    
    private fun buildSphere(primitive: Primitive): Mesh {
        // Generate vertices for sphere
        // Generate indices for triangles
        // Create Mesh object
    }
}
```

**What it does:**
- Converts primitive specs (box, sphere, etc.) to actual 3D meshes
- Generates vertices (corner points)
- Generates indices (which vertices form triangles)
- Applies colors and transformations

**Key Concepts:**
- **Procedural generation**: Creating 3D models from code (not loading files)
- **Vertices**: 3D points that define shape
- **Indices**: Connects vertices into triangles
- **Mesh**: Collection of vertices + indices + colors

---

### 8. **Dependency Injection** (`di/` package)

#### `AppModule.kt`
```kotlin
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    @Provides
    @Singleton
    fun providePokemonRepository(): PokemonRepository = LocalPokemonRepository()
    
    @Provides
    @Singleton
    fun provideMlInferenceService(
        @ApplicationContext context: Context,
        repository: PokemonRepository
    ): MLInferenceService = TfliteAnimalClassifier(context, repository)
}
```

**What it does:**
- Tells Hilt **how to create dependencies**
- When something needs a `PokemonRepository`, Hilt creates `LocalPokemonRepository`
- When something needs `MLInferenceService`, Hilt creates `TfliteAnimalClassifier`

**Key Concepts:**
- **Dependency Injection**: Automatically providing objects their dependencies
- **@Module**: Tells Hilt this is a DI module
- **@InstallIn**: Specifies which Hilt component to install in
- **SingletonComponent**: App-wide scope (single instance)
- **@Provides**: Method that provides a dependency
- **@Singleton**: Only create one instance
- **@ApplicationContext**: Injects application Context
- **object**: Kotlin singleton (like static class in Java)

---

## How Everything Works Together

### Application Startup Flow:

1. **App Launches** → `PokemonVizorApp` created (Hilt initializes)
2. **MainActivity Created** → Hilt injects `CameraViewModel`
3. **ViewModel Created** → Hilt injects `PokemonRepository` and `MLInferenceService`
4. **UI Rendered** → Compose calls `CameraScreen(viewModel)`
5. **Camera Permission** → User grants permission
6. **Camera Starts** → `CameraController` sets up CameraX
7. **Frames Arrive** → `FrameAnalyzer` receives frames

### Recognition Flow:

1. **Camera Frame** → `FrameAnalyzer.analyze()`
2. **Frame Conversion** → Convert ImageProxy to Bitmap
3. **ML Inference** → `TfliteAnimalClassifier.analyzeFrame()`
4. **Result** → `RecognitionResult` with animal ID + confidence
5. **ViewModel** → `onResult()` fetches animal info, Pokemon mapping, 3D spec
6. **State Update** → `_uiState.value = newState`
7. **UI Recompose** → Compose detects state change, rerenders
8. **3D Render** → Renderer updates and draws new Pokemon

### Data Flow Diagram:

```
Camera Frame
    ↓
FrameAnalyzer
    ↓
MLInferenceService (TFLite)
    ↓
RecognitionResult (animalId)
    ↓
ViewModel.onResult()
    ↓
PokemonRepository (lookup data)
    ↓
Update StateFlow
    ↓
UI Recomposition
    ├→ Display animal info
    ├→ Display Pokemon info
    └→ Update 3D renderer
```

---

## Key Kotlin & Android Concepts

### Kotlin Language Features

1. **Data Classes**
   ```kotlin
   data class User(val name: String, val age: Int)
   ```
   Auto-generates: `equals()`, `hashCode()`, `toString()`, `copy()`

2. **Sealed Interfaces/Classes**
   ```kotlin
   sealed interface Result {
       data class Success(val data: String) : Result
       data class Error(val message: String) : Result
   }
   ```
   Restricted hierarchy for type-safe state management

3. **Nullable Types**
   ```kotlin
   val name: String   // Never null
   val name: String?  // Can be null
   ```

4. **Extension Functions**
   ```kotlin
   fun String.reverse(): String = this.reversed()
   "hello".reverse() // "olleh"
   ```

5. **Lambda Functions**
   ```kotlin
   list.filter { it > 5 }  // `it` is implicit parameter
   list.map { item -> item * 2 }
   ```

6. **Coroutines**
   ```kotlin
   suspend fun fetchData(): String {
       delay(1000)  // Non-blocking wait
       return "data"
   }
   
   viewModelScope.launch {
       val data = fetchData()  // Suspends without blocking thread
   }
   ```

7. **Property Delegates**
   ```kotlin
   private val viewModel: MyViewModel by viewModels()
   ```

### Android Architecture Components

1. **ViewModel**
   - Survives configuration changes (rotation)
   - Tied to Activity/Fragment lifecycle
   - Should not hold Activity/View references

2. **StateFlow / LiveData**
   - Observable data holder
   - UI observes and updates automatically
   - Lifecycle-aware

3. **Lifecycle**
   - Activities/Fragments have lifecycle (onCreate, onStart, onResume, etc.)
   - Lifecycle-aware components respond automatically

### Jetpack Compose Concepts

1. **@Composable Functions**
   - Pure functions that describe UI
   - Can be called multiple times (recomposition)
   - No side effects in the function body

2. **State**
   ```kotlin
   var count by remember { mutableStateOf(0) }
   ```
   - When state changes, UI automatically recomposes

3. **remember { }**
   - Stores value across recompositions
   - Recreated if keys change

4. **Side Effects**
   ```kotlin
   LaunchedEffect(key) { /* Runs once when key changes */ }
   DisposableEffect(key) { /* Cleanup when leaving composition */ }
   ```

### Dependency Injection with Hilt

1. **@HiltAndroidApp** - Application class
2. **@AndroidEntryPoint** - Activity/Fragment/Service
3. **@HiltViewModel** - ViewModel
4. **@Inject constructor** - Constructor injection
5. **@Module + @Provides** - How to create dependencies

### CameraX

1. **Preview** - Show camera feed
2. **ImageAnalysis** - Process frames
3. **ImageCapture** - Take photos
4. **Lifecycle-aware** - Automatically handles pause/resume

### OpenGL ES

1. **Renderer** - Renders 3D graphics
2. **Shaders** - GPU programs (vertex + fragment)
3. **Matrices** - 3D transformations
4. **Meshes** - Vertices + Indices

---

## Testing

Tests are located in `app/src/test/`:

- **CameraViewModelTest.kt** - Tests ViewModel logic
- **LocalPokemonRepositoryTest.kt** - Tests data retrieval
- **PrimitiveModelBuilderTest.kt** - Tests 3D model generation

These are **unit tests** (JVM, no emulator needed) that test individual components in isolation.

---

## Summary

The `app` folder contains:
- **Build configuration** (build.gradle)
- **Manifest** (permissions, components)
- **Source code** organized by layers:
  - **ui**: User interface (Compose + ViewModel)
  - **camera**: Camera management (CameraX)
  - **ml**: Machine learning (TensorFlow Lite)
  - **domain**: Business logic and models
  - **data**: Data sources (hardcoded data)
  - **render3d**: 3D rendering (OpenGL ES)
  - **di**: Dependency injection (Hilt)

Everything works together to:
1. Capture camera frames
2. Analyze them with ML
3. Map results to Pokemon
4. Render 3D avatars
5. Display info to the user

The architecture follows modern Android best practices: MVVM, dependency injection, reactive programming, and clean separation of concerns.
