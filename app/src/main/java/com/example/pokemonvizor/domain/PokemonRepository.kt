package com.example.pokemonvizor.domain

interface PokemonRepository {
    fun getAnimalInfo(animalId: String): AnimalInfo?
    fun getPokemonEntry(animalId: String): PokemonEntry?
    fun getPokemonSpec(pokemonId: String): PokemonPrimitiveModelSpec?
    fun getAllAnimalIds(): List<String>
}
