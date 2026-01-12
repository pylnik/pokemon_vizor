package com.example.pokemonvizor.render3d

import java.nio.FloatBuffer
import java.nio.ShortBuffer

class Mesh(
    val vertexBuffer: FloatBuffer,
    val colorBuffer: FloatBuffer,
    val indexBuffer: ShortBuffer,
    val indexCount: Int
)

class MeshInstance(
    val mesh: Mesh,
    val modelMatrix: FloatArray
)
