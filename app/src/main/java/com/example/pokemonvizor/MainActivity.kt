package com.example.pokemonvizor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import com.example.pokemonvizor.ui.CameraScreen
import com.example.pokemonvizor.ui.CameraViewModel
import com.example.pokemonvizor.ui.LocalPokemonRenderer
import com.example.pokemonvizor.render3d.PokemonGlSurfaceView
import dagger.hilt.android.AndroidEntryPoint

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
