package com.example.pokemonvizor.render3d

import android.opengl.Matrix
import com.example.pokemonvizor.domain.Vector3

object TransformUtils {
    fun modelMatrix(position: Vector3, rotation: Vector3): FloatArray {
        val matrix = FloatArray(16)
        Matrix.setIdentityM(matrix, 0)
        Matrix.translateM(matrix, 0, position.x, position.y, position.z)
        Matrix.rotateM(matrix, 0, rotation.x, 1f, 0f, 0f)
        Matrix.rotateM(matrix, 0, rotation.y, 0f, 1f, 0f)
        Matrix.rotateM(matrix, 0, rotation.z, 0f, 0f, 1f)
        return matrix
    }
}
