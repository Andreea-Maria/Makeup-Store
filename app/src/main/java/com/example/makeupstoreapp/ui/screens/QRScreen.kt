package com.example.makeupstoreapp.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.makeupstoreapp.viewmodel.CartViewModel
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import androidx.camera.core.ExperimentalGetImage
import androidx.compose.foundation.border
import android.os.VibrationEffect
import android.os.Vibrator
import android.content.Context

@Composable
fun QRScreen(
    cartViewModel: CartViewModel
) {
    var code by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var showScanner by remember { mutableStateOf(false) }


    val context = LocalContext.current

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
        if (granted) showScanner = true
        else message = "Permisiunea pentru cameră a fost refuzată."
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Reduceri QR",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                val permissionCheck = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.CAMERA
                )

                if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                    showScanner = true
                } else {
                    permissionLauncher.launch(Manifest.permission.CAMERA)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
        ) {
            Icon(Icons.Default.QrCodeScanner, contentDescription = "QR")
            Spacer(Modifier.width(8.dp))
            Text("Scanează cod QR")
        }

        Spacer(Modifier.height(20.dp))

        if (showScanner) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(340.dp)
            ) {
                QRScannerView(
                    onCodeScanned = { scannedCode ->
                        showScanner = false
                        code = scannedCode

                        cartViewModel.applyDiscount(scannedCode) { success ->
                            message = if (success) {
                                "Reducerea a fost aplicată!"
                            } else {
                                "Cod invalid sau inactiv"
                            }
                        }
                    }
                )

                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(220.dp)
                        .border(
                            width = 3.dp,
                            color = primaryColor,
                            shape = RoundedCornerShape(24.dp)
                        )
                )

                Button(
                    onClick = { showScanner = false },
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = primaryColor
                    )
                ) {
                    Text("Înapoi")
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        Text(
            text = "Sau introdu codul manual",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = code,
            onValueChange = { code = it },
            label = { Text("Cod reducere") },
            placeholder = { Text("Ex: MAKEUP20") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        )

        Spacer(Modifier.height(12.dp))

        Button(
            onClick = {
                cartViewModel.applyDiscount(code) { success ->
                    message = if (success) {
                        "Reducerea a fost aplicată!"
                    } else {
                        "Cod invalid sau inactiv"
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
        ) {
            Text("Aplică reducerea")
        }

        if (message.isNotEmpty()) {
            Spacer(Modifier.height(16.dp))
            Text(message)
        }
    }
}
@androidx.annotation.OptIn(ExperimentalGetImage::class)
@Composable
fun QRScannerView(
    onCodeScanned: (String) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var scanned by remember { mutableStateOf(false) }

    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .height(320.dp),
        factory = { ctx ->
            val previewView = PreviewView(ctx)

            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()

                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

                val scanner = BarcodeScanning.getClient()

                val analysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()

                analysis.setAnalyzer(ContextCompat.getMainExecutor(ctx)) { imageProxy ->
                    val mediaImage = imageProxy.image

                    if (mediaImage != null && !scanned) {
                        val image = InputImage.fromMediaImage(
                            mediaImage,
                            imageProxy.imageInfo.rotationDegrees
                        )

                        scanner.process(image)
                            .addOnSuccessListener { barcodes ->
                                val value = barcodes.firstOrNull()?.rawValue

                                if (!value.isNullOrBlank() && !scanned) {
                                    scanned = true

                                    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                                    vibrator.vibrate(
                                        VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE)
                                    )

                                    onCodeScanned(value)
                                }
                            }
                            .addOnCompleteListener {
                                imageProxy.close()
                            }
                    } else {
                        imageProxy.close()
                    }
                }

                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        preview,
                        analysis
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, ContextCompat.getMainExecutor(ctx))

            previewView
        }
    )
}