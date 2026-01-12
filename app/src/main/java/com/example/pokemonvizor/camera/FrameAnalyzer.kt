package com.example.pokemonvizor.camera

import android.graphics.Bitmap
import android.os.SystemClock
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.example.pokemonvizor.domain.RecognitionResult
import com.example.pokemonvizor.ml.MLInferenceService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicLong

class FrameAnalyzer(
    private val mlService: MLInferenceService,
    private val shouldAnalyze: () -> Boolean,
    private val onResult: (RecognitionResult, Bitmap) -> Unit
) : ImageAnalysis.Analyzer {
    private val scope = CoroutineScope(Dispatchers.Default)
    private val lastAnalyzed = AtomicLong(0L)
    private val lastResultTime = AtomicLong(0L)
    private var job: Job? = null
    private val targetIntervalMs = 120L

    override fun analyze(image: ImageProxy) {
        if (!shouldAnalyze()) {
            image.close()
            return
        }
        val now = SystemClock.elapsedRealtime()
        if (now - lastAnalyzed.get() < targetIntervalMs) {
            image.close()
            return
        }
        if (job?.isActive == true) {
            image.close()
            return
        }
        lastAnalyzed.set(now)
        val bitmap = image.toBitmap()
        job = scope.launch {
            val result = mlService.analyzeFrame(bitmap)
            if (result != null) {
                val now = SystemClock.elapsedRealtime()
                val last = lastResultTime.getAndSet(now)
                val fps = if (last == 0L) 0f else 1000f / (now - last).coerceAtLeast(1L)
                onResult(result.copy(fps = fps), bitmap)
            }
        }
        image.close()
    }

    private fun ImageProxy.toBitmap(): Bitmap {
        val buffer = planes[0].buffer
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        bitmap.copyPixelsFromBuffer(java.nio.ByteBuffer.wrap(bytes))
        return bitmap
    }
}
