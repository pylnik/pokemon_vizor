package com.example.pokemonvizor.render3d

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import com.example.pokemonvizor.domain.PokemonPrimitiveModelSpec
import java.util.concurrent.atomic.AtomicReference
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class PokemonRenderer : GLSurfaceView.Renderer {
    private val modelSpec = AtomicReference<PokemonPrimitiveModelSpec?>(null)
    private val modelBuilder = PrimitiveModelBuilder()
    private var meshInstances: List<MeshInstance> = emptyList()
    private var programId = 0
    private var positionHandle = 0
    private var colorHandle = 0
    private var mvpHandle = 0
    private val projection = FloatArray(16)
    private val view = FloatArray(16)
    private val vp = FloatArray(16)
    private var rotationYaw = 0f
    private var needsRebuild = true

    fun updateModel(spec: PokemonPrimitiveModelSpec?) {
        modelSpec.set(spec)
        needsRebuild = true
    }

    fun setRotationYaw(yawDegrees: Float) {
        rotationYaw = yawDegrees
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(0.08f, 0.08f, 0.1f, 1f)
        programId = ShaderUtils.createProgram(VERTEX_SHADER, FRAGMENT_SHADER)
        positionHandle = GLES20.glGetAttribLocation(programId, "aPosition")
        colorHandle = GLES20.glGetAttribLocation(programId, "aColor")
        mvpHandle = GLES20.glGetUniformLocation(programId, "uMvpMatrix")
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        val ratio = width.toFloat() / height.coerceAtLeast(1)
        Matrix.perspectiveM(projection, 0, 45f, ratio, 0.1f, 50f)
        Matrix.setLookAtM(view, 0, 0f, 1.2f, 4f, 0f, 0f, 0f, 0f, 1f, 0f)
        Matrix.multiplyMM(vp, 0, projection, 0, view, 0)
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        if (needsRebuild) {
            meshInstances = modelBuilder.build(modelSpec.get())
            needsRebuild = false
        }
        GLES20.glUseProgram(programId)
        val rotationMatrix = FloatArray(16)
        Matrix.setIdentityM(rotationMatrix, 0)
        Matrix.rotateM(rotationMatrix, 0, rotationYaw, 0f, 1f, 0f)
        meshInstances.forEach { instance ->
            val model = FloatArray(16)
            Matrix.multiplyMM(model, 0, rotationMatrix, 0, instance.modelMatrix, 0)
            val mvp = FloatArray(16)
            Matrix.multiplyMM(mvp, 0, vp, 0, model, 0)
            GLES20.glUniformMatrix4fv(mvpHandle, 1, false, mvp, 0)
            GLES20.glEnableVertexAttribArray(positionHandle)
            GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 0, instance.mesh.vertexBuffer)
            GLES20.glEnableVertexAttribArray(colorHandle)
            GLES20.glVertexAttribPointer(colorHandle, 4, GLES20.GL_FLOAT, false, 0, instance.mesh.colorBuffer)
            GLES20.glDrawElements(
                GLES20.GL_TRIANGLES,
                instance.mesh.indexCount,
                GLES20.GL_UNSIGNED_SHORT,
                instance.mesh.indexBuffer
            )
            GLES20.glDisableVertexAttribArray(positionHandle)
            GLES20.glDisableVertexAttribArray(colorHandle)
        }
    }

    private companion object {
        const val VERTEX_SHADER = """
            uniform mat4 uMvpMatrix;
            attribute vec4 aPosition;
            attribute vec4 aColor;
            varying vec4 vColor;
            void main() {
                vColor = aColor;
                gl_Position = uMvpMatrix * aPosition;
            }
        """

        const val FRAGMENT_SHADER = """
            precision mediump float;
            varying vec4 vColor;
            void main() {
                gl_FragColor = vColor;
            }
        """
    }
}
