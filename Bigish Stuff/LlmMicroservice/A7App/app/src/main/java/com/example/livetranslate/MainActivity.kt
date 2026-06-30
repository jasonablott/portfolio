package com.example.livetranslate

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.compose.CameraXViewfinder
import androidx.camera.viewfinder.compose.MutableCoordinateTransformer
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.livetranslate.ui.theme.LiveTranslateTheme
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.ByteArrayOutputStream

// -----------------------------------------------------------------------------
// DATASTORE SETUP
// -----------------------------------------------------------------------------

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "login")
val tokenKey = stringPreferencesKey("token")
val usernameKey = stringPreferencesKey("username")

// -----------------------------------------------------------------------------
// DATA MODELS
// -----------------------------------------------------------------------------

@Serializable
data class LoginRequest(val username: String, val password: String)

@Serializable
data class AuthResponse(val token: String)

@Serializable
data class Photo(
    val id: String,
    val filename: String,
    val owner: String,
    val path: String,
    val uploadedAt: Long,
    var caption: String? = null // ADDED A7*****
)

// enum for camera recording state
enum class Recording(val state: String) { Start("Launch Camera"), Stop("Close Camera") }

// -----------------------------------------------------------------------------
// MAIN ACTIVITY
// -----------------------------------------------------------------------------

class MainActivity : ComponentActivity() {

    // Camera permission state
    private val _haveCameraPermissionsState = mutableStateOf(false)
    // detector type state
    private val _detectorTypeState = mutableStateOf(DetectorType.EFFICIENTDET)

    // HTTP client for server communication
    private val client by lazy {
        HttpClient(Android) {
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                        isLenient = true
                    }
                )
            }
        }
    }

    // -------------------------------------------------------------------------
    // PERMISSIONS
    // -------------------------------------------------------------------------

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            _haveCameraPermissionsState.value = granted
        }

    // helper to verify camera permissions as needed
    private fun verifyPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
        ) {
            _haveCameraPermissionsState.value = true
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    // -------------------------------------------------------------------------
    // ON CREATE
    // -------------------------------------------------------------------------

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        verifyPermissions()

        setContent {
            LiveTranslateTheme {

                // get login info from datastore
                val prefsFlow = dataStore.data
                val currentPrefs by prefsFlow.collectAsState(initial = null)
                val loggedIn = currentPrefs?.contains(tokenKey) == true

                // camera ViewModel and state
                val viewModel: CameraViewModel = viewModel()
                val surfaceRequest by viewModel.surfaceRequest.collectAsState()
                val detections by viewModel.detections.collectAsState()
                val detectorType by _detectorTypeState
                val scope = rememberCoroutineScope()

                // UI state
                var buttonState by remember { mutableStateOf(Recording.Start) }
                var photos by remember { mutableStateOf<List<Photo>>(emptyList()) }

                // load photos automatically when logged in
                LaunchedEffect(loggedIn) {
                    if (loggedIn) {
                        val token = currentPrefs?.get(tokenKey)
                        if (token != null) {
                            photos = fetchPhotos(token)

                            // ADDED FOR A7*****************
                            // Fetch captions from external llm
                            // fetch captions asynchronously for each photo
                            val photosWithCaptions = photos.map { photo ->
                                async {
                                    photo.copy(caption = fetchCaption("http://10.0.2.2:8080${photo.path}", token))
                                }
                            }.awaitAll()
                            photos = photosWithCaptions

                            // END ADDED FOR A7****************************

                        }
                    }
                }

                // -------------------------------------------------------------
                // UI STRUCTURE
                // -------------------------------------------------------------
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Surface(modifier = Modifier.padding(innerPadding)) {

                        // -----------------------------------------------------
                        // LOGIN SCREEN
                        // -----------------------------------------------------
                        if (!loggedIn) {
                            LoginScreen(
                                onLogin = { username, password ->
                                    try {
                                        val tokenResp = client.post("http://10.0.2.2:8080/api/auth") {
                                            contentType(ContentType.Application.Json)
                                            setBody(LoginRequest(username, password))
                                        }

                                        if (tokenResp.status == HttpStatusCode.OK) {
                                            val token: AuthResponse = tokenResp.body()
                                            dataStore.edit { prefs ->
                                                prefs[tokenKey] = token.token
                                                prefs[usernameKey] = username
                                            }
                                            true
                                        } else {
                                            Log.e("Login", "Login failed: ${tokenResp.status}")
                                            false
                                        }
                                    } catch (e: Exception) {
                                        Log.e("Login", "Error logging in: $e")
                                        false
                                    }
                                },

                                onSignUp = { username, password ->
                                    try {
                                        val resp = client.post("http://10.0.2.2:8080/api/user") {
                                            contentType(ContentType.Application.Json)
                                            setBody(LoginRequest(username, password))
                                        }

                                        if (resp.status == HttpStatusCode.Created) {
                                            val tokenResp = client.post("http://10.0.2.2:8080/api/auth") {
                                                contentType(ContentType.Application.Json)
                                                setBody(LoginRequest(username, password))
                                            }

                                            val token: AuthResponse = tokenResp.body()
                                            dataStore.edit { prefs ->
                                                prefs[tokenKey] = token.token
                                                prefs[usernameKey] = username
                                            }
                                            true
                                        } else false
                                    } catch (e: Exception) {
                                        Log.e("SignUp", "Error signing up: $e")
                                        false
                                    }
                                }
                            )
                        }

                        // -----------------------------------------------------
                        // MAIN APP SCREEN (shown when logged in)
                        // -----------------------------------------------------
                        else {
                            Column(modifier = Modifier.padding(8.dp)) {

                                // User greeting
                                Text("Hello ${currentPrefs!![usernameKey]}!")

                                // LOGOUT Button
                                Button(
                                    onClick = {
                                        scope.launch {
                                            dataStore.edit {
                                                it.remove(tokenKey)
                                                it.remove(usernameKey)
                                            }
                                        }
                                    }
                                ) {
                                    Text("Log out")
                                }

                                Spacer(Modifier.height(8.dp))

                                // -----------------------------
                                // CAMERA SECTION
                                // -----------------------------
                                CameraControls(
                                    viewModel = viewModel,
                                    detectorType = detectorType,
                                    buttonState = buttonState,
                                    haveCameraPermissions = _haveCameraPermissionsState.value,

                                    // Capture callback
                                    onCapture = { uri ->
                                        scope.launch {
                                            val token = currentPrefs!![tokenKey]!!
                                            uploadCapturedPhoto(uri, token)
                                            //photos = fetchPhotos(token)
                                            val updatedPhotos = fetchPhotos(token)

                                            // Fetch captions for newly uploaded photos
                                            val photosWithCaptions = updatedPhotos.map { photo ->
                                                async {
                                                    photo.copy(caption = fetchCaption("http://10.0.2.2:8080${photo.path}", token))
                                                }
                                            }.awaitAll()

                                            photos = photosWithCaptions
                                        }
                                    },

                                    // Switch detector type
                                    onSwitchDetector = {
                                        val newType =
                                            if (detectorType == DetectorType.EFFICIENTDET)
                                                DetectorType.MLKIT
                                            else
                                                DetectorType.EFFICIENTDET
                                        _detectorTypeState.value = newType
                                        viewModel.switchDetector(
                                            newType,
                                            this@MainActivity,
                                            this@MainActivity
                                        )
                                    },

                                    // Start/stop camera preview
                                    onToggleRecording = {
                                        buttonState = if (buttonState == Recording.Start) {
                                            viewModel.startPreview(detectorType, this@MainActivity)
                                            Recording.Stop
                                        } else {
                                            viewModel.stopPreview()
                                            Recording.Start
                                        }
                                    },

                                    surfaceRequest = surfaceRequest,
                                    detections = detections
                                )

                                Spacer(Modifier.height(16.dp))

                                // -----------------------------
                                // PHOTOS SECTION
                                // -----------------------------
                                Text(
                                    "Your Photos:",
                                    style = MaterialTheme.typography.titleMedium
                                )

                                val token = currentPrefs?.get(tokenKey) // get token to authenticate

                                LazyColumn {
                                    items(photos) { photo ->
                                        AsyncImage( // utilize coil to load images
                                            model = ImageRequest.Builder(LocalContext.current)
                                                .data("http://10.0.2.2:8080${photo.path}")
                                                .apply {
                                                    token?.let {
                                                        addHeader("Authorization", "Bearer $it")
                                                    }
                                                }
                                                .allowHardware(false)
                                                .build(),
                                            contentDescription = photo.filename,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(180.dp)
                                                .padding(4.dp)
                                        )

                                        // ADDED FOR A7***************
                                        // Display image caption from external llm
                                        photo.caption?.let { caption ->
                                            Text(
                                                caption,
                                                style = MaterialTheme.typography.bodyMedium,
                                                modifier = Modifier.padding(top = 4.dp)
                                            )
                                        }
                                        // END ADDED FOR A7***********************

                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // -------------------------------------------------------------------------
    // NETWORK HELPERS
    // -------------------------------------------------------------------------

    // Fetch user's photos
    private suspend fun fetchPhotos(token: String): List<Photo> = try {
        client.get("http://10.0.2.2:8080/api/photos") {
            bearerAuth(token)
        }.body()
    } catch (e: Exception) {
        Log.e("fetchPhotos", "Error fetching photos: $e")
        emptyList()
    }

    // Upload photo with orientation correction
    private suspend fun uploadCapturedPhoto(uri: Uri?, token: String) {
        if (uri == null) return

        try {
            // Read EXIF to correct rotation
            val inputStream = contentResolver.openInputStream(uri) ?: return
            val exif = androidx.exifinterface.media.ExifInterface(inputStream)
            inputStream.close()

            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
            val orientation = exif.getAttributeInt(
                androidx.exifinterface.media.ExifInterface.TAG_ORIENTATION,
                androidx.exifinterface.media.ExifInterface.ORIENTATION_NORMAL
            )

            val rotatedBitmap = when (orientation) {
                androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_90 ->
                    Bitmap.createBitmap(
                        bitmap, 0, 0, bitmap.width, bitmap.height,
                        android.graphics.Matrix().apply { postRotate(90f) }, true
                    )
                androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_180 ->
                    Bitmap.createBitmap(
                        bitmap, 0, 0, bitmap.width, bitmap.height,
                        android.graphics.Matrix().apply { postRotate(180f) }, true
                    )
                androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_270 ->
                    Bitmap.createBitmap(
                        bitmap, 0, 0, bitmap.width, bitmap.height,
                        android.graphics.Matrix().apply { postRotate(270f) }, true
                    )
                else -> bitmap
            }

            // Compress image
            val baos = ByteArrayOutputStream()
            rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)

            // Upload via multipart POST
            val response = client.post("http://10.0.2.2:8080/api/photos") {
                bearerAuth(token)
                setBody(
                    MultiPartFormDataContent(
                        formData {
                            append(
                                "file",
                                baos.toByteArray(),
                                Headers.build {
                                    append(HttpHeaders.ContentType, "image/jpeg")
                                    append(
                                        HttpHeaders.ContentDisposition,
                                        "form-data; name=\"file\"; filename=\"photo.jpg\""
                                    )
                                }
                            )
                        }
                    )
                )
            }

            Log.d("upload", "Upload status: ${response.status}")
            Log.d("upload", "Upload body: ${response.bodyAsText()}")
        } catch (e: Exception) {
            Log.e("upload", "Upload failed: $e")
        }
    }
    // ADDED FOR A7********
    private suspend fun fetchCaption(imageUrl: String, token: String): String? {
        return try {
            // fetch the image bytes
            val imageBytes: ByteArray = client.get(imageUrl).body()
            // call the captioning service
            val response: Map<String, String> = client.post("http://10.0.2.2:8080/api/analyze") {
                bearerAuth(token)
                setBody(MultiPartFormDataContent(formData {
                    append("image", imageBytes, Headers.build {
                        append(HttpHeaders.ContentType, "image/jpeg")
                        append(HttpHeaders.ContentDisposition, "form-data; name=\"image\"; filename=\"photo.jpg\"")
                    })
                }))
            }.body()

            Log.d("fetchCaption", "ML response: $response")

            response["caption"]?: "No caption returned"

        } catch (e: Exception) {
            Log.e("fetchCaption", "Failed to fetch caption: $e")
            null
        }
    }
    // END ADDED FOR A7*************
}

// -----------------------------------------------------------------------------
// COMPOSABLES
// -----------------------------------------------------------------------------

// Login screen composable
@Composable
fun LoginScreen(
    onLogin: suspend (String, String) -> Boolean,
    onSignUp: suspend (String, String) -> Boolean
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") }
        )

        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation()
        )

        Row {
            Button(
                onClick = {
                    scope.launch {
                        val success = onLogin(username, password)
                        Toast.makeText(
                            context,
                            if (success) "Logged in!" else "Login failed",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                modifier = Modifier.padding(8.dp)
            ) {
                Text("Log in")
            }

            Button(
                onClick = {
                    scope.launch {
                        val success = onSignUp(username, password)
                        Toast.makeText(
                            context,
                            if (success) "Signed up!" else "Sign up failed",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                modifier = Modifier.padding(8.dp)
            ) {
                Text("Sign up")
            }
        }
    }
}

// Camera controls composable (has object detection box display code)
@Composable
fun CameraControls(
    viewModel: CameraViewModel,
    detectorType: DetectorType,
    buttonState: Recording,
    haveCameraPermissions: Boolean,
    onCapture: (Uri?) -> Unit,
    onSwitchDetector: () -> Unit,
    onToggleRecording: () -> Unit,
    surfaceRequest: androidx.camera.core.SurfaceRequest?,
    detections: List<DetectionResult>
) {
    if (!haveCameraPermissions) {
        Text("Camera permission required.")
        return
    }

    val context = LocalContext.current
    val activity = context as? ComponentActivity

    Column {
        Button(onClick = onToggleRecording) { Text(buttonState.state) }
        Spacer(Modifier.height(8.dp))
        Button(onClick = onSwitchDetector) { Text("Switch Detector (${detectorType.name})") }
        Spacer(Modifier.height(8.dp))
        Button(onClick = {
            activity?.let { viewModel.takePhoto(it, onCapture) }
        }) {
            Text("Capture")
        }

        Button(onClick = {
            activity?.let { viewModel.toggleCamera(context, it, detectorType) }
        }) {
            Text("Switch Camera")
        }

        surfaceRequest?.let { req ->
            val transformer = remember { MutableCoordinateTransformer() }

            Box(modifier = Modifier.fillMaxSize()) {
                // Live camera feed
                CameraXViewfinder(
                    surfaceRequest = req,
                    coordinateTransformer = transformer,
                    modifier = Modifier.fillMaxSize()
                )

                // Draw bounding boxes and labels for detections
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val bufferToUiTransformMatrix = Matrix().apply {
                        setFrom(transformer.transformMatrix)
                        invert()
                    }

                    detections.forEach { det ->
                        val rect = androidx.compose.ui.geometry.Rect(
                            det.boundingBox.left,
                            det.boundingBox.top,
                            det.boundingBox.right,
                            det.boundingBox.bottom
                        )

                        val uiRect = bufferToUiTransformMatrix.map(rect)

                        // Draw bounding box
                        drawRect(
                            color = Color.Red,
                            topLeft = Offset(uiRect.left, uiRect.top),
                            size = androidx.compose.ui.geometry.Size(
                                uiRect.width,
                                uiRect.height
                            ),
                            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 4f)
                        )

                        // Draw label text
                        drawContext.canvas.nativeCanvas.drawText(
                            "${det.label} ${(det.score * 100).toInt()}%",
                            uiRect.left,
                            uiRect.top - 8,
                            android.graphics.Paint().apply {
                                color = android.graphics.Color.RED
                                textSize = 36f
                                isFakeBoldText = true
                            }
                        )
                    }
                }
            }
        }
    }
}
