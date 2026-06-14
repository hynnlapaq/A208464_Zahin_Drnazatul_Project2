package com.example.a208464_zahin_drnazatul_project2

import android.Manifest
import android.annotation.SuppressLint
import android.location.Geocoder
import android.location.Location
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

@SuppressLint("MissingPermission")
@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun NearbyJobsScreen(
    viewModel: JobViewModel,
    onBack: () -> Unit,
    onJobClick: (JobPost) -> Unit
) {
    val context       = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val fusedLocation  = remember { LocationServices.getFusedLocationProviderClient(context) }

    // Request both Fine and Coarse location (Recommended for Android 12+)
    val locationPermissions = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    var isLocating by remember { mutableStateOf(false) }
    var locationLabel by remember { mutableStateOf<String?>(null) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    fun processLocation(location: Location) {
        viewModel.onLocationReceived(location)
        coroutineScope.launch {
            try {
                val city = withContext(Dispatchers.IO) {
                    val geocoder = Geocoder(context, Locale.getDefault())
                    @Suppress("DEPRECATION")
                    val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                    addresses?.firstOrNull()?.locality 
                        ?: addresses?.firstOrNull()?.adminArea 
                        ?: "Unknown City"
                }
                locationLabel = "$city (${String.format("%.4f", location.latitude)}, ${String.format("%.4f", location.longitude)})"
                viewModel.onCityResolved(city)
            } catch (e: Exception) {
                errorMsg = "Geocoding failed: ${e.message}"
            } finally {
                isLocating = false
            }
        }
    }

    fun fetchLocation() {
        isLocating = true
        errorMsg   = null
        
        fusedLocation.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                processLocation(location)
            } else {
                fusedLocation.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                    .addOnSuccessListener { freshLocation ->
                        if (freshLocation != null) {
                            processLocation(freshLocation)
                        } else {
                            errorMsg = "Could not detect location. Is GPS enabled?"
                            isLocating = false
                        }
                    }
                    .addOnFailureListener {
                        errorMsg = "Location error: ${it.message}"
                        isLocating = false
                    }
            }
        }.addOnFailureListener {
            errorMsg = "Location error: ${it.message}"
            isLocating = false
        }
    }

    val nearbyJobs = viewModel.nearbyJobs

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Jobs Near Me", fontWeight = FontWeight.Bold) },
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
                .padding(padding)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(16.dp))

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF1A237E).copy(alpha = 0.07f)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "GPS Location Sensor",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleSmall
                            )
                            Text(
                                if (locationLabel != null) locationLabel!!
                                else "Detecting location allows us to find local jobs",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    // Check if all requested permissions are granted
                    val allGranted = locationPermissions.permissions.all { it.status.isGranted }

                    if (!allGranted) {
                        Button(
                            onClick = { locationPermissions.launchMultiplePermissionRequest() },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Grant Location Permissions")
                        }
                    } else {
                        Button(
                            onClick  = { fetchLocation() },
                            modifier = Modifier.fillMaxWidth(),
                            shape    = RoundedCornerShape(12.dp),
                            enabled  = !isLocating
                        ) {
                            if (isLocating) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                                Spacer(Modifier.width(8.dp))
                                Text("Detecting...")
                            } else {
                                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(8.dp))
                                Text(if (locationLabel == null) "Detect My Location" else "Refresh Location")
                            }
                        }
                    }

                    if (errorMsg != null) {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            errorMsg!!,
                            color    = MaterialTheme.colorScheme.error,
                            style    = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            val label = if (viewModel.userCity != null) "JOBS IN ${viewModel.userCity!!.uppercase()}" else "ALL AVAILABLE JOBS"
            Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.secondary)
            Spacer(Modifier.height(12.dp))

            if (viewModel.userCity != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1B5E20).copy(alpha = 0.06f)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFF388E3C), modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(
                            "Showing ${nearbyJobs.size} local job(s) — supporting SDG 1 goals.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF2E7D32)
                        )
                    }
                }
                Spacer(Modifier.height(12.dp))
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(nearbyJobs) { job ->
                    JobItemCard(
                        job            = job,
                        isApplied      = viewModel.hasApplied(job.title),
                        onDetailsClick = { onJobClick(job) }
                    )
                }
            }
        }
    }
}
