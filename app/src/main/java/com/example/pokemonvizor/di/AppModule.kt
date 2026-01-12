package com.example.pokemonvizor.di

import android.content.Context
import com.example.pokemonvizor.data.LocalPokemonRepository
import com.example.pokemonvizor.domain.PokemonRepository
import com.example.pokemonvizor.ml.MLInferenceService
import com.example.pokemonvizor.ml.TfliteAnimalClassifier
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

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
