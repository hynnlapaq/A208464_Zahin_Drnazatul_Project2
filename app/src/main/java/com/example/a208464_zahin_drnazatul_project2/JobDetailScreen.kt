package com.example.a208464_zahin_drnazatul_project2

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobDetailScreen(
    viewModel: JobViewModel,
    onBack: () -> Unit,
    onApplyClick: () -> Unit,
    onChatClick: () -> Unit
) {
    val job = viewModel.selectedJob ?: return

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Job Details", fontWeight = FontWeight.Bold) },
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
            // ── Company Banner ──
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .background(
                        MaterialTheme.colorScheme.primaryContainer,
                        RoundedCornerShape(20.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.AccountBox, // Changed from Business
                        modifier = Modifier.size(52.dp),
                        tint = MaterialTheme.colorScheme.primary,
                        contentDescription = null
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        job.company,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // ── Title & SDG Tag ──
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        job.title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        job.company,
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                if (viewModel.hasApplied(job.title)) {
                    Surface(
                        color = Color(0xFF4CAF50).copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            "✓ Applied",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF2E7D32),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            // ── Info Chips ──
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                DetailInfoChip(Icons.Default.LocationOn, job.location)
                DetailInfoChip(Icons.Default.ShoppingCart, job.salary) // Changed from Payments
            }
            Spacer(Modifier.height(8.dp))
            DetailInfoChip(Icons.Default.List, job.category) // Changed from Category

            Spacer(Modifier.height(20.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant) // Changed from Divider
            Spacer(Modifier.height(20.dp))

            // ── Description ──
            Text("About This Role", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            Text(
                job.description,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = MaterialTheme.typography.bodyMedium.lineHeight
            )

            Spacer(Modifier.height(20.dp))

            // ── Requirements ──
            Text("Requirements", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            Text(
                job.requirements,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(Modifier.height(20.dp))

            // ── SDG Alignment Note ──
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF1B5E20).copy(alpha = 0.07f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.Top) {
                    Icon(
                        Icons.Default.Star, // Changed from EmojiEvents
                        contentDescription = null,
                        tint = Color(0xFF388E3C),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "This job supports SDG Goal 1 – No Poverty, by providing stable employment opportunities for Malaysians from B40 communities.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF2E7D32)
                    )
                }
            }

            Spacer(Modifier.height(28.dp))

            // ── Action Buttons ──
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onChatClick,
                    modifier = Modifier.weight(1f).height(52.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, modifier = Modifier.size(18.dp)) // Changed from Chat
                    Spacer(Modifier.width(6.dp))
                    Text("Contact")
                }
                Button(
                    onClick = onApplyClick,
                    modifier = Modifier.weight(1f).height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    enabled = !viewModel.hasApplied(job.title)
                ) {
                    Text(if (viewModel.hasApplied(job.title)) "Applied ✓" else "Apply Now", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}