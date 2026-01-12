package com.example.pokemonvizor.ml

import android.content.Context
import android.graphics.Bitmap
import android.os.SystemClock
import com.example.pokemonvizor.domain.PokemonRepository
import com.example.pokemonvizor.domain.RecognitionResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel
import javax.inject.Inject
import kotlin.math.min

class TfliteAnimalClassifier @Inject constructor(
    private val context: Context,
    private val repository: PokemonRepository
) : MLInferenceService {
    private val inputSize = 224
    private val labels = repository.getAllAnimalIds()
    // TODO: Replace with your real TFLite model in app/src/main/assets/ml/animal_model.tflite.
    private val interpreter: Interpreter? = runCatching { Interpreter(loadModelFile("ml/animal_model.tflite")) }
        .onFailure { it.printStackTrace() }
        .getOrNull()
    private var frameCounter = 0

    override suspend fun analyzeFrame(bitmap: Bitmap): RecognitionResult? = withContext(Dispatchers.Default) {
        val startTime = SystemClock.elapsedRealtime()
        val scaled = Bitmap.createScaledBitmap(bitmap, inputSize, inputSize, true)
        val input = bitmapToBuffer(scaled)
        val output = Array(1) { FloatArray(labels.size) }

        val result = if (interpreter == null) {
            val fallbackIndex = (frameCounter++ / 15) % labels.size
            RecognitionResult(labels[fallbackIndex], 0.5f, 0L, 0f)
        } else {
            interpreter.run(input, output)
            val (index, confidence) = output[0].withIndex().maxBy { it.value }
            val safeIndex = min(index.index, labels.lastIndex)
            RecognitionResult(labels[safeIndex], confidence, 0L, 0f)
        }

        val elapsed = SystemClock.elapsedRealtime() - startTime
        result.copy(inferenceMs = elapsed)
    }

    override fun close() {
        interpreter?.close()
    }

    private fun bitmapToBuffer(bitmap: Bitmap): ByteBuffer {
        val buffer = ByteBuffer.allocateDirect(4 * inputSize * inputSize * 3)
        buffer.order(ByteOrder.nativeOrder())
        val pixels = IntArray(inputSize * inputSize)
        bitmap.getPixels(pixels, 0, inputSize, 0, 0, inputSize, inputSize)
        var index = 0
        for (y in 0 until inputSize) {
            for (x in 0 until inputSize) {
                val pixel = pixels[index++]
                buffer.putFloat(((pixel shr 16) and 0xFF) / 255f)
                buffer.putFloat(((pixel shr 8) and 0xFF) / 255f)
                buffer.putFloat((pixel and 0xFF) / 255f)
            }
        }
        buffer.rewind()
        return buffer
    }

    private fun loadModelFile(path: String): ByteBuffer {
        val fileDescriptor = context.assets.openFd(path)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }
}
