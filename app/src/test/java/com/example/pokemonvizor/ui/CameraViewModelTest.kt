package com.example.pokemonvizor.ui

import com.example.pokemonvizor.domain.AnimalInfo
import com.example.pokemonvizor.domain.PokemonEntry
import com.example.pokemonvizor.domain.PokemonPrimitiveModelSpec
import com.example.pokemonvizor.domain.PokemonRepository
import com.example.pokemonvizor.domain.Primitive
import com.example.pokemonvizor.domain.PrimitiveType
import com.example.pokemonvizor.domain.RecognitionResult
import com.example.pokemonvizor.domain.Vector3
import com.example.pokemonvizor.domain.ArgbColor
import com.example.pokemonvizor.ml.MLInferenceService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CameraViewModelTest {
    private val dispatcher = StandardTestDispatcher()

    private lateinit var viewModel: CameraViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        viewModel = CameraViewModel(FakeRepository(), FakeMlService())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `toggle realtime updates state`() = runTest {
        viewModel.toggleRealtime(false)
        val state = viewModel.uiState.value
        assertTrue(state is CameraUiState.Ready && !state.isRealtime)
    }

    @Test
    fun `single shot analysis toggles flag`() = runTest {
        viewModel.toggleRealtime(false)
        viewModel.triggerSingleAnalysis()
        assertTrue(viewModel.shouldAnalyzeFrame())
        assertTrue(!viewModel.shouldAnalyzeFrame())
    }

    @Test
    fun `onResult updates mapping info`() = runTest {
        val result = RecognitionResult("deer", 0.9f, 10L, 5f)
        viewModel.onResult(result)
        advanceUntilIdle()
        val state = viewModel.uiState.value as CameraUiState.Ready
        assertEquals("Deer", state.selectedAnimal?.displayName)
        assertEquals("Springle", state.selectedPokemon?.pokemonName)
        assertEquals("springle", state.selectedSpec?.pokemonId)
    }

    private class FakeRepository : PokemonRepository {
        private val animal = AnimalInfo(
            id = "deer",
            displayName = "Deer",
            description = "Test",
            funFacts = listOf("Fact")
        )
        private val pokemon = PokemonEntry(
            animalId = "deer",
            pokemonId = "springle",
            pokemonName = "Springle",
            pokemonDescription = "Test"
        )
        private val spec = PokemonPrimitiveModelSpec(
            pokemonId = "springle",
            primitives = listOf(
                Primitive(
                    id = "body",
                    type = PrimitiveType.BOX,
                    size = Vector3(1f, 1f, 1f),
                    position = Vector3(0f, 0f, 0f),
                    rotation = Vector3(0f, 0f, 0f),
                    color = ArgbColor(1f, 0.2f, 0.2f, 0.2f)
                )
            )
        )

        override fun getAnimalInfo(animalId: String) = if (animalId == "deer") animal else null
        override fun getPokemonEntry(animalId: String) = if (animalId == "deer") pokemon else null
        override fun getPokemonSpec(pokemonId: String) = if (pokemonId == "springle") spec else null
        override fun getAllAnimalIds(): List<String> = listOf("deer")
    }

    private class FakeMlService : MLInferenceService {
        override suspend fun analyzeFrame(bitmap: android.graphics.Bitmap) = null
        override fun close() = Unit
    }
}
