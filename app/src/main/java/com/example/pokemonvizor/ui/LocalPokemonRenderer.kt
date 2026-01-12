package com.example.pokemonvizor.ui

import androidx.compose.runtime.staticCompositionLocalOf
import com.example.pokemonvizor.render3d.PokemonGlSurfaceView

val LocalPokemonRenderer = staticCompositionLocalOf<PokemonGlSurfaceView> {
    error("PokemonGlSurfaceView not provided")
}
