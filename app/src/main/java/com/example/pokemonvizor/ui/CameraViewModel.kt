package com.example.pokemonvizor.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokemonvizor.domain.PokemonRepository
import com.example.pokemonvizor.domain.RecognitionResult
import com.example.pokemonvizor.ml.MLInferenceService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val repository: PokemonRepository,
    private val mlService: MLInferenceService
) : ViewModel() {
    private val singleShotRequested = AtomicBoolean(false)
    private val _uiState = MutableStateFlow<CameraUiState>(
        CameraUiState.Ready(
            isRealtime = true,
            selectedAnimal = null,
            selectedPokemon = null,
            selectedSpec = null,
            confidence = 0f,
            inferenceMs = 0L,
            fps = 0f,
            lastFrame = 0L
        )
    )
    val uiState: StateFlow<CameraUiState> = _uiState.asStateFlow()

    fun toggleRealtime(enabled: Boolean) {
        val current = _uiState.value
        if (current is CameraUiState.Ready) {
            _uiState.value = current.copy(isRealtime = enabled)
        }
    }

    fun triggerSingleAnalysis() {
        singleShotRequested.set(true)
    }

    fun shouldAnalyzeFrame(): Boolean {
        val current = _uiState.value
        return if (current is CameraUiState.Ready && current.isRealtime) {
            true
        } else {
            singleShotRequested.getAndSet(false)
        }
    }

    fun onResult(result: RecognitionResult) {
        viewModelScope.launch {
            val info = repository.getAnimalInfo(result.animalId)
            val pokemon = repository.getPokemonEntry(result.animalId)
            val spec = pokemon?.let { repository.getPokemonSpec(it.pokemonId) }
            val current = _uiState.value
            if (current is CameraUiState.Ready) {
                _uiState.value = current.copy(
                    selectedAnimal = info,
                    selectedPokemon = pokemon,
                    selectedSpec = spec,
                    confidence = result.confidence,
                    inferenceMs = result.inferenceMs,
                    fps = result.fps,
                    lastFrame = System.currentTimeMillis()
                )
            }
        }
    }

    fun mlService(): MLInferenceService = mlService
}
