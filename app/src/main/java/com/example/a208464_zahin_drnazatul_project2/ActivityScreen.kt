package com.example.a208464_zahin_drnazatul_project2

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityScreen(
    viewModel: JobViewModel,
    onBack: () -> Unit,
    onHomeClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    val applications by viewModel.applications.collectAsState() // baris ni ambik data dari room

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("My Applications", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            JobBottomNav(
                current = "activity",
                onHome = onHomeClick,
                onActivity = {},
                onProfile = onProfileClick
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    label = "Applied",
                    count = applications.size.toString(),
                    color = MaterialTheme.colorScheme.primary
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    label = "Pending",
                    count = applications.count { it.status == "Pending Review" }.toString(),
                    color = Color(0xFFFF9800)
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    label = "Reviewed",
                    count = applications.count { it.status != "Pending Review" }.toString(),
                    color = Color(0xFF4CAF50)
                )
            }

            Spacer(Modifier.height(20.dp))

            Text(
                "APPLICATION HISTORY",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.secondary
            )
            Spacer(Modifier.height(12.dp))

            if (applications.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            modifier = Modifier.size(56.dp),
                            tint = MaterialTheme.colorScheme.outlineVariant
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(
                            "No applications yet.",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            "Start applying to jobs from the Home screen.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(
                        items = applications, //loop dan tunjuk setiap card
                        key = { it.id }
                    ) { app ->
                        // ★ tambah onDelete callback
                        ApplicationCard(
                            application = app,
                            onDelete = { viewModel.deleteApplication(app) }
                        )
                    }
                    item { Spacer(Modifier.height(8.dp)) }
                }
            }
        }
    }
}

@Composable
fun StatCard(modifier: Modifier = Modifier, label: String, count: String, color: Color) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                count,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = color
            )
            Text(
                label,
                style = MaterialTheme.typography.labelSmall,
                color = color
            )
        }
    }
}

// ★ tambah parameter onDelete
@Composable
fun ApplicationCard(
    application: ApplicationEntity,
    onDelete: () -> Unit
) {
    // ★ state untuk show/hide confirm dialog
    var showConfirm by remember { mutableStateOf(false) }

    // ★ confirm dialog sebelum delete
    if (showConfirm) {
        AlertDialog(
            onDismissRequest = { showConfirm = false },
            icon = {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = null,
                    tint = Color(0xFFE53935),
                    modifier = Modifier.size(32.dp)
                )
            },
            title = { Text("Delete Application?", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "Remove \"${application.jobTitle}\" at ${application.company} from your history? " +
                            "This cannot be undone.",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showConfirm = false
                        onDelete()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE53935)
                    )
                ) {
                    Text("Yes, Delete")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showConfirm = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(44.dp),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF4CAF50).copy(alpha = 0.12f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.Notifications,
                        contentDescription = null,
                        tint = Color(0xFF2E7D32),
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    application.jobTitle,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    application.company,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(Modifier.height(4.dp))
                Surface(
                    color = Color(0xFFFF9800).copy(alpha = 0.15f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        application.status,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFFE65100),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            // ★ tukar arrow icon kepada delete button
            IconButton(onClick = { showConfirm = true }) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete application",
                    tint = Color(0xFFE53935)
                )
            }
        }
    }
}