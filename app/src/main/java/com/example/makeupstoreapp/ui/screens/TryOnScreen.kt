package com.example.makeupstoreapp.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.platform.LocalContext
import com.example.makeupstoreapp.presentation.data.ai.FaceLandmarkerHelper
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import com.example.makeupstoreapp.data.model.Product
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.OutlinedButton
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width


@Composable
fun TryOnScreen(
    product: Product,
    onBack: () -> Unit
) {

    val context = LocalContext.current

    val faceLandmarkerHelper = remember {
        FaceLandmarkerHelper(context)
    }

    var selectedImageUri by remember {
        mutableStateOf<Uri?>(null)
    }

    var selectedBitmap by remember {
        mutableStateOf<Bitmap?>(null)
    }

    var faceDetected by remember {
        mutableStateOf<Boolean?>(null)
    }

    var lipPoints by remember {
        mutableStateOf<List<com.google.mediapipe.tasks.components.containers.NormalizedLandmark>>(
            emptyList()
        )
    }
    var showAfter by remember {
        mutableStateOf(true)
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->

        selectedImageUri = uri

        if (uri != null) {

            val bitmap = uriToBitmap(context, uri)

            if (bitmap != null) {

                selectedBitmap = bitmap

                val result = faceLandmarkerHelper.detectFace(bitmap)

                faceDetected = result
                    ?.faceLandmarks()
                    ?.isNotEmpty() == true

                val faceLandmarks = result
                    ?.faceLandmarks()
                    ?.firstOrNull()

                if (faceLandmarks != null) {

                    val lipIndexes = listOf(
                        // contur exterior sus
                        61, 185, 40, 39, 37, 0, 267, 269, 270, 409, 291,

                        // contur exterior jos
                        291, 375, 321, 405, 314, 17, 84, 181, 91, 146, 61,

                        // contur interior sus
                        78, 191, 80, 81, 82, 13, 312, 311, 310, 415, 308,

                        // contur interior jos
                        308, 324, 318, 402, 317, 14, 87, 178, 88, 95, 78
                    )

                    lipPoints = lipIndexes.map { index ->
                        faceLandmarks[index]
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier.padding(16.dp)
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Înapoi"
                )
            }

            Text(
                text = "Probă virtuală",
                style = MaterialTheme.typography.titleLarge
            )
        }

        Text(
            text = "Produs: ${product.name}"
        )

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Nuanță: ${product.shadeName}"
            )

            Spacer(modifier = Modifier.width(8.dp))

            Canvas(
                modifier = Modifier.size(22.dp)
            ) {
                drawCircle(
                    color = parseColor(product.color)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                photoPickerLauncher.launch("image/*")
            }
        ) {
            Text("Alege o imagine")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (selectedImageUri != null) {

            selectedBitmap?.let { bitmap ->

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(350.dp),
                    contentAlignment = Alignment.Center
                ) {

                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Selected photo",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize()
                    )

                    Canvas(
                        modifier = Modifier.fillMaxSize()
                    ) {

                        val imageAspectRatio =
                            bitmap.width.toFloat() / bitmap.height.toFloat()

                        val boxAspectRatio =
                            size.width / size.height

                        val displayedWidth: Float
                        val displayedHeight: Float
                        val offsetX: Float
                        val offsetY: Float

                        if (imageAspectRatio > boxAspectRatio) {

                            displayedWidth = size.width
                            displayedHeight = size.width / imageAspectRatio

                            offsetX = 0f
                            offsetY = (size.height - displayedHeight) / 2f

                        } else {

                            displayedHeight = size.height
                            displayedWidth = size.height * imageAspectRatio

                            offsetX = (size.width - displayedWidth) / 2f
                            offsetY = 0f
                        }

                        if (showAfter && lipPoints.size == 44) {

                            val outerLipPoints = lipPoints.take(22)
                            val innerLipPoints = lipPoints.drop(22)

                            val lipPath = Path().apply {
                                fillType = PathFillType.EvenOdd
                            }

                            // Conturul exterior al buzelor
                            outerLipPoints.forEachIndexed { index, point ->

                                val x =
                                    offsetX + point.x() * displayedWidth

                                val y =
                                    offsetY + point.y() * displayedHeight

                                if (index == 0) {
                                    lipPath.moveTo(x, y)
                                } else {
                                    lipPath.lineTo(x, y)
                                }
                            }

                            lipPath.close()

                            // Conturul interior al gurii
                            innerLipPoints.forEachIndexed { index, point ->

                                val x =
                                    offsetX + point.x() * displayedWidth

                                val y =
                                    offsetY + point.y() * displayedHeight

                                if (index == 0) {
                                    lipPath.moveTo(x, y)
                                } else {
                                    lipPath.lineTo(x, y)
                                }
                            }

                            lipPath.close()

                            drawPath(
                                path = lipPath,
                                color = parseColor(product.color),
                                alpha = 0.45f
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    OutlinedButton(
                        onClick = {
                            showAfter = false
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Înainte")
                    }

                    Button(
                        onClick = {
                            showAfter = true
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("După")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (faceDetected) {

                true -> {
                    Text("Face detected successfully!")
                    Text("Lip points detected: ${lipPoints.size}")
                }

                false -> {
                    Text("No face detected.")
                }

                null -> {
                    Text("Waiting for face detection...")
                }
            }
        }
    }
}

fun uriToBitmap(
    context: android.content.Context,
    uri: Uri
): Bitmap? {

    return context.contentResolver
        .openInputStream(uri)
        ?.use { inputStream ->
            BitmapFactory.decodeStream(inputStream)
        }
}