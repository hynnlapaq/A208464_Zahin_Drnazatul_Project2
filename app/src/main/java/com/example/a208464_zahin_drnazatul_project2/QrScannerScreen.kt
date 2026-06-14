package com.example.a208464_zahin_drnazatul_project2

import android.Manifest
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.util.concurrent.Executors

@kotlin.OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun QrScannerScreen(
    viewModel: JobViewModel,
    onBack: () -> Unit,
    onScanSuccess: () -> Unit
) {
    val context = LocalContext.current
    val cameraPermission = rememberPermissionState(Manifest.permission.CAMERA)
    
    var hasScanned by remember { mutableStateOf(false) }
    var isProcessingGallery by remember { mutableStateOf(false) }

    // Gallery Picker Launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            isProcessingGallery = true
            try {
                val image = InputImage.fromFilePath(context, uri)
                val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
                
                recognizer.process(image)
                    .addOnSuccessListener { visionText ->
                        if (visionText.text.isNotBlank()) {
                            viewModel.onTextRecognized(visionText.text)
                            hasScanned = true
                        }
                    }
                    .addOnCompleteListener { isProcessingGallery = false }
            } catch (e: Exception) {
                isProcessingGallery = false
                e.printStackTrace()
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Scan ID or QR", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(16.dp))

            // Guidance Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Info, null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(12.dp))
                    Text(
                        "Scan your IC card for auto-fill or use a Resume QR code. You can also pick a clear photo from your gallery.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            if (isProcessingGallery) {
                Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(Modifier.height(16.dp))
                        Text("Reading details from image...")
                    }
                }
            } else if (hasScanned) {
                // Results View
                Box(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF4CAF50), modifier = Modifier.size(64.dp))
                        Spacer(Modifier.height(12.dp))
                        Text("Captured Successfully!", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        
                        Spacer(Modifier.height(16.dp))
                        
                        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                            Column(Modifier.padding(16.dp)) {
                                if (viewModel.scannedApplicantName.isNotEmpty()) {
                                    Text("Name", style = MaterialTheme.typography.labelSmall)
                                    Text(viewModel.scannedApplicantName, fontWeight = FontWeight.Bold)
                                    Spacer(Modifier.height(8.dp))
                                }
                                if (viewModel.scannedApplicantId.isNotEmpty()) {
                                    Text("IC Number", style = MaterialTheme.typography.labelSmall)
                                    Text(viewModel.scannedApplicantId, fontWeight = FontWeight.Bold)
                                    Spacer(Modifier.height(8.dp))
                                }
                                if (viewModel.scannedApplicantEmail.isNotEmpty()) {
                                    Text("Email", style = MaterialTheme.typography.labelSmall)
                                    Text(viewModel.scannedApplicantEmail, fontWeight = FontWeight.Bold)
                                }
                                if (viewModel.scannedApplicantName.isEmpty() && viewModel.scannedApplicantId.isEmpty()) {
                                    Text("Recognized Text", style = MaterialTheme.typography.labelSmall)
                                    Text(viewModel.scannedData?.take(150) ?: "No text found", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                        
                        Spacer(Modifier.height(24.dp))
                        Button(onClick = { onScanSuccess() }, modifier = Modifier.fillMaxWidth()) {
                            Text("Confirm & Auto-fill")
                        }
                        TextButton(onClick = { hasScanned = false; viewModel.clearScan() }) {
                            Text("Try Again")
                        }
                    }
                }
            } else if (!cameraPermission.status.isGranted) {
                Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Camera access is required to scan documents.")
                        Spacer(Modifier.height(16.dp))
                        Button(onClick = { cameraPermission.launchPermissionRequest() }) {
                            Text("Allow Camera")
                        }
                    }
                }
            } else {
                // Live Camera View
                Box(modifier = Modifier.fillMaxWidth().weight(1f).padding(horizontal = 20.dp)) {
                    CameraAndOcrView(
                        modifier = Modifier.fillMaxSize(),
                        viewModel = viewModel,
                        onDataDetected = { hasScanned = true }
                    )
                    
                    // Scanning Box UI Overlay
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .height(220.dp)
                            .align(Alignment.Center)
                            .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp))
                            .background(Color.Black.copy(alpha = 0.1f))
                    )
                    
                    Text(
                        "Align IC Card or QR here",
                        modifier = Modifier.align(Alignment.Center).padding(top = 260.dp),
                        color = Color.White,
                        style = MaterialTheme.typography.labelLarge,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(Modifier.height(20.dp))
            OutlinedButton(
                onClick = { galleryLauncher.launch("image/*") },
                modifier = Modifier.padding(bottom = 32.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.PhotoLibrary, null)
                Spacer(Modifier.width(8.dp))
                Text("Upload from Gallery")
            }
        }
    }
}

@Composable
private fun CameraAndOcrView(
    modifier: Modifier = Modifier,
    viewModel: JobViewModel,
    onDataDetected: () -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val executor = remember { Executors.newSingleThreadExecutor() }

    DisposableEffect(Unit) {
        onDispose { executor.shutdown() }
    }

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            val previewView = PreviewView(ctx)
            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

                val barcodeScanner = BarcodeScanning.getClient()
                val textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

                val imageAnalysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()

                imageAnalysis.setAnalyzer(executor) { imageProxy ->
                    processImageProxy(imageProxy, barcodeScanner, textRecognizer, viewModel, onDataDetected)
                }

                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        preview,
                        imageAnalysis
                    )
                } catch (e: Exception) { e.printStackTrace() }
            }, ContextCompat.getMainExecutor(ctx))
            previewView
        }
    )
}

@OptIn(ExperimentalGetImage::class)
private fun processImageProxy(
    imageProxy: ImageProxy,
    barcodeScanner: com.google.mlkit.vision.barcode.BarcodeScanner,
    textRecognizer: com.google.mlkit.vision.text.TextRecognizer,
    viewModel: JobViewModel,
    onDataDetected: () -> Unit
) {
    val mediaImage = imageProxy.image
    if (mediaImage != null) {
        val inputImage = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
        

        barcodeScanner.process(inputImage)
            .addOnSuccessListener { barcodes ->
                val qr = barcodes.firstOrNull { it.rawValue != null }?.rawValue
                if (qr != null) {
                    viewModel.onQrScanned(qr)
                    onDataDetected()
                } else {

                    textRecognizer.process(inputImage)
                        .addOnSuccessListener { visionText ->
                            // Check for Malaysia IC format: 000000-00-0000
                            if (visionText.text.contains(Regex("\\d{6}-\\d{2}-\\d{4}"))) {
                                viewModel.onTextRecognized(visionText.text)
                                onDataDetected()
                            }
                        }
                }
            }
            .addOnCompleteListener { imageProxy.close() }
    } else {
        imageProxy.close()
    }
}
