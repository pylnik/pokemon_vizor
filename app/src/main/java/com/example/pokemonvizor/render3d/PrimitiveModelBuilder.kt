package com.example.pokemonvizor.render3d

import com.example.pokemonvizor.domain.PokemonPrimitiveModelSpec
import com.example.pokemonvizor.domain.Primitive
import com.example.pokemonvizor.domain.PrimitiveType
import com.example.pokemonvizor.domain.Vector3
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.cos
import kotlin.math.sin

class PrimitiveModelBuilder {
    fun build(spec: PokemonPrimitiveModelSpec?): List<MeshInstance> {
        if (spec == null) return emptyList()
        return spec.primitives.map { primitive ->
            val mesh = when (primitive.type) {
                PrimitiveType.BOX -> createBox(primitive.size, primitive.color)
                PrimitiveType.SPHERE -> createSphere(primitive.size.x, primitive.color)
                PrimitiveType.CYLINDER -> createCylinder(primitive.size.x, primitive.size.y, primitive.color)
                PrimitiveType.CONE -> createCone(primitive.size.x, primitive.size.y, primitive.color)
            }
            val modelMatrix = TransformUtils.modelMatrix(primitive.position, primitive.rotation)
            MeshInstance(mesh, modelMatrix)
        }
    }

    private fun createBox(size: Vector3, color: com.example.pokemonvizor.domain.ArgbColor): Mesh {
        val hx = size.x / 2f
        val hy = size.y / 2f
        val hz = size.z / 2f
        val vertices = floatArrayOf(
            -hx, -hy, -hz,
            hx, -hy, -hz,
            hx, hy, -hz,
            -hx, hy, -hz,
            -hx, -hy, hz,
            hx, -hy, hz,
            hx, hy, hz,
            -hx, hy, hz
        )
        val indices = shortArrayOf(
            0, 1, 2, 0, 2, 3,
            4, 5, 6, 4, 6, 7,
            0, 1, 5, 0, 5, 4,
            2, 3, 7, 2, 7, 6,
            1, 2, 6, 1, 6, 5,
            0, 3, 7, 0, 7, 4
        )
        return buildMesh(vertices, indices, color)
    }

    private fun createSphere(radius: Float, color: com.example.pokemonvizor.domain.ArgbColor): Mesh {
        val stacks = 12
        val slices = 12
        val vertices = ArrayList<Float>()
        val indices = ArrayList<Short>()
        for (stack in 0..stacks) {
            val phi = Math.PI * stack / stacks
            val y = cos(phi).toFloat()
            val r = sin(phi).toFloat()
            for (slice in 0..slices) {
                val theta = 2.0 * Math.PI * slice / slices
                val x = (r * cos(theta)).toFloat()
                val z = (r * sin(theta)).toFloat()
                vertices.add(x * radius)
                vertices.add(y * radius)
                vertices.add(z * radius)
            }
        }
        val ring = slices + 1
        for (stack in 0 until stacks) {
            for (slice in 0 until slices) {
                val first = (stack * ring + slice).toShort()
                val second = (first + ring).toShort()
                indices.add(first)
                indices.add((first + 1).toShort())
                indices.add(second)
                indices.add((first + 1).toShort())
                indices.add((second + 1).toShort())
                indices.add(second)
            }
        }
        return buildMesh(vertices.toFloatArray(), indices.toShortArray(), color)
    }

    private fun createCylinder(radius: Float, height: Float, color: com.example.pokemonvizor.domain.ArgbColor): Mesh {
        val slices = 16
        val vertices = ArrayList<Float>()
        val indices = ArrayList<Short>()
        val half = height / 2f
        for (i in 0..slices) {
            val theta = 2.0 * Math.PI * i / slices
            val x = (cos(theta) * radius).toFloat()
            val z = (sin(theta) * radius).toFloat()
            vertices.add(x)
            vertices.add(-half)
            vertices.add(z)
            vertices.add(x)
            vertices.add(half)
            vertices.add(z)
        }
        for (i in 0 until slices) {
            val base = (i * 2).toShort()
            indices.add(base)
            indices.add((base + 1).toShort())
            indices.add((base + 2).toShort())
            indices.add((base + 1).toShort())
            indices.add((base + 3).toShort())
            indices.add((base + 2).toShort())
        }
        return buildMesh(vertices.toFloatArray(), indices.toShortArray(), color)
    }

    private fun createCone(radius: Float, height: Float, color: com.example.pokemonvizor.domain.ArgbColor): Mesh {
        val slices = 16
        val vertices = ArrayList<Float>()
        val indices = ArrayList<Short>()
        val tipIndex: Short
        vertices.add(0f)
        vertices.add(height / 2f)
        vertices.add(0f)
        tipIndex = 0
        for (i in 0..slices) {
            val theta = 2.0 * Math.PI * i / slices
            val x = (cos(theta) * radius).toFloat()
            val z = (sin(theta) * radius).toFloat()
            vertices.add(x)
            vertices.add(-height / 2f)
            vertices.add(z)
        }
        for (i in 1..slices) {
            indices.add(tipIndex)
            indices.add(i.toShort())
            indices.add((i + 1).toShort())
        }
        return buildMesh(vertices.toFloatArray(), indices.toShortArray(), color)
    }

    private fun buildMesh(vertices: FloatArray, indices: ShortArray, color: com.example.pokemonvizor.domain.ArgbColor): Mesh {
        val colors = FloatArray(vertices.size / 3 * 4) { index ->
            when (index % 4) {
                0 -> color.r
                1 -> color.g
                2 -> color.b
                else -> color.a
            }
        }
        val vertexBuffer = ByteBuffer.allocateDirect(vertices.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .apply {
                put(vertices)
                position(0)
            }
        val colorBuffer = ByteBuffer.allocateDirect(colors.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .apply {
                put(colors)
                position(0)
            }
        val indexBuffer = ByteBuffer.allocateDirect(indices.size * 2)
            .order(ByteOrder.nativeOrder())
            .asShortBuffer()
            .apply {
                put(indices)
                position(0)
            }
        return Mesh(vertexBuffer, colorBuffer, indexBuffer, indices.size)
    }
}
