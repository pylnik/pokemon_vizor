package com.example.pokemonvizor.render3d

import android.content.Context
import android.opengl.GLSurfaceView
import com.example.pokemonvizor.domain.PokemonPrimitiveModelSpec

class PokemonGlSurfaceView(context: Context) : GLSurfaceView(context) {
    private val renderer: PokemonRenderer

    init {
        setEGLContextClientVersion(2)
        renderer = PokemonRenderer()
        setRenderer(renderer)
        renderMode = RENDERMODE_CONTINUOUSLY
    }

    fun setModelSpec(spec: PokemonPrimitiveModelSpec?) {
        renderer.updateModel(spec)
    }

    fun setRotationYaw(yawDegrees: Float) {
        renderer.setRotationYaw(yawDegrees)
    }
}
