package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.FileDownloadOff
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SdStorage
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.ContentItem
import com.example.ui.components.EmptyStateView
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.MidnightBackground
import com.example.ui.theme.MidnightCardBorder
import com.example.ui.theme.MidnightSurface
import com.example.ui.theme.MidnightSurfaceElevated
import com.example.ui.theme.MidnightSurfaceVariant
import com.example.ui.theme.SoftCyan
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.CineVaultViewModel

@Composable
fun DownloadsScreen(
    viewModel: CineVaultViewModel,
    onPlayContent: (ContentItem) -> Unit,
    onNavigateHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    val downloads by viewModel.downloads.collectAsState()
    val allContent by viewModel.allContent.collectAsState()

    val totalBytes = downloads.sumOf { it.sizeBytes }
    val totalGB = totalBytes.toDouble() / (1024.0 * 1024.0 * 1024.0)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MidnightBackground)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Downloads",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    ),
                    color = Color.White
                )
                Text(
                    text = "Offline Vault • Ready to watch without internet",
                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                )
            }

            if (downloads.isNotEmpty()) {
                Text(
                    text = "Clear All",
                    style = MaterialTheme.typography.labelSmall.copy(color = SoftCyan),
                    modifier = Modifier.clickable { viewModel.clearAllDownloads() }
                )
            }
        }

        // Storage Usage Bar Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            colors = CardDefaults.cardColors(containerColor = MidnightSurfaceVariant),
            shape = RoundedCornerShape(10.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.SdStorage, contentDescription = null, tint = SoftCyan, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Storage Used by CineVault",
                            style = MaterialTheme.typography.titleMedium.copy(fontSize = 12.sp, fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }
                    Text(
                        text = String.format("%.2f GB / 64.0 GB", totalGB),
                        style = MaterialTheme.typography.bodySmall.copy(color = SoftCyan, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { (totalGB / 64.0).toFloat().coerceIn(0.01f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = ElectricViolet,
                    trackColor = MidnightSurfaceElevated
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (downloads.isEmpty()) {
            EmptyStateView(
                icon = Icons.Default.FileDownloadOff,
                title = "No Offline Downloads",
                description = "Download your favorite movies and series to watch anytime on planes, trains, or off the grid.",
                actionLabel = "Explore Titles to Download",
                onActionClick = onNavigateHome
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(downloads, key = { it.id }) { dl ->
                    val contentItem = allContent.find { it.id == dl.contentId }
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .clickable {
                                if (contentItem != null) onPlayContent(contentItem)
                            },
                        colors = CardDefaults.cardColors(containerColor = MidnightSurfaceVariant),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(0.5.dp, MidnightCardBorder)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Thumbnail
                            Box(
                                modifier = Modifier
                                    .width(80.dp)
                                    .height(55.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(MidnightSurfaceElevated)
                            ) {
                                AsyncImage(
                                    model = dl.backdropUrl,
                                    contentDescription = dl.title,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .align(Alignment.Center)
                                        .background(Color(0xAA000000), androidx.compose.foundation.shape.CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = "Play Download",
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = dl.title,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 13.sp),
                                    color = Color.White,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                if (dl.episodeTitle != null) {
                                    Text(
                                        text = "S${dl.seasonNumber}:E${dl.episodeNumber} • ${dl.episodeTitle}",
                                        style = MaterialTheme.typography.bodySmall.copy(color = SoftCyan, fontSize = 11.sp),
                                        maxLines = 1
                                    )
                                }
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(Icons.Default.DownloadDone, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(12.dp))
                                    Text(
                                        text = "${dl.quality} • ${(dl.sizeBytes / (1024 * 1024))} MB",
                                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 10.5.sp)
                                    )
                                }
                            }

                            IconButton(
                                onClick = { viewModel.deleteDownload(dl.id) },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DeleteOutline,
                                    contentDescription = "Delete Download",
                                    tint = TextSecondary,
                                    modifier = Modifier.size(20.dp)
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
    }
}
