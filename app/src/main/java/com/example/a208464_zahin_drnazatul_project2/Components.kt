package com.example.a208464_zahin_drnazatul_project2

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlin.text.category

// ── Bottom Navigation Bar ────────────────────────────────────────────────────
@Composable
fun JobBottomNav(
    current: String,
    onHome: () -> Unit,
    onActivity: () -> Unit,
    onProfile: () -> Unit
) {
    NavigationBar {
        NavigationBarItem(
            selected = current == "home",
            onClick = onHome,
            label = { Text("Home") },
            icon = { Icon(Icons.Default.Home, contentDescription = null) }
        )
        NavigationBarItem(
            selected = current == "activity",
            onClick = onActivity,
            label = { Text("Activity") },
            icon = { Icon(Icons.Default.Notifications, contentDescription = null) }
        )
        NavigationBarItem(
            selected = current == "profile",
            onClick = onProfile,
            label = { Text("Profile") },
            icon = { Icon(Icons.Default.Person, contentDescription = null) }
        )
    }
}

// ── Search Bar ───────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobSearchBar(
    text: String,
    onValueChange: (String) -> Unit,
    onSearch: () -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        OutlinedTextField(
            value = text,
            onValueChange = onValueChange,
            modifier = Modifier.weight(1f),
            placeholder = { Text("Search jobs, companies...") },
            shape = RoundedCornerShape(12.dp),
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            singleLine = true
        )
        Spacer(Modifier.width(8.dp))
        Button(
            onClick = onSearch,
            modifier = Modifier.height(55.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Find")
        }
    }
}

// ── Job Card ─────────────────────────────────────────────────────────────────
@Composable
fun JobItemCard(
    job: JobPost,
    isApplied: Boolean = false,
    onDetailsClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        job.title,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(job.company, color = MaterialTheme.colorScheme.primary)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowUp
                        else Icons.Default.KeyboardArrowDown,
                        contentDescription = null
                    )
                    if (isApplied) {
                        Spacer(Modifier.height(4.dp))
                        Surface(
                            color = Color(0xFF4CAF50).copy(alpha = 0.15f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                "Applied",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF2E7D32),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(10.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    job.location,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.width(10.dp))
                Box(
                    modifier = Modifier
                        .size(3.dp)
                        .background(MaterialTheme.colorScheme.outline, CircleShape)
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    job.salary,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(Modifier.height(8.dp))
            Surface(
                color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    job.category,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }

            if (expanded) {
                Spacer(Modifier.height(14.dp))
                Text(
                    job.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = onDetailsClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("View Details & Apply")
                }
            }
        }
    }
}

// ── Detail Info Chip ─────────────────────────────────────────────────────────
@Composable
fun DetailInfoChip(icon: ImageVector, text: String) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.width(6.dp))
            Text(text, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
        }
    }
}

// ── Profile Menu Item ────────────────────────────────────────────────────────
@Composable
fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String = "",
    tint: Color = MaterialTheme.colorScheme.onSurface,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(40.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(20.dp))
            }
        }
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.Medium, color = tint)
            if (subtitle.isNotEmpty()) {
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Icon(
            Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = Color.LightGray
        )
    }
}

// ── SDG Badge ────────────────────────────────────────────────────────────────
@Composable
fun SdgBadge() {
    Surface(
        color = Color(0xFFE8F5E9),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, Color(0xFF4CAF50).copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Star,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = Color(0xFF388E3C)
            )
            Spacer(Modifier.width(5.dp))
            Text(
                "SDG 1 · No Poverty",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF2E7D32),
                fontWeight = FontWeight.Bold
            )
        }
    }
}