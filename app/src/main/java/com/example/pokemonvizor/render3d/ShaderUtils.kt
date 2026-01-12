package com.example.pokemonvizor.render3d

import android.opengl.GLES20

object ShaderUtils {
    fun createProgram(vertexShader: String, fragmentShader: String): Int {
        val vertexId = compileShader(GLES20.GL_VERTEX_SHADER, vertexShader)
        val fragmentId = compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShader)
        val programId = GLES20.glCreateProgram()
        GLES20.glAttachShader(programId, vertexId)
        GLES20.glAttachShader(programId, fragmentId)
        GLES20.glLinkProgram(programId)
        return programId
    }

    private fun compileShader(type: Int, shaderCode: String): Int {
        val shaderId = GLES20.glCreateShader(type)
        GLES20.glShaderSource(shaderId, shaderCode)
        GLES20.glCompileShader(shaderId)
        return shaderId
    }
}
