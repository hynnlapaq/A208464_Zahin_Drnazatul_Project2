package com.example.a208464_zahin_drnazatul_project2


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    viewModel: JobViewModel,
    onJobClick: (JobPost) -> Unit,
    onActivityClick: () -> Unit,
    onProfileClick: () -> Unit,
    onNearbyClick: () -> Unit         // ★ NEW
) {
    Scaffold(
        bottomBar = {
            JobBottomNav(
                current    = "home",
                onHome     = {},
                onActivity = onActivityClick,
                onProfile  = onProfileClick
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            // ── Header ────────────────────────────────────────────────────────
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp, bottom = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Column {
                        Text("JobHub",
                            style = MaterialTheme.typography.displaySmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.ExtraBold)
                        SdgBadge()
                    }
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
                            .clickable { onProfileClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Z", color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.Bold)
                    }
                }
            }

            // ── SDG Mission Card ──────────────────────────────────────────────
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                    colors   = CardDefaults.cardColors(
                        containerColor = Color(0xFF1B5E20).copy(alpha = 0.08f)
                    ),
                    shape  = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp, Color(0xFF4CAF50).copy(alpha = 0.3f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier.size(44.dp),
                            shape    = RoundedCornerShape(12.dp),
                            color    = Color(0xFF4CAF50).copy(alpha = 0.15f)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Home, contentDescription = null,
                                    tint = Color(0xFF2E7D32), modifier = Modifier.size(24.dp))
                            }
                        }
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text("Good Morning, Muhammad Zahin",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold)
                            Text("Bridging employment gaps to end poverty in Malaysia.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            // ── ★ Jobs Near Me shortcut button ───────────────────────────────
            item {
                OutlinedButton(
                    onClick  = onNearbyClick,
                    modifier = Modifier.fillMaxWidth(),
                    shape    = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.LocationOn, contentDescription = null,
                        modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Jobs Near Me", fontWeight = FontWeight.SemiBold)
                }
                Spacer(Modifier.height(12.dp))
            }

            // ── Search Bar ────────────────────────────────────────────────────
            item {
                JobSearchBar(
                    text          = viewModel.searchQuery,
                    onValueChange = { viewModel.onSearchQueryChange(it) },
                    onSearch      = { viewModel.onSearchSubmit() }
                )
                Spacer(Modifier.height(16.dp))
            }

            // ── Section Label ─────────────────────────────────────────────────
            item {
                Text(
                    text = if (viewModel.displayedQuery.isEmpty()) "RECOMMENDED FOR YOU"
                    else "RESULTS FOR: \"${viewModel.displayedQuery.uppercase()}\"",
                    color = MaterialTheme.colorScheme.secondary,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            // ── Job Cards ─────────────────────────────────────────────────────
            val jobList = viewModel.filteredJobs
            if (jobList.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No jobs found. Try a different keyword.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            } else {
                items(jobList) { job ->
                    JobItemCard(
                        job            = job,
                        isApplied      = viewModel.hasApplied(job.title),
                        onDetailsClick = { onJobClick(job) }
                    )
                    Spacer(Modifier.height(14.dp))
                }
            }

            item { Spacer(Modifier.height(8.dp)) }
        }
    }
}
