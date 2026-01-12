package com.example.pokemonvizor.domain

data class AnimalInfo(
    val id: String,
    val displayName: String,
    val description: String,
    val funFacts: List<String>,
    val wikipediaUrl: String? = null
)

data class PokemonEntry(
    val animalId: String,
    val pokemonId: String,
    val pokemonName: String,
    val pokemonDescription: String
)

data class PokemonPrimitiveModelSpec(
    val pokemonId: String,
    val primitives: List<Primitive>
)

data class Primitive(
    val id: String,
    val type: PrimitiveType,
    val size: Vector3,
    val position: Vector3,
    val rotation: Vector3,
    val color: ArgbColor,
    val parentId: String? = null
)

enum class PrimitiveType {
    BOX,
    SPHERE,
    CYLINDER,
    CONE
}

data class Vector3(val x: Float, val y: Float, val z: Float)

data class ArgbColor(val a: Float, val r: Float, val g: Float, val b: Float) {
    init {
        require(a in 0f..1f && r in 0f..1f && g in 0f..1f && b in 0f..1f)
    }
}

data class RecognitionResult(
    val animalId: String,
    val confidence: Float,
    val inferenceMs: Long,
    val fps: Float
)
