# Pokemon Vizor

Pokemon Vizor is an Android 13+ Kotlin app MVP that uses CameraX for live preview and on-device
inference to recognize animals in a wild park, then displays a Pokemon-style mapping with a
procedural 3D avatar rendered from simple primitives (boxes, spheres, cylinders, cones).

## What the app does

- Opens to a Camera screen with live preview.
- Runs on-device ML inference (real-time or tap-to-analyze) to classify animals.
- Displays:
  - Top label + confidence.
  - Animal info card (description + fun facts).
  - Pokemon mapping (name + short description).
  - A simple 3D procedural Pokemon avatar rendered on-screen.

## Architecture

Single-module MVP with clean-ish layers and MVVM:

- `ui`: Compose screens + ViewModel.
- `camera`: CameraX preview + analysis.
- `ml`: TFLite inference service (stub until model is added).
- `domain`: Core models + repository interface.
- `data`: Local animal info + Pokemon mapping + primitive specs.
- `render3d`: OpenGL ES renderer and procedural primitive builder.
- `di`: Hilt module wiring.

## Running the app

1. Open the project in Android Studio.
2. Add a TFLite model at `app/src/main/assets/ml/animal_model.tflite`.
3. Build and run on an Android 13+ device or emulator with a camera.

### Notes

- If the model file is missing, the ML service falls back to deterministic dummy results so the
  UI and 3D renderer can still be exercised.
- The 3D avatar is rendered via OpenGL ES in a small panel; rotation can be controlled with a
  slider (placeholder for direction-based rotation).

## Tests

JVM unit tests live under `app/src/test` and cover:

- Local repository mappings.
- Primitive model building.
- ViewModel state updates.
