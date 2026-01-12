package com.example.pokemonvizor.render3d

import com.example.pokemonvizor.domain.ArgbColor
import com.example.pokemonvizor.domain.PokemonPrimitiveModelSpec
import com.example.pokemonvizor.domain.Primitive
import com.example.pokemonvizor.domain.PrimitiveType
import com.example.pokemonvizor.domain.Vector3
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PrimitiveModelBuilderTest {
    @Test
    fun `build creates mesh instances for each primitive`() {
        val spec = PokemonPrimitiveModelSpec(
            pokemonId = "testmon",
            primitives = listOf(
                Primitive(
                    id = "body",
                    type = PrimitiveType.BOX,
                    size = Vector3(1f, 1f, 1f),
                    position = Vector3(0f, 0f, 0f),
                    rotation = Vector3(0f, 0f, 0f),
                    color = ArgbColor(1f, 0.2f, 0.3f, 0.4f)
                ),
                Primitive(
                    id = "head",
                    type = PrimitiveType.SPHERE,
                    size = Vector3(0.5f, 0.5f, 0.5f),
                    position = Vector3(0f, 1f, 0f),
                    rotation = Vector3(0f, 0f, 0f),
                    color = ArgbColor(1f, 0.6f, 0.7f, 0.8f)
                )
            )
        )

        val builder = PrimitiveModelBuilder()
        val instances = builder.build(spec)

        assertEquals(2, instances.size)
        assertTrue(instances.all { it.mesh.indexCount > 0 })
    }
}
