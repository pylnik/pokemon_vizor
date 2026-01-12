package com.example.pokemonvizor.ml

import android.graphics.Bitmap
import com.example.pokemonvizor.domain.RecognitionResult

interface MLInferenceService {
    suspend fun analyzeFrame(bitmap: Bitmap): RecognitionResult?
    fun close()
}
