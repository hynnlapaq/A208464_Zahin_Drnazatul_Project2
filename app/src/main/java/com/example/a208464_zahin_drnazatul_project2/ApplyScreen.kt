package com.example.a208464_zahin_drnazatul_project2


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApplyScreen(
    viewModel: JobViewModel,
    onBack: () -> Unit,
    onSubmitSuccess: () -> Unit,
    onScanQr: () -> Unit
) {
    val job = viewModel.selectedJob ?: return

    var applicantName  by remember { mutableStateOf(viewModel.scannedApplicantName.ifEmpty { "Muhammad Zahin" }) }
    var applicantEmail by remember { mutableStateOf(viewModel.scannedApplicantEmail.ifEmpty { "zahin@email.com" }) }
    var coverNote    by remember { mutableStateOf("") }
    var showSuccess  by remember { mutableStateOf(false) }
    var nameError    by remember { mutableStateOf(false) }
    var emailError   by remember { mutableStateOf(false) }

    LaunchedEffect(viewModel.scannedApplicantName, viewModel.scannedApplicantEmail) {
        if (viewModel.scannedApplicantName.isNotEmpty()) applicantName  = viewModel.scannedApplicantName
        if (viewModel.scannedApplicantEmail.isNotEmpty()) applicantEmail = viewModel.scannedApplicantEmail
    }

    if (showSuccess) {
        AlertDialog(
            onDismissRequest = {},
            icon = { Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF4CAF50), modifier = Modifier.size(48.dp)) },
            title = { Text("Application Submitted!", fontWeight = FontWeight.Bold) },
            text  = {
                Column {
                    Text("Your application for \"${job.title}\" has been saved locally.")
                    Spacer(Modifier.height(8.dp))
                    // ── Show Cloud Sync Status ──
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Cloud: ${viewModel.firestoreStatus ?: "Waiting..."}",
                            modifier = Modifier.padding(8.dp),
                            style = MaterialTheme.typography.labelMedium,
                            color = if (viewModel.firestoreStatus?.contains("Failed") == true) Color.Red else Color.Unspecified
                        )
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showSuccess = false; onSubmitSuccess() }) {
                    Text("View My Applications")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Apply for Job", fontWeight = FontWeight.Bold) },
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
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
                shape = RoundedCornerShape(14.dp)
            ) {
                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(32.dp))
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(job.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                        Text("${job.company} · ${job.location}", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            Text("Your Information", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(14.dp))

            OutlinedTextField(
                value = applicantName,
                onValueChange = { applicantName = it; nameError = false },
                label = { Text("Full Name") },
                modifier = Modifier.fillMaxWidth(),
                isError = nameError,
                singleLine = true
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = applicantEmail,
                onValueChange = { applicantEmail = it; emailError = false },
                label = { Text("Email Address") },
                modifier = Modifier.fillMaxWidth(),
                isError = emailError,
                singleLine = true
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = coverNote,
                onValueChange = { coverNote = it },
                label = { Text("Cover Note (Optional)") },
                modifier = Modifier.fillMaxWidth().height(130.dp),
                maxLines = 5
            )

            Spacer(Modifier.height(32.dp))

            Button(
                onClick = {
                    nameError = applicantName.isBlank()
                    emailError = applicantEmail.isBlank() || !applicantEmail.contains("@")
                    if (!nameError && !emailError) {
                        viewModel.submitApplication(
                            ApplicationEntity(
                                jobTitle = job.title,
                                company = job.company,
                                applicantName = applicantName,
                                applicantEmail = applicantEmail,
                                coverNote = coverNote
                            )
                        )
                        showSuccess = true
                    }
                },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(14.dp),
                enabled = !viewModel.hasApplied(job.title)
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(if (viewModel.hasApplied(job.title)) "Already Applied" else "Submit Application")
            }
        }
    }
}
