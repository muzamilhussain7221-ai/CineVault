package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.QueryBuilder
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ContentType
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.MidnightBackground
import com.example.ui.theme.MidnightCardBorder
import com.example.ui.theme.MidnightSurfaceElevated
import com.example.ui.theme.MidnightSurfaceVariant
import com.example.ui.theme.SoftCyan
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.CineVaultViewModel

@Composable
fun AdminScreen(
    viewModel: CineVaultViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }
    val allContent by viewModel.allContent.collectAsState()

    var isAuthenticated by remember { mutableStateOf(true) } // Authenticated for primary demo
    var showAddDialog by remember { mutableStateOf(false) }

    if (showAddDialog) {
        var title by remember { mutableStateOf("") }
        var synopsis by remember { mutableStateOf("") }
        var type by remember { mutableStateOf(ContentType.MOVIE) }
        var year by remember { mutableStateOf("2026") }
        var genres by remember { mutableStateOf("Sci-Fi, Action") }
        var duration by remember { mutableStateOf("115") }
        var isFeatured by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            containerColor = MidnightSurfaceElevated,
            title = { Text("Publish New Title", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Title", color = TextSecondary) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = synopsis,
                            onValueChange = { synopsis = it },
                            label = { Text("Synopsis", color = TextSecondary) },
                            modifier = Modifier.fillMaxWidth().height(90.dp)
                        )
                    }
                    item {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Type: ", color = TextSecondary, fontSize = 12.sp)
                            RadioButton(
                                selected = type == ContentType.MOVIE,
                                onClick = { type = ContentType.MOVIE },
                                colors = RadioButtonDefaults.colors(selectedColor = ElectricViolet)
                            )
                            Text("Movie", color = Color.White, fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            RadioButton(
                                selected = type == ContentType.SERIES,
                                onClick = { type = ContentType.SERIES },
                                colors = RadioButtonDefaults.colors(selectedColor = ElectricViolet)
                            )
                            Text("Series", color = Color.White, fontSize = 12.sp)
                        }
                    }
                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = year,
                                onValueChange = { year = it },
                                label = { Text("Year", color = TextSecondary) },
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = duration,
                                onValueChange = { duration = it },
                                label = { Text("Duration (m)", color = TextSecondary) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    item {
                        OutlinedTextField(
                            value = genres,
                            onValueChange = { genres = it },
                            label = { Text("Genres (comma separated)", color = TextSecondary) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = isFeatured,
                                onCheckedChange = { isFeatured = it },
                                colors = CheckboxDefaults.colors(checkedColor = SoftCyan)
                            )
                            Text("Feature in Home Hero Carousel", color = Color.White, fontSize = 12.sp)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            viewModel.addAdminContent(
                                title = title.trim(),
                                synopsis = synopsis.trim().ifEmpty { "CineVault Original Title" },
                                type = type,
                                releaseYear = year.toIntOrNull() ?: 2026,
                                genres = genres.split(",").map { it.trim() }.filter { it.isNotEmpty() },
                                durationMinutes = duration.toIntOrNull() ?: 110,
                                totalSeasons = if (type == ContentType.SERIES) 1 else 0,
                                rating = 8.5f,
                                posterUrl = "https://images.unsplash.com/photo-1578632767115-351597cf2477?w=800",
                                backdropUrl = "https://images.unsplash.com/photo-1508739773434-c26b3d09e071?w=1600",
                                isFeatured = isFeatured
                            )
                            showAddDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricViolet),
                    enabled = title.isNotBlank()
                ) {
                    Text("Publish to Catalog")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MidnightBackground),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Top Bar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Admin Portal",
                        style = MaterialTheme.typography.headlineLarge.copy(fontSize = 20.sp, fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }

                Button(
                    onClick = { showAddDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricViolet),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Title", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Platform Analytics KPIs
        item {
            Text(
                text = "Platform Analytics & Telemetry",
                style = MaterialTheme.typography.labelLarge.copy(color = TextSecondary, fontWeight = FontWeight.Bold)
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AdminKpiCard(
                    icon = Icons.Default.People,
                    title = "Total Users",
                    value = "1.42M",
                    subtitle = "+8.4% this month",
                    modifier = Modifier.weight(1f)
                )
                AdminKpiCard(
                    icon = Icons.Default.TrendingUp,
                    title = "Active Today",
                    value = "382.5K",
                    subtitle = "Peak concurrency",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AdminKpiCard(
                    icon = Icons.Default.QueryBuilder,
                    title = "Streamed Time",
                    value = "4.8M hrs",
                    subtitle = "Average 92m/user",
                    modifier = Modifier.weight(1f)
                )
                AdminKpiCard(
                    icon = Icons.Default.Analytics,
                    title = "Active ARR",
                    value = "$5.2M",
                    subtitle = "Premium 4K 64%",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Content Management Table
        item {
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Catalog Content (${allContent.size})",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
                Text(
                    text = "Live Sync to Room DB",
                    style = MaterialTheme.typography.labelSmall.copy(color = SoftCyan)
                )
            }
        }

        items(allContent, key = { it.id }) { item ->
            Card(
                colors = CardDefaults.cardColors(containerColor = MidnightSurfaceVariant),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = item.title,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.5.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(MidnightSurfaceElevated)
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = if (item.type == ContentType.MOVIE) "MOVIE" else "SERIES",
                                    color = SoftCyan,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${item.releaseYear} • ${item.genres.take(2).joinToString(", ")} • ⭐ ${item.rating}",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }

                    IconButton(
                        onClick = { viewModel.deleteAdminContent(item.id) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete item",
                            tint = Color(0xFFEF4444),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(70.dp))
        }
    }
}

@Composable
private fun AdminKpiCard(
    icon: ImageVector,
    title: String,
    value: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MidnightSurfaceVariant),
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(0.5.dp, MidnightCardBorder)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = title, color = TextSecondary, fontSize = 11.sp)
                Icon(imageVector = icon, contentDescription = null, tint = SoftCyan, modifier = Modifier.size(16.dp))
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, color = Color.White, fontWeight = FontWeight.Black, fontSize = 20.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = subtitle, color = ElectricViolet, fontSize = 9.5.sp)
        }
    }
}
