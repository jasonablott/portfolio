package com.example.livetranslate

import android.content.Context
import android.graphics.Bitmap
import android.graphics.RectF
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.example.livetranslate.ml.EfficientDetModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.ObjectDetector
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.Executor
import org.tensorflow.lite.support.image.TensorImage

/**
 * This class supports both ML Kit and EfficientDet Lite4 model. It converts the imageProxy ->
 * Bitmap -> TensorImage, and returns the DetectionResult list via StateFlow and callback
 */
class ObjectDetectionAnalyzer(
    private val context: Context,
    private val executor: Executor,
    private val onDetections: (List<DetectionResult>) -> Unit
) {

    // public detection state to store results from model
    private val _detections = MutableStateFlow<List<DetectionResult>>(emptyList())
    val detections = _detections.asStateFlow()

    // result confidence threshold for displaying bounding boxes
    private val confidenceThreshold = 0.3f

    // EfficientDet model initialization
    val efficientDetModel by lazy { EfficientDetModel.newInstance(context) }

    // ML Kit model initialization
    private val mlKitOptions = ObjectDetectorOptions.Builder()
        .setDetectorMode(ObjectDetectorOptions.STREAM_MODE)
        .enableClassification()
        .build()
    private val mlKitDetector: ObjectDetector = ObjectDetection.getClient(mlKitOptions)

    // create ImageAnalysis use case for CameraX
    fun createImageAnalysis(detectorType: DetectorType): ImageAnalysis {
        return ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .setOutputImageRotationEnabled(false)
            .build()
            .apply {
                setAnalyzer(executor) { imageProxy ->
                    val bitmap = imageProxy.toBitmap()

                    // Route to the correct detector
                    when (detectorType) {
                        DetectorType.EFFICIENTDET -> analyzeEfficientDet(bitmap)
                        DetectorType.MLKIT -> analyzeMLKit(bitmap)
                    }

                    imageProxy.close()
                }
            }
    }

    // EfficientDet Lite4 analysis
    private fun analyzeEfficientDet(bitmap: Bitmap) {
        // Convert Bitmap to TensorImage
        val tensorImage = TensorImage.fromBitmap(bitmap)
        // run the model
        val outputs = efficientDetModel.process(tensorImage)
        // Map model outputs to DetectionResult
        val detectionList = outputs.detectionResultList.map { det ->
            DetectionResult(
                boundingBox = det.locationAsRectF,          // bounding box for detected object
                label = det.categoryAsString,               // object label
                score = det.scoreAsFloat                    // detection confidence
            )
        }
            .filter { it.score > confidenceThreshold }     // filter out low-confidence detections

        // update StateFlow and callback
        _detections.value = detectionList
        onDetections(detectionList)
    }

    // ML Kit analysis
    private fun analyzeMLKit(bitmap: Bitmap) {
        // Convert Bitmap to ML Kit InputImage (assuming no rotation here)
        val image = InputImage.fromBitmap(bitmap, 0)
        // run the model
        mlKitDetector.process(image)
            .addOnSuccessListener { detectedObjects ->
                // Map model outputs to DetectionResult
                val detectionList = detectedObjects.map { obj ->
                    DetectionResult(
                        boundingBox = RectF(obj.boundingBox),               // bounding box for detected object
                        label = obj.labels.firstOrNull()?.text ?: "Unknown",    // object label
                        score = obj.labels.firstOrNull()?.confidence ?: 0f      // detection confidence
                    )
                }
                    .filter { it.score > confidenceThreshold }                  // Filter out low-confidence detections

                // update StateFlow and callback
                _detections.value = detectionList
                onDetections(detectionList)
            }
            // handle failure scenario
            .addOnFailureListener { _ ->
                _detections.value = emptyList()
                onDetections(emptyList())
            }
    }

    // helper to convert ImageProxy -> Bitmap
    private fun ImageProxy.toBitmap(): Bitmap {
        val buffer = planes[0].buffer
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)
        return Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            .apply { copyPixelsFromBuffer(java.nio.ByteBuffer.wrap(bytes)) }
    }
}

/**
 * Data class for detection results
 */
data class DetectionResult(
    val boundingBox: RectF,
    val label: String,
    val score: Float
)

/**
 * Enum for selecting detection method
 */
enum class DetectorType { MLKIT, EFFICIENTDET }
