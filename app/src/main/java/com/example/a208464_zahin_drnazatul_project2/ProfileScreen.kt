package com.example.a208464_zahin_drnazatul_project2


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: JobViewModel,
    onBack: () -> Unit,
    onHomeClick: () -> Unit,
    onActivityClick: () -> Unit
) {
    val applications by viewModel.applications.collectAsState()

    // Fetch country stats when screen opens
    LaunchedEffect(Unit) {
        if (viewModel.countryStatsState is ApiState.Idle) {
            viewModel.fetchMalaysiaStats()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Profile", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            JobBottomNav(
                current    = "profile",
                onHome     = onHomeClick,
                onActivity = onActivityClick,
                onProfile  = {}
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ── Profile Header ────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Surface(
                        modifier = Modifier.size(80.dp),
                        shape    = CircleShape,
                        color    = Color.White
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("Z", fontSize = 36.sp, fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary)
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Text("Muhammad Zahin", color = Color.White,
                        style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text("ID: A208464", color = Color.White.copy(alpha = 0.8f),
                        style = MaterialTheme.typography.bodySmall)
                }
            }

            Spacer(Modifier.height(16.dp))
            SdgBadge()
            Spacer(Modifier.height(20.dp))

            // ── Activity Summary ──────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ProfileStatCard(Modifier.weight(1f), "Applications", applications.size.toString())
                ProfileStatCard(Modifier.weight(1f), "Jobs",         viewModel.jobs.size.toString())
                ProfileStatCard(Modifier.weight(1f), "Chats",
                    viewModel.chatMessages.values.sumOf { it.size }.toString())
            }

            Spacer(Modifier.height(24.dp))

            // ── ★ Malaysia Country Stats (REST API) ───────────────────────────
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Text("MALAYSIA EMPLOYMENT CONTEXT",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(bottom = 8.dp))

                when (val state = viewModel.countryStatsState) {
                    is ApiState.Loading -> {
                        Card(shape = RoundedCornerShape(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                                Spacer(Modifier.width(12.dp))
                                Text("Fetching live data from REST Countries API...",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }

                    is ApiState.Success -> {
                        val stats = state.data
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF1A237E).copy(alpha = 0.06f)
                            ),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Info, contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp))
                                    Spacer(Modifier.width(8.dp))
                                    Text("Live: Malaysia National Data",
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.titleSmall,
                                        color = MaterialTheme.colorScheme.primary)
                                    Spacer(Modifier.weight(1f))
                                    Surface(
                                        color = Color(0xFF4CAF50).copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text("API ✓",
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                                    }
                                }
                                Spacer(Modifier.height(12.dp))
                                CountryStatRow("Population",  stats.population)
                                CountryStatRow("Capital",     stats.capital)
                                CountryStatRow("Currency",    stats.currency)
                                CountryStatRow("Region",      stats.region)
                                CountryStatRow("Area",        stats.area)
                                CountryStatRow("Languages",   stats.languages)
                                CountryStatRow("Gini Index",  stats.latestGini)
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    "Gini index measures income inequality (0 = perfect equality, 100 = extreme inequality). " +
                                            "This context supports SDG 1 — understanding poverty levels guides our job placement strategy.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    is ApiState.Error -> {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                            ),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Warning, contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(20.dp))
                                Spacer(Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Could not load data", fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.error,
                                        style = MaterialTheme.typography.bodySmall)
                                    Text(state.message, style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                TextButton(onClick = { viewModel.fetchMalaysiaStats() }) {
                                    Text("Retry")
                                }
                            }
                        }
                    }

                    is ApiState.Idle -> { /* nothing shown yet */ }
                }
            }

            Spacer(Modifier.height(20.dp))

            // ── Menu Items ────────────────────────────────────────────────────
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Text("ACCOUNT", style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(bottom = 8.dp))
                ProfileMenuItem(icon = Icons.Default.Person, title = "Personal Information",
                    subtitle = "Muhammad Zahin · A208464")
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                ProfileMenuItem(icon = Icons.Default.Edit, title = "Education & Skills",
                    subtitle = "Add qualifications to stand out")
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                ProfileMenuItem(icon = Icons.Default.Notifications, title = "Job Alerts",
                    subtitle = "Get notified about new B40-friendly jobs")

                Spacer(Modifier.height(20.dp))
                Text("SDG MISSION", style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(bottom = 8.dp))
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF1B5E20).copy(alpha = 0.07f)
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, contentDescription = null,
                                tint = Color(0xFF388E3C), modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Your SDG 1 Impact", fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleSmall, color = Color(0xFF2E7D32))
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "You have submitted ${applications.size} application(s). " +
                                    "Every job application brings Malaysia one step closer to eliminating poverty through employment.",
                            style = MaterialTheme.typography.bodySmall, color = Color(0xFF2E7D32)
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))
                Text("SETTINGS", style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(bottom = 8.dp))
                ProfileMenuItem(icon = Icons.Default.Settings, title = "Account Settings")
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                ProfileMenuItem(icon = Icons.AutoMirrored.Filled.ExitToApp,
                    title = "Logout", tint = Color.Red)
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun CountryStatRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold)
    }
    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
}

@Composable
fun ProfileStatCard(modifier: Modifier = Modifier, label: String, value: String) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
            Text(label, style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
