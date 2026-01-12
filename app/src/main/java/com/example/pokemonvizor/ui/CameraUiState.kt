package com.example.pokemonvizor.ui

import com.example.pokemonvizor.domain.AnimalInfo
import com.example.pokemonvizor.domain.PokemonEntry
import com.example.pokemonvizor.domain.PokemonPrimitiveModelSpec

sealed class CameraUiState {
    data object Loading : CameraUiState()

    data class Ready(
        val isRealtime: Boolean,
        val selectedAnimal: AnimalInfo?,
        val selectedPokemon: PokemonEntry?,
        val selectedSpec: PokemonPrimitiveModelSpec?,
        val confidence: Float,
        val inferenceMs: Long,
        val fps: Float,
        val lastFrame: Long
    ) : CameraUiState()

    data class Error(val message: String) : CameraUiState()
}
