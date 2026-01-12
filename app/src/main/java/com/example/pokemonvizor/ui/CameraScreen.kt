package com.example.pokemonvizor.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.pokemonvizor.camera.CameraController
import com.example.pokemonvizor.camera.FrameAnalyzer
import com.example.pokemonvizor.domain.PokemonPrimitiveModelSpec
import com.example.pokemonvizor.render3d.PokemonGlSurfaceView
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat

@Composable
fun CameraScreen(viewModel: CameraViewModel) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val renderer = LocalPokemonRenderer.current
    var rotationYaw by remember { mutableFloatStateOf(0f) }
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
    }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    val analyzer = remember {
        FrameAnalyzer(
            mlService = viewModel.mlService(),
            shouldAnalyze = { viewModel.shouldAnalyzeFrame() },
            onResult = { result, _ -> viewModel.onResult(result) }
        )
    }
    val cameraController = remember { CameraController(context, analyzer) }

    Box(modifier = Modifier.fillMaxSize()) {
        if (hasPermission) {
            AndroidView(
                factory = { ctx ->
                    PreviewView(ctx).apply {
                        cameraController.startCamera(this, lifecycleOwner)
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Text(
                text = "Camera permission required",
                color = Color.White,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        when (val state = uiState) {
            is CameraUiState.Ready -> {
                CameraOverlay(
                    state = state,
                    onToggleRealtime = { viewModel.toggleRealtime(it) },
                    onTapAnalyze = { viewModel.triggerSingleAnalysis() },
                    rotationYaw = rotationYaw,
                    onYawChange = { yawDegrees ->
                        rotationYaw = yawDegrees
                        // TODO: Wire yaw to direction estimation when model provides orientation hints.
                        renderer.setRotationYaw(yawDegrees)
                    }
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                        .size(180.dp)
                        .background(Color(0xAA000000))
                ) {
                    PokemonPanel(spec = state.selectedSpec, renderer = renderer)
                }
            }

            is CameraUiState.Error -> {
                Text(
                    text = state.message,
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            CameraUiState.Loading -> {
                Text(
                    text = "Loading...",
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            cameraController.stopCamera()
        }
    }
}

@Composable
private fun CameraOverlay(
    state: CameraUiState.Ready,
    onToggleRealtime: (Boolean) -> Unit,
    onTapAnalyze: () -> Unit,
    rotationYaw: Float,
    onYawChange: (Float) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = buildString {
                append(state.selectedAnimal?.displayName ?: "No animal")
                if (state.confidence > 0f) {
                    append(" ")
                    append(String.format("%.2f", state.confidence))
                }
            },
            color = Color.White,
            style = MaterialTheme.typography.headlineSmall
        )

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(text = state.selectedAnimal?.description ?: "Point the camera at an animal.")
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Fun facts:")
                state.selectedAnimal?.funFacts?.forEach { fact ->
                    Text(text = "• $fact")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Pokemon mapping:")
                Text(text = state.selectedPokemon?.pokemonName ?: "Unknown")
                Text(text = state.selectedPokemon?.pokemonDescription ?: "")
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Real-time", color = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Switch(checked = state.isRealtime, onCheckedChange = onToggleRealtime)
            Spacer(modifier = Modifier.width(12.dp))
            Button(onClick = onTapAnalyze, enabled = !state.isRealtime) {
                Text(text = "Tap to analyze")
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Rotation", color = Color.White)
            Slider(
                value = rotationYaw,
                valueRange = 0f..360f,
                onValueChange = { onYawChange(it) },
                modifier = Modifier.weight(1f)
            )
        }

        Text(
            text = "Inference: ${state.inferenceMs}ms | FPS: ${String.format("%.1f", state.fps)}",
            color = Color.White
        )
    }
}

@Composable
private fun PokemonPanel(spec: PokemonPrimitiveModelSpec?, renderer: PokemonGlSurfaceView) {
    LaunchedEffect(spec) {
        renderer.setModelSpec(spec)
    }
    AndroidView(
        factory = { renderer },
        modifier = Modifier.fillMaxSize()
    )
}
