package com.example.livetranslate

import android.content.ContentValues
import android.content.Context
import android.graphics.Point
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.Executors

/**
 * ViewModel to manage camera lifecycle, capture, and object detection results.
 */
class CameraViewModel : ViewModel() {

    // used to take picture on separate thread
    private val executor = Executors.newSingleThreadExecutor()

    // track selected camera (front or back)
    private val _cameraSelector = MutableStateFlow(CameraSelector.DEFAULT_BACK_CAMERA)
    val cameraSelector = _cameraSelector.asStateFlow()

    // SurfaceRequest for CameraXViewfinder
    private val _surfaceRequest = MutableStateFlow<SurfaceRequest?>(null)
    val surfaceRequest = _surfaceRequest.asStateFlow()

    // frame size for use in transforming coordinate
    private val _frameSize = MutableStateFlow(Point(0, 0))
    val frameSize = _frameSize.asStateFlow()

    // object detection results
    private val _detections = MutableStateFlow<List<DetectionResult>>(emptyList())
    val detections = _detections.asStateFlow()

    // analyzer instance
    private var objectAnalyzer: ObjectDetectionAnalyzer? = null

    /**
     * this function initializes the analyzer with the chosen detector (mlkit of efficientdet)
     */
    fun initAnalyzer(context: Context, detectorType: DetectorType) {
        objectAnalyzer = ObjectDetectionAnalyzer(
            context,
            executor
        ) { results ->
            _detections.value = results
        }
    }

    /**
     * this function switches detector at runtime, for user selection
     */
    fun switchDetector(detectorType: DetectorType, context: Context, activity: ComponentActivity) {
        // reinitialize analyzer with new detector
        initAnalyzer(context, detectorType)
        // restart preview with the new analyzer
        startPreview(detectorType, activity)
    }

    /**
     * this function toggles camera between back and front
     */
    fun toggleCamera(context: Context, activity: ComponentActivity, detectorType: DetectorType) {
        _cameraSelector.value =
            if (_cameraSelector.value == CameraSelector.DEFAULT_BACK_CAMERA) {
                CameraSelector.DEFAULT_FRONT_CAMERA
            } else {
                CameraSelector.DEFAULT_BACK_CAMERA
            }
        // Ensure analyzer is initialized
        if (objectAnalyzer == null) {
            initAnalyzer(context, detectorType)
        }
        startPreview(detectorType, activity)
    }

    /**
     * this function starts camera preview with ImageAnalysis attached
     */
    fun startPreview(detectorType: DetectorType, activity: ComponentActivity? = null) {

        val safeActivity = activity ?: return
        if (objectAnalyzer == null) {
            initAnalyzer(safeActivity, detectorType)
        }
        val cameraProviderFuture = ProcessCameraProvider.getInstance(safeActivity)

        //val cameraProviderFuture = ProcessCameraProvider.getInstance(activity!!)
        cameraProviderFuture.addListener({
            // get camera provider
            val cameraProvider = cameraProviderFuture.get()
            // create preview use case
            val previewUseCase = Preview.Builder().build().apply {
                setSurfaceProvider { request -> _surfaceRequest.value = request }
            }
            // use new ImageAnalysis from analyzer
            val analysisUseCase = objectAnalyzer!!.createImageAnalysis(detectorType)

            // initialize image capture to save img to
            imageCapture = ImageCapture.Builder().build()
            // bind both use cases to the camera
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                activity,
                _cameraSelector.value,
                previewUseCase,
                analysisUseCase,
                imageCapture
            )
        }, ContextCompat.getMainExecutor(activity)) // run on main thread
    }

    /**
     * stop camera preview
     */
    fun stopPreview() {
        _surfaceRequest.value = null
    }
    lateinit var imageCapture: ImageCapture


    /**
     * This function takes a photo and saves it to a remote server (A6Server)
     * Updated for A6 to work with remote server instead of local gallery
     */
    fun takePhoto(activity: ComponentActivity, onSaved: (uri: Uri?) -> Unit) {
        // name image to be saved
        val name = SimpleDateFormat("yyyyMMdd-HHmmss", Locale.US).format(System.currentTimeMillis())
        // save metadata for the image
        val outputOptions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, name)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/LiveTranslate")
            }
            ImageCapture.OutputFileOptions.Builder(
                activity.contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            ).build()
        } else {
            val file = File(activity.externalMediaDirs.first(), "$name.jpg")
            ImageCapture.OutputFileOptions.Builder(file).build()
        }
        // take the actual picture
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(activity),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    onSaved(output.savedUri)
                }
                override fun onError(exc: ImageCaptureException) {
                    Log.e("CameraVM", "Photo capture failed: ${exc.message}")
                    onSaved(null)
                }
            }
        )
    }
}


