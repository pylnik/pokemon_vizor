package com.example.pokemonvizor.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class LocalPokemonRepositoryTest {
    private val repository = LocalPokemonRepository()

    @Test
    fun `returns animal info for known id`() {
        val info = repository.getAnimalInfo("deer")
        assertNotNull(info)
        assertEquals("Deer", info?.displayName)
    }

    @Test
    fun `returns pokemon entry and spec for mapping`() {
        val entry = repository.getPokemonEntry("goat")
        assertNotNull(entry)
        val spec = entry?.pokemonId?.let { repository.getPokemonSpec(it) }
        assertNotNull(spec)
    }

    @Test
    fun `returns all animal ids`() {
        val ids = repository.getAllAnimalIds()
        assertEquals(8, ids.size)
    }
}
